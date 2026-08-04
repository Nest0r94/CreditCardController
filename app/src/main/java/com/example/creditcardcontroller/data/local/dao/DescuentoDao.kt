package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DescuentoDao {
    @Query("SELECT * FROM descuentos ORDER BY nombre ASC")
    fun getAllDescuentos(): Flow<List<DescuentoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(descuento: DescuentoEntity)

    @Update
    suspend fun update(descuento: DescuentoEntity)

    @Delete
    suspend fun delete(descuento: DescuentoEntity)

    @Query("SELECT * FROM descuentos WHERE id = :id")
    suspend fun getById(id: Long): DescuentoEntity?
}
