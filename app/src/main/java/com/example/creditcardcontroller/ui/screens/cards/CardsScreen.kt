package com.example.creditcardcontroller.ui.screens.cards

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.screens.cards.comp.CardView

@Composable
fun CardsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CardView(
                cardName = "Visa Principal",
                amount = "$1,240.00",
                limit = "$5,000",
                closingDate = "15 Oct",
                dueDate = "02 Nov",
                cardExpiration = "12/28",
                usagePercentage = 0.248f
            )

            CardView(
                cardName = "Mastercard Black",
                amount = "$4,500.00",
                limit = "$10,000",
                closingDate = "20 Oct",
                dueDate = "05 Nov",
                cardExpiration = "06/30",
                usagePercentage = 0.45f
            )

            CardView(
                cardName = "Amex Gold",
                amount = "$850.00",
                limit = "$3,000",
                closingDate = "10 Oct",
                dueDate = "25 Oct",
                cardExpiration = "03/27",
                usagePercentage = 0.283f
            )
            
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Agregar Tarjeta",
            onClick = { /* TODO */ },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Add
        )
    }
}
