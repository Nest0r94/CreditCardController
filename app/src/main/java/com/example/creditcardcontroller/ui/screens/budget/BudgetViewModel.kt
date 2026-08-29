package com.example.creditcardcontroller.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.dao.TarjetaDao
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.YearMonth

data class BudgetUiState(
    val incomes: List<PresupuestoEntity> = emptyList(),
    val expenses: List<PresupuestoEntity> = emptyList(),
    val limites: List<PresupuestoEntity> = emptyList(),
    val ahorros: List<PresupuestoEntity> = emptyList(),
    val tarjetas: List<TarjetaEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val gastosChip: Double = 0.0,
    val ahorroChip: Double = 0.0
)

class BudgetViewModel(
    private val presupuestoDao: PresupuestoDao,
    private val tarjetaDao: TarjetaDao
) : ViewModel() {

    companion object {
        const val DISPONIBLE_TITULO = "Disponible"
    }

    private val mutex = Mutex()
    private val _selectedDate = MutableStateFlow(YearMonth.now())
    val selectedDate: StateFlow<YearMonth> = _selectedDate

    private val tarjetas = tarjetaDao.getAllTarjetas()

    val availableMonths: StateFlow<List<YearMonth>> = presupuestoDao.getAvailableMonths()
        .map { tuples -> tuples.map { YearMonth.of(it.anio, it.mes) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        cleanupDuplicates()
    }

    private fun cleanupDuplicates() {
        viewModelScope.launch {
            mutex.withLock {
                val allItems = presupuestoDao.getAllItemsSync()
                // Agrupamos por título, tipo, mes y año (ignorando monto para detectar fantasmas)
                val grouped = allItems.groupBy {
                    "${it.titulo}-${it.tipo}-${it.mes}-${it.anio}"
                }
                grouped.forEach { (_, items) ->
                    if (items.size > 1) {
                        // Si hay duplicados, preferimos el que tiene monto > 0
                        val sortedItems = items.sortedByDescending { it.monto }
                        // Conservamos el mejor y borramos el resto
                        sortedItems.drop(1).forEach { presupuestoDao.delete(it) }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetUiState> = combine(
        flatMapLatestImpl(),
        tarjetas
    ) { items, tarjetas ->
        if (items.isEmpty()) {
            initializeMonth(_selectedDate.value)
        }
        val incomes = items.filter { it.tipo == PresupuestoEntity.TIPO_INGRESO }
        val gastos = items.filter { it.tipo == PresupuestoEntity.TIPO_GASTO }
        val limites = items.filter { it.tipo == PresupuestoEntity.TIPO_LIMITE }
        val ahorros = items.filter { it.tipo == PresupuestoEntity.TIPO_AHORRO }

        val totalIncome = incomes.sumOf { it.monto }
        val totalLimite = limites.sumOf { it.monto }
        // Los gastos con tarjeta ya estan contados en los limites
        val gastosCuenta = gastos.filter { it.tarjetaId == null }.sumOf { it.monto }
        val gastosChip = totalLimite + gastosCuenta
        val ahorroChip = totalIncome - gastosChip

        val ahorrosUsuario = ahorros.filter { it.titulo != DISPONIBLE_TITULO }
        val disponibleMonto = totalIncome - gastosChip - ahorrosUsuario.sumOf { it.monto }
        val ahorrosUi = (listOfNotNull(
            ahorros.firstOrNull { it.titulo == DISPONIBLE_TITULO }?.copy(monto = disponibleMonto)
        ) + ahorrosUsuario).distinctBy { it.id }

        BudgetUiState(
            incomes = incomes,
            expenses = gastos,
            limites = limites,
            ahorros = ahorrosUi,
            tarjetas = tarjetas,
            totalIncome = totalIncome,
            gastosChip = gastosChip,
            ahorroChip = ahorroChip
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun flatMapLatestImpl() = _selectedDate.flatMapLatest { date ->
        presupuestoDao.getItemsByMonth(date.monthValue, date.year)
    }

    private fun initializeMonth(date: YearMonth) {
        viewModelScope.launch {
            mutex.withLock {
                // Verificamos si realmente está vacío
                val currentItems = presupuestoDao.getItemsByMonthSync(date.monthValue, date.year)
                if (currentItems.isNotEmpty()) return@withLock

                val lastMonth = presupuestoDao.getLastMonthWithData()

                if (lastMonth != null) {
                    // Copiar del mes más reciente con datos
                    val itemsToCopy = presupuestoDao.getItemsByMonthSync(lastMonth.mes, lastMonth.anio)
                    itemsToCopy.forEach { item ->
                        presupuestoDao.insert(
                            item.copy(id = 0, mes = date.monthValue, anio = date.year)
                        )
                    }
                } else {
                    // Seed inicial con los 3 montos troncales si es la primera vez en la app
                    seedDefaultItems(date)
                }
            }
        }
    }

    private suspend fun seedDefaultItems(date: YearMonth) {
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Ingreso mensual", monto = 4500.0, tipo = PresupuestoEntity.TIPO_INGRESO,
                icono = "AddChart", color = "#4CAF50"
            )
        )
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Gasto 1 cuota en tarjeta", monto = 450.0, tipo = PresupuestoEntity.TIPO_LIMITE,
                icono = "CreditCard", color = "#00BFA5"
            )
        )
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Gasto mensual de tarjeta", monto = 850.0, tipo = PresupuestoEntity.TIPO_LIMITE,
                icono = "AccountBalanceWallet", color = "#E57373"
            )
        )
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = DISPONIBLE_TITULO, monto = 0.0, tipo = PresupuestoEntity.TIPO_AHORRO,
                icono = "AccountBalance", color = "#FFC107"
            )
        )
    }

    fun updateSelectedDate(date: YearMonth) {
        if (availableMonths.value.contains(date)) {
            _selectedDate.value = date
        }
    }

    fun navigatePrevious() {
        val current = _selectedDate.value
        val available = availableMonths.value
        val previous = available.filter { it.isBefore(current) }.maxOrNull()
        previous?.let { _selectedDate.value = it }
    }

    fun navigateNext() {
        val current = _selectedDate.value
        val available = availableMonths.value
        val next = available.filter { it.isAfter(current) }.minOrNull()
        next?.let { _selectedDate.value = it }
    }

    fun addIncome(titulo: String, monto: Double) {
        addItem(PresupuestoEntity.TIPO_INGRESO, titulo, monto, tarjetaId = null)
    }

    fun addExpense(titulo: String, monto: Double, tarjetaId: Long?) {
        addItem(PresupuestoEntity.TIPO_GASTO, titulo, monto, tarjetaId = tarjetaId)
    }

    fun addAhorro(titulo: String, monto: Double) {
        addItem(PresupuestoEntity.TIPO_AHORRO, titulo, monto, tarjetaId = null)
    }

    private fun addItem(tipo: String, titulo: String, monto: Double, tarjetaId: Long?) {
        val (icono, color) = when (tipo) {
            PresupuestoEntity.TIPO_INGRESO -> "Payments" to "#4CAF50"
            PresupuestoEntity.TIPO_GASTO -> "ShoppingCart" to "#7E57C2"
            else -> "AccountBalance" to "#FFC107"
        }
        viewModelScope.launch {
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = _selectedDate.value.monthValue,
                    anio = _selectedDate.value.year,
                    titulo = titulo,
                    monto = monto,
                    tipo = tipo,
                    icono = icono,
                    color = color,
                    tarjetaId = tarjetaId
                )
            )
        }
    }

    fun updateItem(item: PresupuestoEntity, newAmount: Double, newTarjetaId: Long? = null) {
        if (item.titulo == DISPONIBLE_TITULO) return
        viewModelScope.launch {
            presupuestoDao.update(item.copy(monto = newAmount, tarjetaId = if (item.tipo == PresupuestoEntity.TIPO_GASTO) newTarjetaId else item.tarjetaId))
        }
    }

    fun deleteItem(item: PresupuestoEntity) {
        if (item.titulo == DISPONIBLE_TITULO) return
        viewModelScope.launch {
            presupuestoDao.delete(item)
        }
    }

    class Factory(
        private val presupuestoDao: PresupuestoDao,
        private val tarjetaDao: TarjetaDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(presupuestoDao, tarjetaDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
