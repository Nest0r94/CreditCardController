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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.cards.CardStatusPreview
import com.example.creditcardcontroller.ui.composables.inputs.FormInput
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun EditCardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = true
) {
    var cardName by remember { mutableStateOf(if (isEditMode) "Platinum Premium" else "") }
    var monthlyLimit by remember { mutableStateOf(if (isEditMode) "$ 1500000" else "") }
    var installmentsLimit by remember { mutableStateOf(if (isEditMode) "$ 850000" else "") }
    var closingDate by remember { mutableStateOf(if (isEditMode) "25/12/2023" else "") }
    var dueDate by remember { mutableStateOf(if (isEditMode) "05/01/2024" else "") }
    var cardExpiration by remember { mutableStateOf(if (isEditMode) "noviembre de 2028" else "") }

    Scaffold(
        bottomBar = {
            PrimaryButton(
                text = if (isEditMode) "Guardar Cambios" else "Agregar Tarjeta",
                onClick = { /* TODO: Guardar lógica */ onBack() },
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

            FormInput(
                label = "Fecha de cierre",
                value = closingDate,
                onValueChange = { closingDate = it },
                icon = Icons.Default.CalendarToday,
                trailingIcon = Icons.Default.CalendarToday
            )

            FormInput(
                label = "Fecha de vencimiento",
                value = dueDate,
                onValueChange = { dueDate = it },
                icon = Icons.Default.CalendarToday,
                trailingIcon = Icons.Default.CalendarToday
            )

            FormInput(
                label = "Fecha de vencimiento de tarjeta (MM/AA)",
                value = cardExpiration,
                onValueChange = { cardExpiration = it },
                icon = Icons.Default.CalendarToday,
                trailingIcon = Icons.Default.CalendarToday
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditCardScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        EditCardScreen(onBack = {})
    }
}
