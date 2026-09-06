package com.example.creditcardcontroller.data.local.resumen

import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.ResumenEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.util.fechaDesdeDia
import com.example.creditcardcontroller.ui.util.periodoResumen
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class ResumenGenerator(private val db: AppDatabase) {

    suspend fun generarResumenesPendientes() {
        db.tarjetaDao().getAllSync()
            .filter { it.tipo == TipoMedioPago.CREDITO && esElegible(it) }
            .forEach { generarParaTarjeta(it) }
    }

    suspend fun recalcular(tarjetaId: Long) {
        val tarjeta = db.tarjetaDao().getById(tarjetaId) ?: return
        if (tarjeta.tipo != TipoMedioPago.CREDITO || !esElegible(tarjeta)) {
            db.resumenDao().deleteByTarjeta(tarjetaId)
            return
        }
        generarParaTarjeta(tarjeta)
    }

    private fun esElegible(tarjeta: TarjetaEntity): Boolean =
        tarjeta.diaCierreResumen != null && tarjeta.diaVencimientoResumen != null

    private suspend fun generarParaTarjeta(tarjeta: TarjetaEntity) {
        val diaCierre = tarjeta.diaCierreResumen ?: return
        val diaVencimiento = tarjeta.diaVencimientoResumen ?: return
        val movimientos = db.movimientoDao().getAllSync().filter { it.tarjetaId == tarjeta.id }

        val existentes = db.resumenDao().getByTarjetaSync(tarjeta.id).associateBy { it.periodo }

        val hoy = LocalDate.now()
        val hoyInicio = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val ultimoPeriodo = if (fechaDesdeDia(diaCierre, YearMonth.from(hoy)) <= hoyInicio) {
            YearMonth.from(hoy)
        } else {
            YearMonth.from(hoy).minusMonths(1)
        }

        val primerPeriodo = movimientos.minOfOrNull { toYearMonth(it.fecha) }

        if (primerPeriodo != null) {
            var periodo: YearMonth = primerPeriodo
            while (!periodo.isAfter(ultimoPeriodo)) {
                upsert(tarjeta.id, periodo, diaCierre, diaVencimiento, movimientos, existentes[periodoDe(periodo)])
                periodo = periodo.plusMonths(1)
            }
        }

        val rangoCubierto = primerPeriodo != null
        existentes.forEach { (periodoStr, resumen) ->
            val p = YearMonth.parse(periodoStr)
            val fueraDelRango = !rangoCubierto || p.isBefore(primerPeriodo) || p.isAfter(ultimoPeriodo)
            if (fueraDelRango) {
                val total = calcularTotal(movimientos, diaCierre, p)
                if (resumen.total != total) {
                    db.resumenDao().update(resumen.copy(total = total))
                }
            }
        }
    }

    private suspend fun upsert(
        tarjetaId: Long,
        periodo: YearMonth,
        diaCierre: Int,
        diaVencimiento: Int,
        movimientos: List<MovimientoEntity>,
        existente: ResumenEntity?
    ) {
        val total = calcularTotal(movimientos, diaCierre, periodo)
        if (existente != null) {
            if (existente.total != total) {
                db.resumenDao().update(existente.copy(total = total))
            }
        } else {
            db.resumenDao().insert(
                ResumenEntity(
                    tarjetaId = tarjetaId,
                    periodo = periodoDe(periodo),
                    fechaCierre = fechaDesdeDia(diaCierre, periodo),
                    fechaVencimiento = fechaDesdeDia(diaVencimiento, periodo.plusMonths(1)),
                    total = total,
                    pagado = false
                )
            )
        }
    }

    private fun calcularTotal(movimientos: List<MovimientoEntity>, diaCierre: Int, periodo: YearMonth): Double {
        var total = 0.0
        for (m in movimientos) {
            if (periodo == periodoResumen(m.fecha, diaCierre)) total += m.monto
        }
        return total
    }

    private fun toYearMonth(millis: Long): YearMonth =
        YearMonth.from(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate())

    private fun periodoDe(periodo: YearMonth): String = "%04d-%02d".format(periodo.year, periodo.monthValue)
}