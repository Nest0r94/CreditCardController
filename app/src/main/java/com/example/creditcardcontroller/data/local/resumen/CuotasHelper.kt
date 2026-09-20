package com.example.creditcardcontroller.data.local.resumen

import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.ui.util.fechaDesdeDia
import com.example.creditcardcontroller.ui.util.periodoResumen
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

fun expandirEnCuotas(movimiento: MovimientoEntity, diaCierre: Int?, cuotaInicial: Int = 1): List<MovimientoEntity> {
    val n = movimiento.cantidadCuotas.coerceAtLeast(1)
    val total = movimiento.monto

    if (n <= 1) {
        return listOf(movimiento.copy(numeroCuota = 0))
    }

    val cuotaGroupId = UUID.randomUUID().toString()
    val montoBase = redondear(total / n)
    val diaOriginal = Instant.ofEpochMilli(movimiento.fechaPresentacion)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .dayOfMonth
    val base = periodoResumen(movimiento.fechaPresentacion, diaCierre)
    val dia = if (diaCierre != null && diaCierre in 1..31) minOf(diaOriginal, diaCierre) else diaOriginal

    return (cuotaInicial..n).map { k ->
        val fechaPresentacion = if (k == cuotaInicial) {
            movimiento.fechaPresentacion
        } else {
            // Calculamos el mes objetivo de la cuota basándonos en el período de la primera cuota registrada
            val mesObjetivo = base.plusMonths((k - cuotaInicial).toLong())
            // Para las cuotas futuras, usamos un día seguro (por ejemplo el día 1 del mes objetivo) 
            // de modo que la función periodoResumen devuelva exactamente ese mesObjetivo 
            // y no se desfase si el día de compra original es mayor al día de cierre.
            fechaDesdeDia(if (diaCierre != null) minOf(dia, diaCierre) else dia, mesObjetivo)
        }
        movimiento.copy(
            descripcion = "${movimiento.descripcion} (cuota $k/$n)",
            monto = if (k == n) redondear(total - montoBase * (n - 1)) else montoBase,
            cantidadCuotas = n,
            numeroCuota = k,
            // Todas las cuotas muestran la fecha de compra original. Su período se
            // determina con fechaPresentacion, que sí avanza en cada cuota.
            fechaPresentacion = fechaPresentacion,
            hora = if (k == cuotaInicial) movimiento.hora else null,
            montoReintegrable = if (k == cuotaInicial) movimiento.montoReintegrable else 0.0,
            cuotaGroupId = cuotaGroupId
        )
    }
}

private fun redondear(valor: Double): Double =
    Math.round(valor * 100.0) / 100.0
