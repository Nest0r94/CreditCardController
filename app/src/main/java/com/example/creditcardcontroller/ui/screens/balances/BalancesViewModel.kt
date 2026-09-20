package com.example.creditcardcontroller.ui.screens.balances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.TipoMovimiento
import com.example.creditcardcontroller.data.local.dao.CategoriaDao
import com.example.creditcardcontroller.data.local.dao.MovimientoDao
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.dao.ResumenDao
import com.example.creditcardcontroller.data.local.dao.TarjetaDao
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.ResumenEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.util.periodoVencimientoResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import kotlin.math.abs

data class BalancesUiState(
    val tarjetas: List<TarjetaEntity> = emptyList(),
    val consumoPorTarjeta: Map<Long, Double> = emptyMap(),
    val tarjetasFiltro: List<TarjetaEntity> = emptyList(),
    val movimientos: List<MovimientoEntity> = emptyList(),
    val categorias: List<CategoriaEntity> = emptyList(),
    val selectedTarjetaId: Long? = null,
    val selectedDate: YearMonth = YearMonth.now(),
    val availableMonths: List<YearMonth> = emptyList(),
    val canGoPrev: Boolean = false,
    val canGoNext: Boolean = false,
    val totalPresupuesto: Double = 0.0,
    val gastoMensual: Double = 0.0,
    val gastoCuotas: Double = 0.0,
    val gastoUnPago: Double = 0.0,
    val limiteCuotas: Double = 0.0,
    val limiteUnPago: Double = 0.0
)

private data class BalancesData(
    val tarjetas: List<TarjetaEntity>,
    val movimientos: List<MovimientoEntity>,
    val categorias: List<CategoriaEntity>,
    val presupuestos: List<PresupuestoEntity>,
    val resumenes: List<ResumenEntity>
)

class BalancesViewModel(
    private val tarjetaDao: TarjetaDao,
    private val movimientoDao: MovimientoDao,
    private val categoriaDao: CategoriaDao,
    private val presupuestoDao: PresupuestoDao,
    private val resumenDao: ResumenDao
) : ViewModel() {

    private val _selectedTarjetaId = MutableStateFlow<Long?>(null)
    private val _selectedDate = MutableStateFlow<YearMonth?>(null)

    val uiState: StateFlow<BalancesUiState> = combine(
        combine(
            tarjetaDao.getAllTarjetas(),
            movimientoDao.getAllMovements(),
            categoriaDao.getAllCategorias(),
            presupuestoDao.getAllItems(),
            resumenDao.getAll()
        ) { tarjetas, movimientos, categorias, presupuestos, resumenes ->
            BalancesData(tarjetas, movimientos, categorias, presupuestos, resumenes)
        },
        _selectedTarjetaId,
        _selectedDate
    ) { data, selectedId, requestedDate ->
        val allTarjetas = data.tarjetas
        val movimientos = data.movimientos
        val categorias = data.categorias
        val presupuestos = data.presupuestos

        val tarjetasCredito = allTarjetas.filter { it.tipo == TipoMedioPago.CREDITO }
        val tarjetasById = allTarjetas.associateBy { it.id }

        // Solo se navega entre meses que tengan un resumen creado.
        val availableMonths = data.resumenes.map { YearMonth.parse(it.periodo) }.distinct().sorted()
        val defaultMonth = availableMonths.firstOrNull { !it.isBefore(YearMonth.now()) }
            ?: availableMonths.lastOrNull()
            ?: YearMonth.now()
        val selectedDate = requestedDate?.takeIf { availableMonths.isEmpty() || it in availableMonths }
            ?: defaultMonth
        val selectedIndex = availableMonths.indexOf(selectedDate)

        // Los movimientos de crédito pertenecen al mes de vencimiento de su resumen;
        // los de cuenta/débito al mes en que se realizaron.
        fun mesEfectivo(m: MovimientoEntity): YearMonth {
            val tarjeta = tarjetasById[m.tarjetaId]
            return if (tarjeta?.tipo == TipoMedioPago.CREDITO && tarjeta.diaCierreResumen != null) {
                periodoVencimientoResumen(m.fechaPresentacion, tarjeta.diaCierreResumen)
            } else {
                YearMonth.from(Instant.ofEpochMilli(m.fechaPresentacion).atZone(ZoneId.systemDefault()).toLocalDate())
            }
        }

        val movimientosMes = movimientos.filter { mesEfectivo(it) == selectedDate }

        val totalGasto = movimientosMes.sumOf {
            when (it.tipo) {
                TipoMovimiento.GASTO -> it.monto
                TipoMovimiento.INGRESO -> -it.monto
                TipoMovimiento.REINTEGRO -> 0.0
            }
        }
        val gastoCuotas = movimientosMes.filter { it.esCuotas && it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }
        val gastoUnPago = movimientosMes.filter { !it.esCuotas && it.tipo == TipoMovimiento.GASTO }.sumOf { it.monto }

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

        val consumoPorTarjeta = data.resumenes
            .filter { it.periodo == selectedDate.toString() }
            .associate { it.tarjetaId to it.total }

        BalancesUiState(
            tarjetas = tarjetasCredito,
            consumoPorTarjeta = consumoPorTarjeta,
            tarjetasFiltro = allTarjetas,
            movimientos = filteredMovimientos,
            categorias = categorias,
            selectedTarjetaId = selectedId,
            selectedDate = selectedDate,
            availableMonths = availableMonths,
            canGoPrev = selectedIndex > 0,
            canGoNext = selectedIndex in 0 until (availableMonths.size - 1),
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

    fun selectPrevMonth() = moveBy(-1)

    fun selectNextMonth() = moveBy(1)

    private fun moveBy(delta: Int) {
        val months = uiState.value.availableMonths
        if (months.isEmpty()) return
        val current = uiState.value.selectedDate
        val idx = months.indexOf(current)
        val target = if (idx == -1) 0 else (idx + delta).coerceIn(0, months.lastIndex)
        _selectedDate.value = months[target]
    }

    fun updateSelectedDate(date: YearMonth) {
        val months = uiState.value.availableMonths
        if (months.isEmpty()) {
            _selectedDate.value = date
            return
        }
        val target = if (date in months) {
            date
        } else {
            months.minByOrNull {
                abs((it.year * 12 + it.monthValue) - (date.year * 12 + date.monthValue))
            } ?: date
        }
        _selectedDate.value = target
    }

    class Factory(
        private val tarjetaDao: TarjetaDao,
        private val movimientoDao: MovimientoDao,
        private val categoriaDao: CategoriaDao,
        private val presupuestoDao: PresupuestoDao,
        private val resumenDao: ResumenDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BalancesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BalancesViewModel(tarjetaDao, movimientoDao, categoriaDao, presupuestoDao, resumenDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
