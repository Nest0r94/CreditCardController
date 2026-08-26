package com.example.creditcardcontroller.ui.screens.budget.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import java.util.Locale

data class BudgetItemData(
    val id: String,
    val title: String,
    val amount: Double,
    val icon: ImageVector,
    val iconBackground: Color
)

sealed class BudgetEditableItem {
    data class Income(val entity: PresupuestoEntity) : BudgetEditableItem()
    data class Expense(val entity: PresupuestoEntity) : BudgetEditableItem()
}

fun PresupuestoEntity.toBudgetItemData() = BudgetItemData(
    id = id.toString(),
    title = titulo,
    amount = monto,
    icon = iconoDeCategoria(icono),
    iconBackground = colorDeCategoria(color)
)

internal fun formatAmount(amount: Double): String {
    return String.format(Locale.US, "%,.2f", amount)
        .replace(',', 'X')
        .replace('.', ',')
        .replace('X', '.')
}
