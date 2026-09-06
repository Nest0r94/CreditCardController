package com.example.creditcardcontroller.data.local.resumen

import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.ui.util.fechaDesdeDia
import com.example.creditcardcontroller.ui.util.periodoResumen
import java.time.Instant
import java.time.ZoneId

fun expandirEnCuotas(movimiento: MovimientoEntity, diaCierre: Int?): List<MovimientoEntity> {
    val n = movimiento.cantidadCuotas.coerceAtLeast(1)
    val total = movimiento.monto

    if (n <= 1) {
        return listOf(movimiento.copy(numeroCuota = 0))
    }

    val montoBase = redondear(total / n)
    val diaOriginal = Instant.ofEpochMilli(movimiento.fecha)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .dayOfMonth
    val base = periodoResumen(movimiento.fecha, diaCierre)
    val dia = if (diaCierre != null && diaCierre in 1..31) minOf(diaOriginal, diaCierre) else diaOriginal

    return (1..n).map { k ->
        val fecha = if (k == 1) {
            movimiento.fecha
        } else {
            fechaDesdeDia(dia, base.plusMonths((k - 1).toLong()))
        }
        movimiento.copy(
            descripcion = "${movimiento.descripcion} (cuota $k/$n)",
            monto = if (k == n) redondear(total - montoBase * (n - 1)) else montoBase,
            cantidadCuotas = n,
            numeroCuota = k,
            fecha = fecha,
            hora = if (k == 1) movimiento.hora else null,
            montoReintegrable = if (k == 1) movimiento.montoReintegrable else 0.0
        )
    }
}

private fun redondear(valor: Double): Double =
    Math.round(valor * 100.0) / 100.0