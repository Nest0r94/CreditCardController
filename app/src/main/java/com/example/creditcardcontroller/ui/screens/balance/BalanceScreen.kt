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
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import java.util.UUID

@Composable
fun BalanceScreen(modifier: Modifier = Modifier) {
    var monthlyIncome by remember { mutableDoubleStateOf(4500.0) }
    var programmedMonthlyExpense by remember { mutableDoubleStateOf(2150.0) }

    var editingItem by remember { mutableStateOf<EditableItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val expenses = remember {
        mutableStateListOf(
            ExpenseItemData("1", "Gasto 1 cuota en tarjeta", "Visa finaliza en 4512", 450.0, Icons.Default.CreditCard, Color(0xFF00BFA5)),
            ExpenseItemData("2", "Gasto mensual de tarjeta", "Cuotas 3/12", 850.0, Icons.Default.AccountBalanceWallet, Color(0xFFE57373)),
            ExpenseItemData("3", "Gasto en cuenta/efectivo", "Transferencias automáticas", 850.0, Icons.Default.AccountBalance, Color(0xFF455A64))
        )
    }

    // Calculate programmed expense based on items
    LaunchedEffect(expenses.toList()) {
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
                Text(
                    text = "Resumen de Montos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item {
                SummaryCard(
                    title = "Ingreso Mensual",
                    amount = monthlyIncome,
                    icon = Icons.Default.Edit,
                    onEditClick = { editingItem = EditableItem.Income }
                )
            }

            item {
                SummaryCard(
                    title = "Gasto Mensual Programado",
                    amount = programmedMonthlyExpense,
                    icon = Icons.Default.AccountBalanceWallet,
                    onEditClick = { /* No editing for this one as it's calculated */ }
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
                        text = "Desglose de Gastos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Agregar gasto programado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
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

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, amount ->
                expenses.add(
                    ExpenseItemData(
                        id = UUID.randomUUID().toString(),
                        title = name,
                        subtitle = "",
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
            is EditableItem.Income -> monthlyIncome
            is EditableItem.Expense -> item.data.amount
        }
        val title = when (item) {
            is EditableItem.Income -> "Editar Ingreso Mensual"
            is EditableItem.Expense -> "Editar ${item.data.title}"
        }

        EditAmountDialog(
            title = title,
            initialAmount = initialAmount,
            onDismiss = { editingItem = null },
            onConfirm = { newAmount ->
                when (item) {
                    is EditableItem.Income -> monthlyIncome = newAmount
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
    object Income : EditableItem()
    data class Expense(val data: ExpenseItemData) : EditableItem()
}

data class ExpenseItemData(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val icon: ImageVector,
    val iconBackground: Color
)

@Composable
fun SummaryCard(
    title: String,
    amount: Double,
    icon: ImageVector,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$ ${formatAmount(amount)}",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
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

private fun formatAmount(amount: Double): String {
    return String.format("%,.2f", amount)
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
