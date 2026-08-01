package com.example.creditcardcontroller.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "descuentos")
data class DescuentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val porcentajeDescuento: Double,
    val montoTope: Double,
    val cantidadLimitePagos: Int,
    val frecuencia: Frecuencia,
    val fechaVencimiento: Long,
    val diasHabiles: List<Int>, // 1-7
    val tarjetasAplicables: List<Long>, // IDs de tarjetas
    val tipoDescuento: TipoDescuento
)
