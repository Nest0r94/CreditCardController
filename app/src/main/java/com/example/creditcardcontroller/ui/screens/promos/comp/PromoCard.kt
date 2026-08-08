package com.example.creditcardcontroller.ui.screens.promos.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

data class PromoData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val expiryDate: String,
    val reimbursed: Int,
    val limit: Int,
    val isFinalized: Boolean = false
)

@Composable
fun PromoCard(promo: PromoData, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = if (promo.isFinalized) 0.2f else 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant.copy(alpha = if (promo.isFinalized) 0.5f else 1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = promo.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant.copy(alpha = if (promo.isFinalized) 0.5f else 1f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = promo.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.onSurface.copy(alpha = if (promo.isFinalized) 0.6f else 1f)
                    )
                    Text(
                        text = promo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant.copy(alpha = if (promo.isFinalized) 0.6f else 1f)
                    )
                }

                // Expiry Info
                Column(horizontalAlignment = Alignment.End) {
                    if (promo.expiryDate != "VENCIDO") {
                        Text(
                            text = "VENCE",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = colors.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    val dateParts = promo.expiryDate.split(" ")
                    if (dateParts.size == 2 && dateParts[0].length == 3) {
                        Text(
                            text = dateParts[0],
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.tertiary
                        )
                        Text(
                            text = dateParts[1],
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.tertiary,
                            modifier = Modifier.offset(y = (-4).dp)
                        )
                    } else {
                        Text(
                            text = promo.expiryDate,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = if (promo.isFinalized) colors.error else colors.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (!promo.isFinalized) {
                    Text(
                        text = "Reembolsado $${promo.reimbursed} / $${promo.limit}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colors.onSurfaceVariant
                    )
                }
                Text(
                    text = if (promo.reimbursed >= promo.limit) "Límite Alcanzado" else if (promo.isFinalized) "Finalizado" else "${(promo.reimbursed.toFloat() / promo.limit * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (promo.isFinalized) colors.error else colors.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { promo.reimbursed.toFloat() / promo.limit },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (promo.isFinalized) colors.error else colors.secondary,
                trackColor = colors.outlineVariant.copy(alpha = 0.3f),
                strokeCap = StrokeCap.Round,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoCardPreview() {
    CreditCardControllerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PromoCard(
                promo = PromoData(
                    title = "Supermercado",
                    description = "20% de reintegro los martes",
                    icon = Icons.Default.ShoppingCart,
                    expiryDate = "DIC 31",
                    reimbursed = 1500,
                    limit = 5000
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PromoCardFinalizedPreview() {
    CreditCardControllerTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PromoCard(
                promo = PromoData(
                    title = "Combustible",
                    description = "10% de ahorro los jueves",
                    icon = Icons.Default.ShoppingCart,
                    expiryDate = "VENCIDO",
                    reimbursed = 3000,
                    limit = 3000,
                    isFinalized = true
                )
            )
        }
    }
}
