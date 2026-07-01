package com.example.creditcardcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.actions.ActionButtonRow
import com.example.creditcardcontroller.ui.composables.cards.BalanceCard
import com.example.creditcardcontroller.ui.composables.cards.MetricChip
import com.example.creditcardcontroller.ui.composables.cards.SectionCard
import com.example.creditcardcontroller.ui.composables.layout.FinancialHeader
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.composables.layout.MovementRow
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CreditCardControllerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme

    FinancialSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FinancialHeader(
                title = "Control financiero",
                subtitle = "Tu centro de mando para tarjetas, metas y gastos."
            )

            BalanceCard(balance = "$12.450,80", change = "+8,4% respecto al mes pasado")

            ActionButtonRow(
                primaryLabel = "Transferir",
                secondaryLabel = "Guardar",
                onPrimaryClick = { },
                onSecondaryClick = { }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = "Ingresos",
                    value = "+$2.150",
                    color = colors.secondary
                )
                MetricChip(
                    modifier = Modifier.weight(1f),
                    title = "Gastos",
                    value = "-$890",
                    color = colors.tertiary
                )
            }

            SectionCard(title = "Próximos movimientos") {
                MovementRow(label = "Pago de tarjeta", value = "$320")
                MovementRow(label = "Depósito semanal", value = "$1.200")
                MovementRow(label = "Meta de ahorro", value = "68%")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CreditCardControllerTheme {
        HomeScreen()
    }
}