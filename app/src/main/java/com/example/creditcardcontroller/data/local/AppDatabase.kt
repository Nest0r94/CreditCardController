package com.example.creditcardcontroller.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.creditcardcontroller.data.local.dao.CategoriaDao
import com.example.creditcardcontroller.data.local.dao.DescuentoDao
import com.example.creditcardcontroller.data.local.dao.MovimientoDao
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.dao.ResumenDao
import com.example.creditcardcontroller.data.local.dao.TarjetaDao
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.ResumenEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity

@Database(
    entities = [
        MovimientoEntity::class,
        CategoriaEntity::class,
        TarjetaEntity::class,
        DescuentoEntity::class,
        PresupuestoEntity::class,
        ResumenEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movimientoDao(): MovimientoDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun tarjetaDao(): TarjetaDao
    abstract fun descuentoDao(): DescuentoDao
    abstract fun presupuestoDao(): PresupuestoDao
    abstract fun resumenDao(): ResumenDao

    suspend fun seedCategoriasSiVacia() {
        if (categoriaDao().count() == 0) {
            categoriaDao().insertAll(DefaultCategorias.lista)
        }
    }

    suspend fun seedTarjetaCuentaSiVacia() {
        if (tarjetaDao().count() == 0) {
            tarjetaDao().insert(
                TarjetaEntity(
                    nombre = "Cuenta",
                    tipo = TipoMedioPago.CUENTA
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE movimientos ADD COLUMN presupuestoId INTEGER")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "credit_card_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
