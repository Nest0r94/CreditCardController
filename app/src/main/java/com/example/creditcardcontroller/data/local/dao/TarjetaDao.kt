package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TarjetaDao {
    @Query("SELECT * FROM tarjetas ORDER BY nombre ASC")
    fun getAllTarjetas(): Flow<List<TarjetaEntity>>

    @Query("SELECT * FROM tarjetas ORDER BY nombre ASC")
    suspend fun getAllSync(): List<TarjetaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarjeta: TarjetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tarjetas: List<TarjetaEntity>)

    @Update
    suspend fun update(tarjeta: TarjetaEntity)

    @Delete
    suspend fun delete(tarjeta: TarjetaEntity)

    @Query("SELECT * FROM tarjetas WHERE id = :id")
    suspend fun getById(id: Long): TarjetaEntity?

    @Query("SELECT COUNT(*) FROM tarjetas")
    suspend fun count(): Int

    @Query("DELETE FROM tarjetas")
    suspend fun clearAll()
}
