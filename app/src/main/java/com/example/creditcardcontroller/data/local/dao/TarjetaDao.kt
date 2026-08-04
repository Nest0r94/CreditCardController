package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TarjetaDao {
    @Query("SELECT * FROM tarjetas ORDER BY nombre ASC")
    fun getAllTarjetas(): Flow<List<TarjetaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tarjeta: TarjetaEntity)

    @Update
    suspend fun update(tarjeta: TarjetaEntity)

    @Delete
    suspend fun delete(tarjeta: TarjetaEntity)

    @Query("SELECT * FROM tarjetas WHERE id = :id")
    suspend fun getById(id: Long): TarjetaEntity?
}
