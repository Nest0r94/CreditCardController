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
    val subtitle: String? = null,
    val amount: Double,
    val icon: ImageVector,
    val iconBackground: Color
)

fun PresupuestoEntity.toBudgetItemData(
    tarjetaNombre: (Long?) -> String? = { null }
) = BudgetItemData(
    id = id.toString(),
    title = titulo,
    subtitle = if (tipo == com.example.creditcardcontroller.data.local.entities.PresupuestoEntity.TIPO_GASTO) {
        tarjetaNombre(tarjetaId) ?: "Cuenta"
    } else {
        null
    },
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
