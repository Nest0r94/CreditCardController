package com.example.creditcardcontroller.ui.screens.budget.comp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.screens.promos.comp.CardSelector

@Composable
fun AddBudgetItemDialog(
    tipo: String,
    tarjetas: List<TarjetaEntity>,
    onDismiss: () -> Unit,
    onConfirm: (titulo: String, monto: Double, tarjetaId: Long?) -> Unit
) {
    val title = when (tipo) {
        PresupuestoEntity.TIPO_INGRESO -> "Agregar Ingreso"
        PresupuestoEntity.TIPO_AHORRO -> "Agregar Ahorro"
        else -> "Agregar Gasto"
    }
    val nameLabel = when (tipo) {
        PresupuestoEntity.TIPO_INGRESO -> "Nombre del ingreso"
        PresupuestoEntity.TIPO_AHORRO -> "Nombre del ahorro"
        else -> "Nombre del gasto"
    }
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedTarjetaId by remember { mutableStateOf<Long?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(nameLabel) },
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
                if (tipo == PresupuestoEntity.TIPO_GASTO) {
                    HorizontalDivider(modifier = Modifier.height(16.dp), color = androidx.compose.ui.graphics.Color.Transparent)
                    Text(
                        text = "Método de pago",
                        style = androidx.compose.material3.MaterialTheme.typography.labelLarge
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CardSelector(
                            name = "Efectivo",
                            selected = selectedTarjetaId == null,
                            onClick = { selectedTarjetaId = null }
                        )
                        tarjetas.forEach { tarjeta ->
                            CardSelector(
                                name = tarjeta.nombre,
                                selected = selectedTarjetaId == tarjeta.id,
                                onClick = { selectedTarjetaId = tarjeta.id }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    onConfirm(name, amount, selectedTarjetaId)
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