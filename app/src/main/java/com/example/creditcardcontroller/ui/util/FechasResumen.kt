package com.example.creditcardcontroller.ui.util

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset

fun fechaDesdeDia(dia: Int, mes: YearMonth = YearMonth.now()): Long {
    val d = dia.coerceIn(1, mes.lengthOfMonth())
    return mes.atDay(d).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun proximaFechaDeDia(dia: Int, hoy: LocalDate = LocalDate.now()): Long =
    proximaFechaDeDia(dia, hoy, ZoneId.systemDefault())

fun proximaFechaDeDiaUtc(dia: Int, hoy: LocalDate = LocalDate.now()): Long =
    proximaFechaDeDia(dia, hoy, ZoneOffset.UTC)

fun diaDeFecha(millis: Long): Int =
    Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).dayOfMonth

private fun proximaFechaDeDia(dia: Int, hoy: LocalDate, zone: ZoneId): Long {
    if (dia !in 1..31) return hoy.atStartOfDay(zone).toInstant().toEpochMilli() // Fallback for invalid dia
    val mesActual = YearMonth.from(hoy)
    var fecha = if (dia <= mesActual.lengthOfMonth()) mesActual.atDay(dia) else mesActual.atEndOfMonth()
    if (fecha.isBefore(hoy)) {
        val mesSiguiente = mesActual.plusMonths(1)
        fecha = if (dia <= mesSiguiente.lengthOfMonth()) mesSiguiente.atDay(dia) else mesSiguiente.atEndOfMonth()
    }
    return fecha.atStartOfDay(zone).toInstant().toEpochMilli()
}

fun periodoResumen(fechaMillis: Long, diaCierre: Int?): YearMonth {
    val mes = YearMonth.from(Instant.ofEpochMilli(fechaMillis).atZone(ZoneId.systemDefault()).toLocalDate())
    if (diaCierre == null || diaCierre !in 1..31) return mes
    return if (fechaMillis <= fechaDesdeDia(diaCierre, mes)) mes else mes.plusMonths(1)
}

fun periodoDe(anio: Int, mes: Int): String = "%04d-%02d".format(anio, mes)

fun periodoDe(mes: YearMonth): String = "%04d-%02d".format(mes.year, mes.monthValue)