package com.example.creditcardcontroller.data.local.dao

import androidx.room.*
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY id ASC")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: CategoriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categorias: List<CategoriaEntity>)

    @Query("SELECT COUNT(*) FROM categorias")
    suspend fun count(): Int

    @Update
    suspend fun update(categoria: CategoriaEntity)

    @Delete
    suspend fun delete(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getById(id: Long): CategoriaEntity?
}
