package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    fun getAllMovements(): Flow<List<MovimientoEntity>>

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC")
    suspend fun getAllSync(): List<MovimientoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movimiento: MovimientoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movimientos: List<MovimientoEntity>)

    @Update
    suspend fun update(movimiento: MovimientoEntity)

    @Delete
    suspend fun delete(movimiento: MovimientoEntity)

    @Query("SELECT * FROM movimientos WHERE id = :id")
    suspend fun getById(id: Long): MovimientoEntity?

    @Query("DELETE FROM movimientos")
    suspend fun clearAll()
}
