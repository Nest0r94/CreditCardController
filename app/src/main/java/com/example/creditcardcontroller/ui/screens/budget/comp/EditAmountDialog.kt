package com.example.creditcardcontroller.ui.screens.budget.comp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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

@Composable
fun EditAmountDialog(
    title: String,
    initialAmount: Double,
    tipo: String,
    initialTarjetaId: Long?,
    tarjetas: List<TarjetaEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Double, Long?) -> Unit
) {
    var text by remember { mutableStateOf(initialAmount.toString()) }
    var selectedTarjetaId by remember { mutableStateOf(initialTarjetaId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Monto") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (tipo == PresupuestoEntity.TIPO_GASTO) {
                    HorizontalDivider(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tipo de pago",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedTarjetaId == null,
                            onClick = { selectedTarjetaId = null },
                            label = { Text("Cuenta") }
                        )
                        tarjetas.forEach { tarjeta ->
                            FilterChip(
                                selected = selectedTarjetaId == tarjeta.id,
                                onClick = { selectedTarjetaId = tarjeta.id },
                                label = { Text(tarjeta.nombre) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = text.toDoubleOrNull() ?: 0.0
                onConfirm(amount, selectedTarjetaId)
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
