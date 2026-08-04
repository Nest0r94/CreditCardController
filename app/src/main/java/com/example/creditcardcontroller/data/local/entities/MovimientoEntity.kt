package com.example.creditcardcontroller.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TarjetaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tarjetaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DescuentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["descuentoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["categoriaId"]),
        Index(value = ["tarjetaId"]),
        Index(value = ["descuentoId"])
    ]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descripcion: String,
    val monto: Double,
    val esCuotas: Boolean,
    val cantidadCuotas: Int,
    val fecha: Long,
    val categoriaId: Long,
    val tarjetaId: Long,
    val descuentoId: Long?,
    val montoReintegrable: Double,
    val montoReintegrado: Boolean
)
