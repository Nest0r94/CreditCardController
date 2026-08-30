package com.example.creditcardcontroller.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.creditcardcontroller.data.local.TipoMedioPago

@Entity(tableName = "tarjetas")
data class TarjetaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val tipo: TipoMedioPago = TipoMedioPago.CREDITO,
    val limiteMensual: Double? = null,
    val limiteCuotas: Double? = null,
    val diaCierreResumen: Int? = null,
    val diaVencimientoResumen: Int? = null,
    val vencimientoTarjeta: Long = 0L
)
