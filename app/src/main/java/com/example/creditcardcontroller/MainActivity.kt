package com.example.creditcardcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Control financiero",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onBackground
            )
            Text(
                text = "Tu centro de mando para tarjetas, metas y gastos.",
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurfaceVariant
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceVariant,
                    contentColor = colors.onSurface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Saldo disponible",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$12.450,80",
                        style = MaterialTheme.typography.headlineLarge,
                        color = colors.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+8,4% respecto al mes pasado",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.secondary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    Text("Transferir")
                }

                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, colors.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colors.primary
                    )
                ) {
                    Text("Guardar")
                }
            }

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

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface,
                    contentColor = colors.onSurface
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Próximos movimientos",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "• Pago de tarjeta: $320", color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Depósito semanal: $1.200", color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "• Meta de ahorro: 68%", color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun MetricChip(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.16f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = colors.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
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