package com.example.creditcardcontroller.ui.screens.balance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import com.example.creditcardcontroller.ui.composables.cards.MetricChip
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

@Composable
fun BalanceScreen(modifier: Modifier = Modifier) {
    var totalMonthlyIncome by remember { mutableDoubleStateOf(4500.0) }
    var programmedMonthlyExpense by remember { mutableDoubleStateOf(2150.0) }

    var selectedDate by remember { mutableStateOf(YearMonth.now()) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<EditableItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val incomes = remember {
        mutableStateListOf(
            ExpenseItemData(UUID.randomUUID().toString(), "Ingreso mensual", 4500.0, Icons.Default.AddChart, Color(0xFF4CAF50))
        )
    }

    val expenses = remember {
        mutableStateListOf(
            ExpenseItemData("1", "Gasto 1 cuota en tarjeta", 450.0, Icons.Default.CreditCard, Color(0xFF00BFA5)),
            ExpenseItemData("2", "Gasto mensual de tarjeta", 850.0, Icons.Default.AccountBalanceWallet, Color(0xFFE57373)),
            ExpenseItemData("3", "Gasto en cuenta/efectivo", 850.0, Icons.Default.AccountBalance, Color(0xFF455A64))
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
                SummarySection(
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
                ExpenseRow(
                    expense = income,
                    onEditClick = { editingItem = EditableItem.Income(income) }
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
                ExpenseRow(
                    expense = expense,
                    onEditClick = { editingItem = EditableItem.Expense(expense) }
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
                    ExpenseItemData(
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
                    ExpenseItemData(
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
            is EditableItem.Income -> item.data.amount
            is EditableItem.Expense -> item.data.amount
        }
        val title = when (item) {
            is EditableItem.Income -> "Editar ${item.data.title}"
            is EditableItem.Expense -> "Editar ${item.data.title}"
        }

        EditAmountDialog(
            title = title,
            initialAmount = initialAmount,
            onDismiss = { editingItem = null },
            onConfirm = { newAmount ->
                when (item) {
                    is EditableItem.Income -> {
                        val index = incomes.indexOfFirst { it.id == item.data.id }
                        if (index != -1) {
                            incomes[index] = item.data.copy(amount = newAmount)
                        }
                    }
                    is EditableItem.Expense -> {
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

sealed class EditableItem {
    data class Income(val data: ExpenseItemData) : EditableItem()
    data class Expense(val data: ExpenseItemData) : EditableItem()
}

data class ExpenseItemData(
    val id: String,
    val title: String,
    val amount: Double,
    val icon: ImageVector,
    val iconBackground: Color
)

@Composable
fun SummarySection(
    totalIncome: Double,
    totalExpense: Double
) {
    val savings = totalIncome - totalExpense
    val savingsColor = Color(0xFFFFC107) // Amarillo para Ahorro

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Table/Grid of metrics
        Column(
            modifier = Modifier.weight(1.2f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Ingresos",
                value = "$ ${formatAmount(totalIncome)}",
                color = Color(0xFF4CAF50)
            )
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Gastos",
                value = "$ ${formatAmount(totalExpense)}",
                color = Color(0xFFF44336)
            )
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Ahorro",
                value = "$ ${formatAmount(savings)}",
                color = savingsColor
            )
        }

        // Pie Chart
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            val total = totalIncome.coerceAtLeast(1.0)
            val expenseAngle = (totalExpense / total * 360f).toFloat().coerceIn(0f, 360f)
            val savingsAngle = 360f - expenseAngle
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color(0xFFF44336),
                    startAngle = -90f,
                    sweepAngle = expenseAngle,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx())
                )
                drawArc(
                    color = Color(0xFFFFC107), // Amarillo para la porción de ahorro
                    startAngle = -90f + expenseAngle,
                    sweepAngle = savingsAngle,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx())
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val percentage = if (totalIncome > 0) (totalExpense / totalIncome * 100).toInt() else 0
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Gasto",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun DateHeader(
    selectedDate: YearMonth,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Mes anterior",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onMonthClick() }
            )
            Text(
                text = " ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedDate.year.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onYearClick() }
            )
        }

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Mes siguiente",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    val months = (1..12).toList()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Mes") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp)
            ) {
                items(months) { month ->
                    val monthName = YearMonth.of(2000, month).month.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                    TextButton(
                        onClick = { onMonthSelected(month) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(monthName.uppercase())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun YearPickerDialog(
    currentYear: Int,
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val years = (currentYear - 5..currentYear + 5).toList()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Año") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp)
            ) {
                items(years) { year ->
                    TextButton(
                        onClick = { onYearSelected(year) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(year.toString())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun ExpenseRow(
    expense: ExpenseItemData,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(expense.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = expense.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Amount aligned to unit
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "$",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = formatAmount(expense.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    modifier = Modifier.widthIn(min = 80.dp) // Ensures alignment
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EditAmountDialog(
    title: String,
    initialAmount: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var text by remember { mutableStateOf(initialAmount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { 
                val amount = text.toDoubleOrNull() ?: 0.0
                onConfirm(amount)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Gasto Programado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del gasto") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        onConfirm(name, amount)
                    }
                },
                enabled = name.isNotBlank() && amountText.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun AddIncomeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Ingreso") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ingreso") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    onConfirm(name, amount)
                },
                enabled = amountText.isNotBlank()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatAmount(amount: Double): String {
    return String.format(java.util.Locale.US, "%,.2f", amount)
        .replace(',', 'X')
        .replace('.', ',')
        .replace('X', '.')
}

@Preview(showBackground = true)
@Composable
fun BalanceScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        BalanceScreen()
    }
}
