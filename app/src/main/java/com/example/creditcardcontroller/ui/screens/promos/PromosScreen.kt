package com.example.creditcardcontroller.ui.screens.promos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.TipoDescuento
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.dialogs.UpdateDialog
import com.example.creditcardcontroller.ui.screens.promos.comp.PromoCard
import com.example.creditcardcontroller.ui.screens.promos.comp.PromoData
import com.example.creditcardcontroller.ui.screens.promos.comp.PromosSearchBar
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PromosScreen(
    modifier: Modifier = Modifier,
    onAddPromo: () -> Unit = {},
    onEditPromo: (DescuentoEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val dao = remember { AppDatabase.getDatabase(context).descuentoDao() }
    val descuentos by dao.getAllDescuentos().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedDescuento by remember { mutableStateOf<DescuentoEntity?>(null) }

    val filteredDescuentos = descuentos.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) ||
        it.descripcion.contains(searchQuery, ignoreCase = true)
    }

    val promosHoy = filteredDescuentos.filter { it.isActiveToday() }
    val otrasPromos = filteredDescuentos.filter { !it.isActiveToday() }

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
            if (promosHoy.isNotEmpty()) {
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

                items(promosHoy) { descuento ->
                    PromoCard(
                        promo = descuento.toPromoData(),
                        onClick = {
                            if (descuento.isExpired()) {
                                selectedDescuento = descuento
                            } else {
                                onEditPromo(descuento)
                            }
                        }
                    )
                }
            }

            if (otrasPromos.isNotEmpty()) {
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

                items(otrasPromos) { descuento ->
                    PromoCard(
                        promo = descuento.toPromoData(),
                        onClick = {
                            if (descuento.isExpired()) {
                                selectedDescuento = descuento
                            } else {
                                onEditPromo(descuento)
                            }
                        }
                    )
                }
            }

            if (descuentos.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(top = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay promociones registradas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

    if (selectedDescuento != null) {
        UpdateDialog(
            title = "Oferta Vencida",
            body = "Esta promoción ha expirado (${selectedDescuento?.nombre}). ¿Qué deseas hacer?",
            initialDateMillis = selectedDescuento!!.fechaVencimiento,
            onDismiss = { selectedDescuento = null },
            onUpdate = { newDateMillis ->
                scope.launch {
                    dao.update(selectedDescuento!!.copy(fechaVencimiento = newDateMillis))
                    selectedDescuento = null
                }
            },
            onDelete = {
                scope.launch {
                    dao.delete(selectedDescuento!!)
                    selectedDescuento = null
                }
            }
        )
    }
}

private fun DescuentoEntity.isExpired(): Boolean {
    if (fechaVencimiento <= 0) return false
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val expiryDateStr = sdf.format(Date(fechaVencimiento))
    val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    return expiryDateStr < todayStr
}

private fun DescuentoEntity.isExpiringToday(): Boolean {
    if (fechaVencimiento <= 0) return false
    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val expiryDateStr = sdf.format(Date(fechaVencimiento))
    val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    return expiryDateStr == todayStr
}

private fun DescuentoEntity.isActiveToday(): Boolean {
    val calendar = Calendar.getInstance()
    val todayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    
    if (this.isExpired()) return false
    
    return diasHabiles.isEmpty() || diasHabiles.contains(todayOfWeek)
}

private fun DescuentoEntity.toPromoData(): PromoData {
    val icon = when (tipoDescuento) {
        TipoDescuento.EN_PAGO -> Icons.Default.ShoppingBag
        TipoDescuento.REINTEGRO_TARJETA -> Icons.Default.CreditCard
        TipoDescuento.REINTEGRO_CUENTA -> Icons.Default.AccountBalance
    }

    val isExpired = this.isExpired()
    val isExpiringToday = this.isExpiringToday()

    val expiryStr = if (fechaVencimiento > 0) {
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        sdf.format(Date(fechaVencimiento))
    } else {
        "Sin límite"
    }

    return PromoData(
        title = nombre,
        description = descripcion,
        icon = icon,
        expiryDate = when {
            isExpiringToday -> "Hoy"
            isExpired -> "VENCIDO"
            else -> expiryStr
        },
        reimbursed = 0,
        limit = montoTope.toInt(),
        isFinalized = isExpired
    )
}

@Preview(showBackground = true)
@Composable
fun PromosScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        PromosScreen()
    }
}
