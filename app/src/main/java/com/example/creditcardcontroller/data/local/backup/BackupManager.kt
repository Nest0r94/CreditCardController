package com.example.creditcardcontroller.data.local.backup

import android.content.Context
import androidx.room.withTransaction
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.resumen.ResumenGenerator
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupManager(private val db: AppDatabase) {

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    suspend fun exportarJson(): String = withContext(Dispatchers.IO) {
        val data = BackupData(
            categorias = db.categoriaDao().getAllSync(),
            tarjetas = db.tarjetaDao().getAllSync(),
            descuentos = db.descuentoDao().getAllSync(),
            movimientos = db.movimientoDao().getAllSync(),
            presupuesto = db.presupuestoDao().getAllItemsSync(),
            resumenes = db.resumenDao().getAllSync()
        )
        gson.toJson(data)
    }

    suspend fun restaurarJson(json: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val data = gson.fromJson(json, BackupData::class.java) ?: return@withContext false

            db.withTransaction {
                db.categoriaDao().clearAll()
                db.tarjetaDao().clearAll()
                db.descuentoDao().clearAll()
                db.movimientoDao().clearAll()
                db.presupuestoDao().clearAll()
                db.resumenDao().clearAll()

                if (data.categorias.isNotEmpty()) {
                    db.categoriaDao().insertAll(data.categorias)
                }
                if (data.tarjetas.isNotEmpty()) {
                    db.tarjetaDao().insertAll(data.tarjetas)
                }
                if (data.descuentos.isNotEmpty()) {
                    db.descuentoDao().insertAll(data.descuentos)
                }
                if (data.movimientos.isNotEmpty()) {
                    // Si el backup es de una versión vieja, aseguramos que el tipo sea GASTO
                    val movimientosProcesados = data.movimientos.map { mov ->
                        // Si por algún motivo el tipo viene nulo desde GSON por ser backup viejo
                        @Suppress("SENSELESS_COMPARISON")
                        val conTipo = if (mov.tipo == null) {
                            mov.copy(tipo = com.example.creditcardcontroller.data.local.TipoMovimiento.GASTO)
                        } else {
                            mov
                        }
                        // Los backups anteriores no tienen fechaPresentacion: Gson la
                        // deserializa como 0, por lo que se conserva su fecha de compra.
                        if (conTipo.fechaPresentacion == 0L) conTipo.copy(fechaPresentacion = conTipo.fecha) else conTipo
                    }
                    db.movimientoDao().insertAll(movimientosProcesados)
                }
                if (data.presupuesto.isNotEmpty()) {
                    db.presupuestoDao().insertAll(data.presupuesto)
                }
                if (data.resumenes.isNotEmpty()) {
                    db.resumenDao().insertAll(data.resumenes)
                }
            }
            ResumenGenerator(db).generarResumenesPendientes()
            true
        } catch (e: Exception) {
            false
        }
    }
}
