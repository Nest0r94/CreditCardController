package com.example.creditcardcontroller.ui.scaffold

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.creditcardcontroller.ui.screens.home.HomeScreen
import com.example.creditcardcontroller.ui.screens.cards.CardsScreen
import com.example.creditcardcontroller.ui.screens.new_movement.NewMovementScreen
import com.example.creditcardcontroller.ui.screens.stats.StatsScreen
import com.example.creditcardcontroller.ui.screens.promos.PromosScreen
import com.example.creditcardcontroller.ui.screens.settings.SettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.NotificationsSettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.PermissionsSettingsScreen
import com.example.creditcardcontroller.ui.screens.settings.AppPreferencesScreen
import com.example.creditcardcontroller.ui.screens.editcard.EditCardScreen
import com.example.creditcardcontroller.ui.screens.editoffer.EditOfferScreen

@Composable
fun MainScaffold() {
    var currentRoute by remember { mutableStateOf("inicio") }
    var isEditMode by remember { mutableStateOf(false) }
    var editingTarjetaId by remember { mutableStateOf<Long?>(null) }
    var isPromoEditMode by remember { mutableStateOf(false) }

    val isSubScreen = currentRoute in listOf("editar_tarjeta", "editar_oferta", "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val title = when (currentRoute) {
                "inicio" -> "Inicio"
                "tarjetas" -> "Mis Tarjetas"
                "nuevo" -> "Nuevo Movimiento"
                "estadisticas" -> "Estadísticas"
                "promos" -> "Promociones"
                "ajustes" -> "Ajustes"
                "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias" -> "Configuración"
                "editar_tarjeta" -> if (isEditMode) "Editar Tarjeta" else "Agregar Tarjeta"
                "editar_oferta" -> if (isPromoEditMode) "Editar Oferta" else "Agregar Oferta"
                else -> "Credit Card Controller"
            }
            
            val onBack: (() -> Unit)? = when (currentRoute) {
                "editar_tarjeta" -> { { currentRoute = "tarjetas" } }
                "editar_oferta" -> { { currentRoute = "promos" } }
                "ajustes_notificaciones", "ajustes_permisos", "ajustes_preferencias" -> { { currentRoute = "ajustes" } }
                else -> null
            }

            Toolbar(title = title, onBack = onBack)
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
                onBack = { currentRoute = "inicio" }
            )
            "estadisticas" -> StatsScreen(modifier = modifier)
            "promos" -> PromosScreen(
                modifier = modifier,
                onAddPromo = {
                    isPromoEditMode = false
                    currentRoute = "editar_oferta"
                },
                onEditPromo = {
                    isPromoEditMode = true
                    currentRoute = "editar_oferta"
                }
            )
            "editar_oferta" -> EditOfferScreen(
                modifier = modifier,
                isEditMode = isPromoEditMode,
                onBack = { currentRoute = "promos" }
            )
            "ajustes" -> SettingsScreen(
                modifier = modifier,
                onNavigate = { currentRoute = it }
            )
            "ajustes_notificaciones" -> NotificationsSettingsScreen(modifier = modifier)
            "ajustes_permisos" -> PermissionsSettingsScreen(modifier = modifier)
            "ajustes_preferencias" -> AppPreferencesScreen(modifier = modifier)
            else -> HomeScreen(modifier = modifier)
        }
    }
}
