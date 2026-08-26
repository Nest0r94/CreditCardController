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
import com.example.creditcardcontroller.ui.composables.layout.DateHeader
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.composables.layout.MonthPickerDialog
import com.example.creditcardcontroller.ui.composables.layout.YearPickerDialog
import com.example.creditcardcontroller.ui.screens.budget.comp.AddExpenseDialog
import com.example.creditcardcontroller.ui.screens.budget.comp.AddIncomeDialog
import com.example.creditcardcontroller.ui.screens.budget.comp.BudgetItemRow
import com.example.creditcardcontroller.ui.screens.budget.comp.BudgetSummarySection
import com.example.creditcardcontroller.ui.screens.budget.comp.EditAmountDialog
import com.example.creditcardcontroller.ui.screens.budget.model.BudgetEditableItem
import com.example.creditcardcontroller.ui.screens.budget.model.formatAmount
import com.example.creditcardcontroller.ui.screens.budget.model.toBudgetItemData
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import java.time.YearMonth

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    viewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModel.Factory(
            AppDatabase.getDatabase(LocalContext.current).presupuestoDao()
        )
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val availableMonths by viewModel.availableMonths.collectAsState()

    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BudgetEditableItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    FinancialSurface(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp)
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
                    totalExpense = uiState.totalExpense
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
                            text = "Desglose de Ingresos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Total mensual: $ ${formatAmount(uiState.totalIncome)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Agregar ingreso",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .clickable { showAddIncomeDialog = true }
                    )
                }
            }

            items(uiState.incomes) { income ->
                BudgetItemRow(
                    item = income.toBudgetItemData(),
                    onEditClick = { editingItem = BudgetEditableItem.Income(income) }
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
                            text = "Desglose de Gastos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Total programado: $ ${formatAmount(uiState.totalExpense)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Agregar gasto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .clickable { showAddDialog = true }
                    )
                }
            }

            items(uiState.expenses) { expense ->
                BudgetItemRow(
                    item = expense.toBudgetItemData(),
                    onEditClick = { editingItem = BudgetEditableItem.Expense(expense) }
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

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { name, amount ->
                viewModel.addIncome(name, amount)
                showAddIncomeDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount ->
                viewModel.addExpense(name, amount)
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        val entity = when (item) {
            is BudgetEditableItem.Income -> item.entity
            is BudgetEditableItem.Expense -> item.entity
        }
        val title = "Editar ${entity.titulo}"

        EditAmountDialog(
            title = title,
            initialAmount = entity.monto,
            onDismiss = { editingItem = null },
            onConfirm = { newAmount ->
                viewModel.updateAmount(entity, newAmount)
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
