package com.example.creditcardcontroller.data.local.backup

import android.content.Context
import androidx.room.withTransaction
import com.example.creditcardcontroller.data.local.AppDatabase
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
            presupuesto = db.presupuestoDao().getAllItemsSync()
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
                    db.movimientoDao().insertAll(data.movimientos)
                }
                if (data.presupuesto.isNotEmpty()) {
                    db.presupuestoDao().insertAll(data.presupuesto)
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
