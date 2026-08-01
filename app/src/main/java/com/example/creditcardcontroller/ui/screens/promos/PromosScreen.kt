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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.screens.promos.comp.PromoCard
import com.example.creditcardcontroller.ui.screens.promos.comp.PromoData
import com.example.creditcardcontroller.ui.screens.promos.comp.PromosSearchBar
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun PromosScreen(
    modifier: Modifier = Modifier,
    onAddPromo: () -> Unit = {},
    onEditPromo: (PromoData) -> Unit = {}
) {
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
        PromosSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
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
                PromoCard(promo, onClick = { onEditPromo(promo) })
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
                PromoCard(promo, onClick = { onEditPromo(promo) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Agregar Promoción",
            onClick = onAddPromo,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            icon = Icons.Default.Add
        )
    }
}

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
