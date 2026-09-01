package com.example.creditcardcontroller.ui.screens.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.dao.CategoriaDao
import com.example.creditcardcontroller.data.local.dao.MovimientoDao
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.dao.TarjetaDao
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

data class BalancesUiState(
    val tarjetas: List<TarjetaEntity> = emptyList(),
    val movimientos: List<MovimientoEntity> = emptyList(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val selectedTarjetaId: Long? = null,
    val selectedDate: YearMonth = YearMonth.now(),
    val totalPresupuesto: Double = 0.0,
    val gastoMensual: Double = 0.0,
    val gastoCuotas: Double = 0.0,
    val gastoUnPago: Double = 0.0,
    val limiteCuotas: Double = 0.0,
    val limiteUnPago: Double = 0.0
)

class BalancesViewModel(
    private val tarjetaDao: TarjetaDao,
    private val movimientoDao: MovimientoDao,
    private val categoriaDao: CategoriaDao,
    private val presupuestoDao: PresupuestoDao
) : ViewModel() {

    private val _selectedTarjetaId = MutableStateFlow<Long?>(null)
    private val _selectedDate = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<BalancesUiState> = combine(
        tarjetaDao.getAllTarjetas(),
        movimientoDao.getAllMovements(),
        categoriaDao.getAllCategorias(),
        presupuestoDao.getAllItems(),
        combine(_selectedTarjetaId, _selectedDate) { id, date -> id to date }
    ) { allTarjetas, movimientos, categorias, presupuestos, selectedInfo ->
        val (selectedId, selectedDate) = selectedInfo
        
        val tarjetas = allTarjetas.filter { it.tipo == TipoMedioPago.CREDITO }
        val tarjetasIdsPermitidos = tarjetas.map { it.id }.toSet()

        val startOfMonth = selectedDate.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = selectedDate.atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val movimientosMes = movimientos.filter { 
            it.fecha in startOfMonth..endOfMonth && it.tarjetaId in tarjetasIdsPermitidos 
        }
        
        val totalGasto = movimientosMes.sumOf { it.monto }
        val gastoCuotas = movimientosMes.filter { it.esCuotas }.sumOf { it.monto / it.cantidadCuotas }
        val gastoUnPago = movimientosMes.filter { !it.esCuotas }.sumOf { it.monto }

        val totalPresupuesto = presupuestos.filter { 
            it.mes == selectedDate.monthValue && 
            it.anio == selectedDate.year && 
            it.tipo == PresupuestoEntity.TIPO_INGRESO 
        }.sumOf { it.monto }

        val itemsPresupuesto = presupuestos.filter {
            it.mes == selectedDate.monthValue &&
            it.anio == selectedDate.year
        }

        val limiteUnPago = itemsPresupuesto.find { 
            it.tipo == PresupuestoEntity.TIPO_LIMITE && it.titulo.contains("1 cuota", ignoreCase = true) 
        }?.monto ?: 0.0

        val limiteCuotas = itemsPresupuesto.find { 
            it.tipo == PresupuestoEntity.TIPO_LIMITE && it.titulo.contains("mensual", ignoreCase = true) 
        }?.monto ?: 0.0

        val filteredMovimientos = if (selectedId == null) {
            movimientosMes
        } else {
            movimientosMes.filter { it.tarjetaId == selectedId }
        }

        BalancesUiState(
            tarjetas = tarjetas,
            movimientos = filteredMovimientos,
            categorias = categorias,
            selectedTarjetaId = selectedId,
            selectedDate = selectedDate,
            totalPresupuesto = totalPresupuesto,
            gastoMensual = totalGasto,
            gastoCuotas = gastoCuotas,
            gastoUnPago = gastoUnPago,
            limiteCuotas = limiteCuotas,
            limiteUnPago = limiteUnPago
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BalancesUiState()
    )

    fun selectTarjeta(id: Long?) {
        _selectedTarjetaId.value = id
    }

    fun updateSelectedDate(date: YearMonth) {
        _selectedDate.value = date
    }

    class Factory(
        private val tarjetaDao: TarjetaDao,
        private val movimientoDao: MovimientoDao,
        private val categoriaDao: CategoriaDao,
        private val presupuestoDao: PresupuestoDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BalancesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BalancesViewModel(tarjetaDao, movimientoDao, categoriaDao, presupuestoDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
