package com.example.creditcardcontroller.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarjetas")
data class TarjetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val limiteMensual: Double,
    val limiteCuotas: Double,
    val fechaCierreResumen: Long,
    val fechaVencimientoResumen: Long,
    val vencimientoTarjeta: Long
)
