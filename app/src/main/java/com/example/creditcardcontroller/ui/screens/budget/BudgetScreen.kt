package com.example.creditcardcontroller.ui.screens.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.ui.composables.layout.DateHeader
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.composables.layout.MonthPickerDialog
import com.example.creditcardcontroller.ui.composables.layout.YearPickerDialog
import com.example.creditcardcontroller.ui.screens.budget.comp.AddBudgetItemDialog
import com.example.creditcardcontroller.ui.screens.budget.comp.BudgetItemRow
import com.example.creditcardcontroller.ui.screens.budget.comp.BudgetSummarySection
import com.example.creditcardcontroller.ui.screens.budget.comp.EditAmountDialog
import com.example.creditcardcontroller.ui.screens.budget.model.formatAmount
import com.example.creditcardcontroller.ui.screens.budget.model.toBudgetItemData
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import java.time.YearMonth

private val tabs = listOf(
    PresupuestoEntity.TIPO_INGRESO,
    PresupuestoEntity.TIPO_GASTO,
    PresupuestoEntity.TIPO_AHORRO
)

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModel.Factory(
            AppDatabase.getDatabase(LocalContext.current).presupuestoDao(),
            AppDatabase.getDatabase(LocalContext.current).tarjetaDao()
        )
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val availableMonths by viewModel.availableMonths.collectAsState()

    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(tabs.first()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<PresupuestoEntity?>(null) }

    val tarjetaNombre: (Long?) -> String? = { id ->
        uiState.tarjetas.find { it.id == id }?.nombre
    }

    val totalLimite = uiState.limites.sumOf { it.monto }

    val activeItems = when (selectedTab) {
        PresupuestoEntity.TIPO_GASTO -> uiState.expenses
        PresupuestoEntity.TIPO_AHORRO -> uiState.ahorros
        else -> uiState.incomes
    }

    FinancialSurface(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 8.dp)
        ) {
            item {
                DateHeader(
                    selectedDate = selectedDate,
                    onMonthClick = { showMonthPicker = true },
                    onYearClick = { showYearPicker = true },
                    onPreviousMonth = { viewModel.navigatePrevious() },
                    onNextMonth = { viewModel.navigateNext() },
                    prevEnabled = availableMonths.any { it.isBefore(selectedDate) },
                    nextEnabled = availableMonths.any { it.isAfter(selectedDate) }
                )
            }

            item {
                BudgetSummarySection(
                    totalIncome = uiState.totalIncome,
                    gastosChip = uiState.gastosChip,
                    ahorroChip = uiState.ahorroChip
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Establecer límites de tarjeta",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Total límites: $ ${formatAmount(totalLimite)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            items(uiState.limites) { limite ->
                BudgetItemRow(
                    item = limite.toBudgetItemData(),
                    onEditClick = { editingItem = limite }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Presupuesto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Agregar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .clickable { showAddDialog = true }
                    )
                }
            }

            item {
                TabRow(selectedTabIndex = tabs.indexOf(selectedTab).coerceAtLeast(0)) {
                    tabs.forEach { tipo ->
                        Tab(
                            selected = selectedTab == tipo,
                            onClick = { selectedTab = tipo },
                            text = { Text(tipo) }
                        )
                    }
                }
            }

            items(activeItems) { item ->
                val isDisponible = item.titulo == BudgetViewModel.DISPONIBLE_TITULO
                val data = item.toBudgetItemData(tarjetaNombre).let {
                    if (isDisponible) it.copy(subtitle = "Cálculo automático") else it
                }
                BudgetItemRow(
                    item = data,
                    editable = !isDisponible,
                    onEditClick = { editingItem = item }
                )
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month ->
                viewModel.updateSelectedDate(selectedDate.withMonth(month))
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        YearPickerDialog(
            currentYear = selectedDate.year,
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                viewModel.updateSelectedDate(selectedDate.withYear(year))
                showYearPicker = false
            }
        )
    }

    if (showAddDialog) {
        AddBudgetItemDialog(
            tipo = selectedTab,
            tarjetas = uiState.tarjetas,
            onDismiss = { showAddDialog = false },
            onConfirm = { nombre, monto, tarjetaId ->
                when (selectedTab) {
                    PresupuestoEntity.TIPO_GASTO -> viewModel.addExpense(nombre, monto, tarjetaId)
                    PresupuestoEntity.TIPO_AHORRO -> viewModel.addAhorro(nombre, monto)
                    else -> viewModel.addIncome(nombre, monto)
                }
                showAddDialog = false
            }
        )
    }

    editingItem?.let { entity ->
        EditAmountDialog(
            title = "Editar ${entity.titulo}",
            initialAmount = entity.monto,
            tipo = entity.tipo,
            initialTarjetaId = entity.tarjetaId,
            tarjetas = uiState.tarjetas,
            onDismiss = { editingItem = null },
            onConfirm = { newAmount, newTarjetaId ->
                viewModel.updateItem(entity, newAmount, newTarjetaId)
                editingItem = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        BudgetScreen()
    }
}