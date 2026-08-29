package com.example.creditcardcontroller.data.local.backup

import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity

data class BackupData(
    val version: Int = 1,
    val fecha: Long = System.currentTimeMillis(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val tarjetas: List<TarjetaEntity> = emptyList(),
    val descuentos: List<DescuentoEntity> = emptyList(),
    val movimientos: List<MovimientoEntity> = emptyList(),
    val presupuesto: List<PresupuestoEntity> = emptyList()
)
