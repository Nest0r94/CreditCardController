package com.example.creditcardcontroller.ui.screens.promos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun PromosScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ofertas",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificaciones",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            placeholder = { Text("Buscar marcas o promociones...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            singleLine = true
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Text(
                    text = "Promociones del Día",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(promosDelDia) { promo ->
                PromoCard(promo)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Otras Promociones",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(otrasPromos) { promo ->
                PromoCard(promo)
            }
        }
    }
}

@Composable
fun PromoCard(promo: PromoData) {
    val colors = MaterialTheme.colorScheme
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant.copy(alpha = 0.5f)
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
                        .background(colors.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = promo.icon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = promo.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.onSurface
                    )
                    Text(
                        text = promo.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant
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
                            color = if (promo.isFinalized) colors.onSurfaceVariant.copy(alpha = 0.5f) else colors.tertiary
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
                Text(
                    text = "Reembolsado $${promo.reimbursed} / $${promo.limit}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurfaceVariant
                )
                Text(
                    text = if (promo.reimbursed >= promo.limit) "Límite Alcanzado" else if (promo.isFinalized) "Finalizado" else "${(promo.reimbursed.toFloat() / promo.limit * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { promo.reimbursed.toFloat() / promo.limit },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (promo.isFinalized) colors.outlineVariant else colors.secondary,
                trackColor = colors.outlineVariant.copy(alpha = 0.3f),
            )
        }
    }
}

data class PromoData(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val expiryDate: String,
    val reimbursed: Int,
    val limit: Int,
    val isFinalized: Boolean = false
)

val promosDelDia = listOf(
    PromoData(
        title = "The Bistro",
        description = "25% de descuento total",
        icon = Icons.Default.Restaurant,
        expiryDate = "Hoy",
        reimbursed = 120,
        limit = 500
    ),
    PromoData(
        title = "Cloud Store",
        description = "$100 de crédito en equipos",
        icon = Icons.Default.Cloud,
        expiryDate = "Hoy",
        reimbursed = 300,
        limit = 300
    )
)

val otrasPromos = listOf(
    PromoData(
        title = "Vogue Apparel",
        description = "15% dto. Nueva Temporada",
        icon = Icons.Default.Checkroom,
        expiryDate = "Oct 05",
        reimbursed = 45,
        limit = 200
    ),
    PromoData(
        title = "Sky High Travel",
        description = "10% Cashback en vuelos",
        icon = Icons.Default.Flight,
        expiryDate = "VENCIDO",
        reimbursed = 50,
        limit = 50,
        isFinalized = true
    )
)

@Preview(showBackground = true)
@Composable
fun PromosScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        PromosScreen()
    }
}
