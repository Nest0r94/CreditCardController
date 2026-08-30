package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.ResumenEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResumenDao {
    @Query("SELECT * FROM resumenes WHERE tarjetaId = :tarjetaId ORDER BY periodo DESC")
    fun getByTarjeta(tarjetaId: Long): Flow<List<ResumenEntity>>

    @Query("SELECT * FROM resumenes ORDER BY periodo DESC")
    fun getAll(): Flow<List<ResumenEntity>>

    @Query("SELECT * FROM resumenes ORDER BY periodo DESC")
    suspend fun getAllSync(): List<ResumenEntity>

    @Query("SELECT * FROM resumenes WHERE tarjetaId = :tarjetaId ORDER BY periodo DESC")
    suspend fun getByTarjetaSync(tarjetaId: Long): List<ResumenEntity>

    @Query("SELECT * FROM resumenes WHERE tarjetaId = :tarjetaId AND periodo = :periodo LIMIT 1")
    suspend fun getByPeriodo(tarjetaId: Long, periodo: String): ResumenEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(resumen: ResumenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(resumenes: List<ResumenEntity>)

    @Update
    suspend fun update(resumen: ResumenEntity)

    @Delete
    suspend fun delete(resumen: ResumenEntity)

    @Query("DELETE FROM resumenes")
    suspend fun clearAll()
}