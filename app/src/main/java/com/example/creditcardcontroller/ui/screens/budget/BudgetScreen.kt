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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.example.creditcardcontroller.ui.screens.budget.model.BudgetItemData
import com.example.creditcardcontroller.ui.screens.budget.model.formatAmount
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import java.time.YearMonth
import java.util.UUID

@Composable
fun BudgetScreen(modifier: Modifier = Modifier) {
    var totalMonthlyIncome by remember { mutableDoubleStateOf(4500.0) }
    var programmedMonthlyExpense by remember { mutableDoubleStateOf(2150.0) }

    var selectedDate by remember { mutableStateOf(YearMonth.now()) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<BudgetEditableItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val incomes = remember {
        mutableStateListOf(
            BudgetItemData(UUID.randomUUID().toString(), "Ingreso mensual", 4500.0, Icons.Default.AddChart, Color(0xFF4CAF50))
        )
    }

    val expenses = remember {
        mutableStateListOf(
            BudgetItemData("1", "Gasto 1 cuota en tarjeta", 450.0, Icons.Default.CreditCard, Color(0xFF00BFA5)),
            BudgetItemData("2", "Gasto mensual de tarjeta", 850.0, Icons.Default.AccountBalanceWallet, Color(0xFFE57373)),
            BudgetItemData("3", "Gasto en cuenta/efectivo", 850.0, Icons.Default.AccountBalance, Color(0xFF455A64))
        )
    }

    // Calculate total income and programmed expense based on items
    LaunchedEffect(incomes.toList(), expenses.toList()) {
        totalMonthlyIncome = incomes.sumOf { it.amount }
        programmedMonthlyExpense = expenses.sumOf { it.amount }
    }

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
                    onPreviousMonth = { selectedDate = selectedDate.minusMonths(1) },
                    onNextMonth = { selectedDate = selectedDate.plusMonths(1) }
                )
            }

            item {
                BudgetSummarySection(
                    totalIncome = totalMonthlyIncome,
                    totalExpense = programmedMonthlyExpense
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
                            text = "Total mensual: $ ${formatAmount(totalMonthlyIncome)}",
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

            items(incomes) { income ->
                BudgetItemRow(
                    item = income,
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
                            text = "Total programado: $ ${formatAmount(programmedMonthlyExpense)}",
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

            items(expenses) { expense ->
                BudgetItemRow(
                    item = expense,
                    onEditClick = { editingItem = BudgetEditableItem.Expense(expense) }
                )
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month ->
                selectedDate = selectedDate.withMonth(month)
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        YearPickerDialog(
            currentYear = selectedDate.year,
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                selectedDate = selectedDate.withYear(year)
                showYearPicker = false
            }
        )
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { name, amount ->
                incomes.add(
                    BudgetItemData(
                        id = UUID.randomUUID().toString(),
                        title = name,
                        amount = amount,
                        icon = Icons.Default.Payments,
                        iconBackground = Color(0xFF4CAF50)
                    )
                )
                showAddIncomeDialog = false
            }
        )
    }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount ->
                expenses.add(
                    BudgetItemData(
                        id = UUID.randomUUID().toString(),
                        title = name,
                        amount = amount,
                        icon = Icons.Default.ShoppingCart,
                        iconBackground = Color(0xFF7E57C2)
                    )
                )
                showAddDialog = false
            }
        )
    }

    editingItem?.let { item ->
        val initialAmount = when (item) {
            is BudgetEditableItem.Income -> item.data.amount
            is BudgetEditableItem.Expense -> item.data.amount
        }
        val title = when (item) {
            is BudgetEditableItem.Income -> "Editar ${item.data.title}"
            is BudgetEditableItem.Expense -> "Editar ${item.data.title}"
        }

        EditAmountDialog(
            title = title,
            initialAmount = initialAmount,
            onDismiss = { editingItem = null },
            onConfirm = { newAmount ->
                when (item) {
                    is BudgetEditableItem.Income -> {
                        val index = incomes.indexOfFirst { it.id == item.data.id }
                        if (index != -1) {
                            incomes[index] = item.data.copy(amount = newAmount)
                        }
                    }
                    is BudgetEditableItem.Expense -> {
                        val index = expenses.indexOfFirst { it.id == item.data.id }
                        if (index != -1) {
                            expenses[index] = item.data.copy(amount = newAmount)
                        }
                    }
                }
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
