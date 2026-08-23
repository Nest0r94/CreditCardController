package com.example.creditcardcontroller.ui.screens.budget.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.Locale

data class BudgetItemData(
    val id: String,
    val title: String,
    val amount: Double,
    val icon: ImageVector,
    val iconBackground: Color
)

sealed class BudgetEditableItem {
    data class Income(val data: BudgetItemData) : BudgetEditableItem()
    data class Expense(val data: BudgetItemData) : BudgetEditableItem()
}

internal fun formatAmount(amount: Double): String {
    return String.format(Locale.US, "%,.2f", amount)
        .replace(',', 'X')
        .replace('.', ',')
        .replace('X', '.')
}
