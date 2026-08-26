package com.example.creditcardcontroller.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.YearMonthTuple
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {
    @Query("SELECT * FROM presupuesto_items WHERE mes = :mes AND anio = :anio")
    fun getItemsByMonth(mes: Int, anio: Int): Flow<List<PresupuestoEntity>>

    @Query("SELECT * FROM presupuesto_items WHERE mes = :mes AND anio = :anio")
    suspend fun getItemsByMonthSync(mes: Int, anio: Int): List<PresupuestoEntity>

    @Query("SELECT * FROM presupuesto_items ORDER BY anio DESC, mes DESC LIMIT 1")
    suspend fun getLastMonthWithData(): PresupuestoEntity?

    @Query("SELECT DISTINCT mes, anio FROM presupuesto_items ORDER BY anio ASC, mes ASC")
    fun getAvailableMonths(): Flow<List<YearMonthTuple>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PresupuestoEntity): Long

    @Update
    suspend fun update(item: PresupuestoEntity)

    @Delete
    suspend fun delete(item: PresupuestoEntity)

    @Query("DELETE FROM presupuesto_items WHERE mes = :mes AND anio = :anio")
    suspend fun deleteMonth(mes: Int, anio: Int)
}
