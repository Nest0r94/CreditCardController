package com.example.creditcardcontroller.data.local

import java.time.DayOfWeek
import java.time.LocalDate

fun periodoDeFrecuencia(frecuencia: Frecuencia, fecha: LocalDate): ClosedRange<LocalDate> =
    when (frecuencia) {
        Frecuencia.DIARIA -> fecha..fecha
        Frecuencia.SEMANAL -> {
            val lunes = fecha.with(DayOfWeek.MONDAY)
            lunes..lunes.plusDays(6)
        }
        Frecuencia.MENSUAL -> fecha.withDayOfMonth(1)..fecha.withDayOfMonth(fecha.lengthOfMonth())
        Frecuencia.ANUAL -> fecha.withDayOfYear(1)..fecha.withDayOfYear(fecha.lengthOfYear())
    }