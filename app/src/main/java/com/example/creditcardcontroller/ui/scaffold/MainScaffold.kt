package com.example.creditcardcontroller.ui.scaffold

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import com.example.creditcardcontroller.ui.screens.home.HomeScreen
import com.example.creditcardcontroller.ui.screens.cards.CardsScreen
import com.example.creditcardcontroller.ui.screens.new_movement.NewMovementScreen
import com.example.creditcardcontroller.ui.screens.stats.StatsScreen
import com.example.creditcardcontroller.ui.screens.promos.PromosScreen
import com.example.creditcardcontroller.ui.screens.settings.SettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.NotificationsSettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.PermissionsSettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.AppPreferencesScreen
import com.example.creditcardcontroller.ui.screens.settings.HelpCenterScreen
import com.example.creditcardcontroller.ui.screens.editcard.EditCardScreen
import com.example.creditcardcontroller.ui.screens.editoffer.EditOfferScreen
import com.example.creditcardcontroller.ui.screens.balance.BalanceScreen

@Composable
fun MainScaffold() {
    var currentRoute by remember { mutableStateOf("balance") }
    var isEditMode by remember { mutableStateOf(false) }
    var editingTarjetaId by remember { mutableStateOf<Long?>(null) }
    var isPromoEditMode by remember { mutableStateOf(false) }
    var editingPromoId by remember { mutableStateOf<Long?>(null) }

    BackHandler(enabled = currentRoute != "balance") {
        currentRoute = when (currentRoute) {
            "editar_tarjeta" -> "tarjetas"
            "editar_oferta" -> "promos"
            "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias", "ajustes_ayuda" -> "ajustes"
            else -> "balance"
        }
    }

    val isSubScreen = currentRoute in listOf("editar_tarjeta", "editar_oferta", "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias", "ajustes_ayuda")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val title = when (currentRoute) {
                "inicio" -> "Balances"
                "balance" -> "Presupuesto"
                "tarjetas" -> "Mis Tarjetas"
                "nuevo" -> "Nuevo Movimiento"
                "estadisticas" -> "Datos"
                "promos" -> "Promociones"
                "ajustes" -> "Ajustes"
                "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias", "ajustes_ayuda" -> "Configuración"
                "editar_tarjeta" -> if (isEditMode) "Editar Tarjeta" else "Agregar Tarjeta"
                "editar_oferta" -> if (isPromoEditMode) "Editar Oferta" else "Agregar Oferta"
                else -> "Credit Card Controller"
            }
            
            val onBack: (() -> Unit)? = when (currentRoute) {
                "editar_tarjeta" -> { { currentRoute = "tarjetas" } }
                "editar_oferta" -> { { currentRoute = "promos" } }
                "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias", "ajustes_ayuda" -> { { currentRoute = "ajustes" } }
                else -> null
            }

            Toolbar(
                title = title,
                onBack = onBack,
                actions = {
                    if (currentRoute == "balance") {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (!isSubScreen) {
                BottomNavigationBar(currentRoute = currentRoute, onNavigate = { currentRoute = it })
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentRoute) {
            "inicio" -> HomeScreen(modifier = modifier)
            "balance" -> BalanceScreen(modifier = modifier)
            "tarjetas" -> CardsScreen(
                modifier = modifier,
                onEditCard = { tarjetaId ->
                    isEditMode = true
                    editingTarjetaId = tarjetaId
                    currentRoute = "editar_tarjeta"
                },
                onAddCard = {
                    isEditMode = false
                    editingTarjetaId = null
                    currentRoute = "editar_tarjeta"
                }
            )
            "editar_tarjeta" -> EditCardScreen(
                modifier = modifier,
                tarjetaId = editingTarjetaId,
                onBack = { currentRoute = "tarjetas" }
            )
            "nuevo" -> NewMovementScreen(
                modifier = modifier,
                onBack = { currentRoute = "balance" }
            )
            "estadisticas" -> StatsScreen(modifier = modifier)
            "promos" -> PromosScreen(
                modifier = modifier,
                onAddPromo = {
                    isPromoEditMode = false
                    editingPromoId = null
                    currentRoute = "editar_oferta"
                },
                onEditPromo = { promo ->
                    isPromoEditMode = true
                    editingPromoId = promo.id
                    currentRoute = "editar_oferta"
                }
            )
            "editar_oferta" -> EditOfferScreen(
                modifier = modifier,
                isEditMode = isPromoEditMode,
                offerId = editingPromoId,
                onBack = { currentRoute = "promos" }
            )
            "ajustes" -> SettingsScreen(
                modifier = modifier,
                onNavigate = { currentRoute = it }
            )
            "ajustes_notificaciones" -> NotificationsSettingsScreen(modifier = modifier)
            "ajustes_permisos" -> PermissionsSettingsScreen(modifier = modifier)
            "ajustes_preferencias" -> AppPreferencesScreen(modifier = modifier)
            "ajustes_ayuda" -> HelpCenterScreen(modifier = modifier, onBack = { currentRoute = "ajustes" })
            else -> HomeScreen(modifier = modifier)
        }
    }
}
