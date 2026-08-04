package com.example.creditcardcontroller.ui.screens.editcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.cards.CardStatusPreview
import com.example.creditcardcontroller.ui.composables.inputs.FormInput
import com.example.creditcardcontroller.ui.composables.inputs.OfferDateField
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import kotlinx.coroutines.launch

@Composable
fun EditCardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    tarjetaId: Long? = null
) {
    val isEditMode = tarjetaId != null
    val context = LocalContext.current
    val tarjetaDao = remember { AppDatabase.getDatabase(context).tarjetaDao() }
    val scope = rememberCoroutineScope()

    var cardName by remember { mutableStateOf("") }
    var monthlyLimit by remember { mutableStateOf("") }
    var installmentsLimit by remember { mutableStateOf("") }
    var closingDate by remember { mutableStateOf<Long?>(null) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var cardExpiration by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(tarjetaId) {
        if (tarjetaId != null) {
            tarjetaDao.getById(tarjetaId)?.let { tarjeta ->
                cardName = tarjeta.nombre
                monthlyLimit = formatAmount(tarjeta.limiteMensual)
                installmentsLimit = formatAmount(tarjeta.limiteCuotas)
                closingDate = tarjeta.fechaCierreResumen
                dueDate = tarjeta.fechaVencimientoResumen
                cardExpiration = tarjeta.vencimientoTarjeta
            }
        }
    }

    fun save() {
        val nombre = cardName.trim()
        if (nombre.isEmpty()) return

        val limiteMensual = parseAmount(monthlyLimit)
        val limiteCuotas = parseAmount(installmentsLimit)
        val fechaCierre = closingDate ?: 0L
        val fechaVencimiento = dueDate ?: 0L
        val vencimientoTarjeta = cardExpiration ?: 0L

        scope.launch {
            if (tarjetaId == null) {
                tarjetaDao.insert(
                    TarjetaEntity(
                        nombre = nombre,
                        limiteMensual = limiteMensual,
                        limiteCuotas = limiteCuotas,
                        fechaCierreResumen = fechaCierre,
                        fechaVencimientoResumen = fechaVencimiento,
                        vencimientoTarjeta = vencimientoTarjeta
                    )
                )
            } else {
                tarjetaDao.getById(tarjetaId)?.let { existing ->
                    tarjetaDao.update(
                        existing.copy(
                            nombre = nombre,
                            limiteMensual = limiteMensual,
                            limiteCuotas = limiteCuotas,
                            fechaCierreResumen = fechaCierre,
                            fechaVencimientoResumen = fechaVencimiento,
                            vencimientoTarjeta = vencimientoTarjeta
                        )
                    )
                }
            }
            onBack()
        }
    }

    Scaffold(
        bottomBar = {
            PrimaryButton(
                text = if (isEditMode) "Guardar Cambios" else "Agregar Tarjeta",
                onClick = { save() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                icon = if (isEditMode) Icons.Default.Save else Icons.Default.CreditCard
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (isEditMode) {
                CardStatusPreview(
                    cardName = cardName,
                    status = "ACTIVE"
                )
            }

            FormInput(
                label = "Nombre de la tarjeta",
                value = cardName,
                onValueChange = { cardName = it },
                icon = Icons.Default.CreditCard
            )

            FormInput(
                label = "Límite mensual",
                value = monthlyLimit,
                onValueChange = { monthlyLimit = it },
                icon = Icons.Default.Payments
            )

            FormInput(
                label = "Límite en cuotas",
                value = installmentsLimit,
                onValueChange = { installmentsLimit = it },
                icon = Icons.Default.Payments
            )

            OfferDateField(
                label = "Fecha de cierre",
                selectedDateMillis = closingDate,
                onDateSelected = { closingDate = it }
            )

            OfferDateField(
                label = "Fecha de vencimiento",
                selectedDateMillis = dueDate,
                onDateSelected = { dueDate = it }
            )

            OfferDateField(
                label = "Fecha de vencimiento de tarjeta",
                selectedDateMillis = cardExpiration,
                onDateSelected = { cardExpiration = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private fun formatAmount(value: Double): String {
    return if (value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}

private fun parseAmount(value: String): Double {
    var cleaned = value.replace("$", "").replace(" ", "").replace(",", "")
    val lastDot = cleaned.lastIndexOf('.')
    if (lastDot > 0 && cleaned.length - lastDot - 1 == 3 && cleaned.indexOf('.') == lastDot) {
        cleaned = cleaned.replace(".", "")
    }
    return cleaned.toDoubleOrNull() ?: 0.0
}

@Preview(showBackground = true)
@Composable
fun EditCardScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        EditCardScreen(onBack = {})
    }
}
