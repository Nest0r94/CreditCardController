package com.example.creditcardcontroller.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun getAllCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: CategoriaEntity)

    @Update
    suspend fun update(categoria: CategoriaEntity)

    @Delete
    suspend fun delete(categoria: CategoriaEntity)

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun getById(id: Long): CategoriaEntity?
}
