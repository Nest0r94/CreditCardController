package com.example.creditcardcontroller.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presupuesto_items")
data class PresupuestoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mes: Int,
    val anio: Int,
    val titulo: String,
    val monto: Double,
    val tipo: String, // "INGRESO" o "GASTO"
    val icono: String,
    val color: String
)

data class YearMonthTuple(
    val mes: Int,
    val anio: Int
)
