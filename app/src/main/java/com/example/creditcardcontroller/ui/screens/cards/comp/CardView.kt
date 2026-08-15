package com.example.creditcardcontroller.ui.screens.cards.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.ui.composables.feedback.LimitProgressBar
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun CardView(
    cardName: String,
    amount: String,
    limit: String,
    closingDate: String,
    dueDate: String,
    cardExpiration: String,
    usagePercentage: Float,
    isExpired: Boolean = false,
    onClick: () -> Unit = {}
) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6A4CFF), // Violeta intenso
            Color(0xFF9D66FF), // Violeta medio
            Color(0xFFC084FF)  // Violeta claro/rosado
        )
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .alpha(if (isExpired) 0.5f else 1f),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(24.dp)
        ) {
            Column {
                // Fila Superior: Nombre y Expiración
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = cardName,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    if (isExpired) {
                        Text(
                            text = "EXPIRADO",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Red,
                            fontWeight = FontWeight.Black
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EXPIRA",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = cardExpiration,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Fila Media: Monto y Límite
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 36.sp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "LÍMITE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = limit,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // Fechas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    DateItem(
                        icon = Icons.Default.CalendarMonth,
                        label = "CIERRE",
                        date = closingDate
                    )
                    Spacer(modifier = Modifier.width(24.dp))
                    DateItem(
                        icon = Icons.Default.Payments,
                        label = "VENCE",
                        date = dueDate
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Uso Actual y Barra de Progreso
                LimitProgressBar(
                    progress = usagePercentage,
                    leftLabel = "USO ACTUAL: ${String.format("%.1f", usagePercentage * 100)}%",
                    labelColor = Color.White,
                    indicatorColor = Color.White,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    barHeight = 8.dp
                )
            }
        }
    }
}

@Composable
fun DateItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, date: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun CardViewPreview() {
    CreditCardControllerTheme {
        Column(
            modifier = Modifier.padding(16.dp),
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
                cardName = "Mastercard Gold",
                amount = "$850.00",
                limit = "$2,000",
                closingDate = "10 Oct",
                dueDate = "25 Oct",
                cardExpiration = "01/24",
                usagePercentage = 0.425f,
                isExpired = true
            )
        }
    }
}
