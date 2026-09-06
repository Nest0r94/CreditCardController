package com.example.creditcardcontroller.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resumenes",
    foreignKeys = [
        ForeignKey(
            entity = TarjetaEntity::class,
            parentColumns = ["id"],
            childColumns = ["tarjetaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tarjetaId"]),
        Index(value = ["tarjetaId", "periodo"], unique = true)
    ]
)
data class ResumenEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tarjetaId: Long,
    val periodo: String,
    val fechaCierre: Long,
    val fechaVencimiento: Long,
    val total: Double,
    val pagado: Boolean
)