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
    val tipo: String, // "INGRESO", "GASTO", "LIMITE" o "AHORRO"
    val icono: String,
    val color: String,
    val tarjetaId: Long? = null // Solo para GASTO: null = Cuenta, si no id de la tarjeta
) {
    companion object {
        const val TIPO_INGRESO = "INGRESO"
        const val TIPO_GASTO = "GASTO"
        const val TIPO_LIMITE = "LIMITE"
        const val TIPO_AHORRO = "AHORRO"
    }
}

data class YearMonthTuple(
    val mes: Int,
    val anio: Int
)
