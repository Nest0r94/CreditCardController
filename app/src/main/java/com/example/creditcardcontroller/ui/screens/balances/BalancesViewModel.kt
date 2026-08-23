package com.example.creditcardcontroller.ui.screens.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.dao.CategoriaDao
import com.example.creditcardcontroller.data.local.dao.MovimientoDao
import com.example.creditcardcontroller.data.local.dao.TarjetaDao
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class BalancesUiState(
    val tarjetas: List<TarjetaEntity> = emptyList(),
    val movimientos: List<MovimientoEntity> = emptyList(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val selectedTarjetaId: Long? = null,
    val totalPresupuesto: Double = 3200.0, // Hardcoded as per image for now or can be dynamic
    val gastoMensual: Double = 0.0,
    val gastoCuotas: Double = 0.0,
    val gastoUnPago: Double = 0.0
)

class BalancesViewModel(
    private val tarjetaDao: TarjetaDao,
    private val movimientoDao: MovimientoDao,
    private val categoriaDao: CategoriaDao
) : ViewModel() {

    private val _selectedTarjetaId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<BalancesUiState> = combine(
        tarjetaDao.getAllTarjetas(),
        movimientoDao.getAllMovements(),
        categoriaDao.getAllCategorias(),
        _selectedTarjetaId
    ) { tarjetas, movimientos, categorias, selectedId ->
        
        val now = LocalDate.now()
        val startOfMonth = now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endOfMonth = now.withDayOfMonth(now.lengthOfMonth()).atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val movimientosMes = movimientos.filter { it.fecha in startOfMonth..endOfMonth }
        
        val totalGasto = movimientosMes.sumOf { it.monto }
        val gastoCuotas = movimientosMes.filter { it.esCuotas }.sumOf { it.monto / it.cantidadCuotas }
        val gastoUnPago = movimientosMes.filter { !it.esCuotas }.sumOf { it.monto }

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
            gastoMensual = totalGasto,
            gastoCuotas = gastoCuotas,
            gastoUnPago = gastoUnPago
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BalancesUiState()
    )

    fun selectTarjeta(id: Long?) {
        _selectedTarjetaId.value = id
    }

    class Factory(
        private val tarjetaDao: TarjetaDao,
        private val movimientoDao: MovimientoDao,
        private val categoriaDao: CategoriaDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BalancesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BalancesViewModel(tarjetaDao, movimientoDao, categoriaDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
