package com.example.creditcardcontroller.ui.screens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creditcardcontroller.data.local.dao.PresupuestoDao
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth

data class BudgetUiState(
    val incomes: List<PresupuestoEntity> = emptyList(),
    val expenses: List<PresupuestoEntity> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0
)

class BudgetViewModel(
    private val presupuestoDao: PresupuestoDao
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(YearMonth.now())
    val selectedDate: StateFlow<YearMonth> = _selectedDate

    val availableMonths: StateFlow<List<YearMonth>> = presupuestoDao.getAvailableMonths()
        .map { tuples -> tuples.map { YearMonth.of(it.anio, it.mes) } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkAndCreateCurrentMonth()
    }

    private fun checkAndCreateCurrentMonth() {
        viewModelScope.launch {
            val now = YearMonth.now()
            val items = presupuestoDao.getItemsByMonthSync(now.monthValue, now.year)
            if (items.isEmpty()) {
                initializeMonth(now)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BudgetUiState> = _selectedDate.flatMapLatest { date ->
        presupuestoDao.getItemsByMonth(date.monthValue, date.year)
    }.onEach { items ->
        if (items.isEmpty()) {
            initializeMonth(_selectedDate.value)
        }
    }.map { items ->
        val incomes = items.filter { it.tipo == "INGRESO" }
        val expenses = items.filter { it.tipo == "GASTO" }
        BudgetUiState(
            incomes = incomes,
            expenses = expenses,
            totalIncome = incomes.sumOf { it.monto },
            totalExpense = expenses.sumOf { it.monto }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudgetUiState()
    )

    private fun initializeMonth(date: YearMonth) {
        viewModelScope.launch {
            // Verificamos si realmente está vacío
            val currentItems = presupuestoDao.getItemsByMonthSync(date.monthValue, date.year)
            if (currentItems.isNotEmpty()) return@launch

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

    private suspend fun seedDefaultItems(date: YearMonth) {
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Ingreso mensual", monto = 4500.0, tipo = "INGRESO",
                icono = "AddChart", color = "#4CAF50"
            )
        )
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Gasto 1 cuota en tarjeta", monto = 450.0, tipo = "GASTO",
                icono = "CreditCard", color = "#00BFA5"
            )
        )
        presupuestoDao.insert(
            PresupuestoEntity(
                mes = date.monthValue, anio = date.year,
                titulo = "Gasto mensual de tarjeta", monto = 850.0, tipo = "GASTO",
                icono = "AccountBalanceWallet", color = "#E57373"
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
        viewModelScope.launch {
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = _selectedDate.value.monthValue,
                    anio = _selectedDate.value.year,
                    titulo = titulo,
                    monto = monto,
                    tipo = "INGRESO",
                    icono = "Payments",
                    color = "#4CAF50"
                )
            )
        }
    }

    fun addExpense(titulo: String, monto: Double) {
        viewModelScope.launch {
            presupuestoDao.insert(
                PresupuestoEntity(
                    mes = _selectedDate.value.monthValue,
                    anio = _selectedDate.value.year,
                    titulo = titulo,
                    monto = monto,
                    tipo = "GASTO",
                    icono = "ShoppingCart",
                    color = "#7E57C2"
                )
            )
        }
    }

    fun updateAmount(item: PresupuestoEntity, newAmount: Double) {
        viewModelScope.launch {
            presupuestoDao.update(item.copy(monto = newAmount))
        }
    }

    fun deleteItem(item: PresupuestoEntity) {
        viewModelScope.launch {
            presupuestoDao.delete(item)
        }
    }

    class Factory(private val presupuestoDao: PresupuestoDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(presupuestoDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
