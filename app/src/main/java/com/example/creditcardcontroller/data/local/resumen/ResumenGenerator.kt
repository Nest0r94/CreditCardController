package com.example.creditcardcontroller.data.local.resumen

import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.TipoMovimiento
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.ResumenEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.util.fechaDesdeDia
import com.example.creditcardcontroller.ui.util.periodoDe
import com.example.creditcardcontroller.ui.util.periodoVencimientoResumen
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset

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

        val hoy = YearMonth.now()

        // Ancla del primer resumen: el existente más antiguo, o la fecha de vencimiento
        // elegida al crear la tarjeta, o el primer movimiento, o el mes actual.
        val inicioPeriodo = existentes.keys.minOrNull()?.let { YearMonth.parse(it) }
            ?: tarjeta.primerVencimientoResumen?.let { toYearMonthUtc(it) }
            ?: movimientos.minOfOrNull { periodoVencimientoResumen(it.fecha, diaCierre) }
            ?: hoy

        val primerPeriodo = minOf(inicioPeriodo, hoy)

        // El resumen pendiente actual es el siguiente al último cuyo vencimiento ya pasó.
        val base = primerPeriodo.minusMonths(1)
        val ultimoVencido = generateSequence(base) { it.plusMonths(1) }
            .takeWhile { fechaDesdeDia(diaVencimiento, it) <= hoyInicio() }
            .lastOrNull() ?: base
        val siguientePendiente = ultimoVencido.plusMonths(1)

        // Las compras en cuotas pueden requerir resúmenes futuros.
        val ultimoMovimiento = movimientos.maxOfOrNull { periodoVencimientoResumen(it.fecha, diaCierre) }

        val maxResumenExistente = existentes.keys.maxOrNull()?.let { YearMonth.parse(it) }
        val ultimoPeriodo = maxOf(siguientePendiente, ultimoMovimiento ?: primerPeriodo, primerPeriodo, hoy, maxResumenExistente ?: hoy)

        var periodo = primerPeriodo
        while (!periodo.isAfter(ultimoPeriodo)) {
            upsert(tarjeta.id, periodo, diaCierre, diaVencimiento, movimientos, existentes[periodoDe(periodo)])
            inicializarPresupuestoSiNoExiste(periodo)
            periodo = periodo.plusMonths(1)
        }

        // Se conservan los resúmenes fuera del rango: solo se actualiza su total.
        existentes.forEach { (periodoStr, resumen) ->
            val p = YearMonth.parse(periodoStr)
            if (p.isBefore(primerPeriodo) || p.isAfter(ultimoPeriodo)) {
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
                    fechaCierre = fechaDesdeDia(diaCierre, periodo.minusMonths(1)),
                    fechaVencimiento = fechaDesdeDia(diaVencimiento, periodo),
                    total = total,
                    pagado = false
                )
            )
        }
    }

    private fun calcularTotal(movimientos: List<MovimientoEntity>, diaCierre: Int, periodo: YearMonth): Double {
        var total = 0.0
        for (m in movimientos) {
            if (periodo == periodoVencimientoResumen(m.fecha, diaCierre)) {
                when (m.tipo) {
                    TipoMovimiento.GASTO -> total += m.monto
                    TipoMovimiento.INGRESO -> total -= m.monto
                    TipoMovimiento.REINTEGRO -> { /* Por ahora no suma ni resta según instrucción */ }
                }
            }
        }
        return total
    }

    private fun hoyInicio(): Long =
        LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    private suspend fun inicializarPresupuestoSiNoExiste(periodo: YearMonth) {
        val currentItems = db.presupuestoDao().getItemsByMonthSync(periodo.monthValue, periodo.year)
        if (currentItems.isNotEmpty()) return

        val lastMonth = db.presupuestoDao().getLastMonthWithData()
        if (lastMonth != null) {
            val itemsToCopy = db.presupuestoDao().getItemsByMonthSync(lastMonth.mes, lastMonth.anio)
            itemsToCopy.forEach { item ->
                db.presupuestoDao().insert(
                    item.copy(id = 0, mes = periodo.monthValue, anio = periodo.year)
                )
            }
        }
    }

    private fun toYearMonthUtc(millis: Long): YearMonth =
        YearMonth.from(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
}
