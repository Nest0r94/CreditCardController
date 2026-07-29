package com.example.creditcardcontroller.ui.scaffold

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.screens.home.HomeScreen
import com.example.creditcardcontroller.ui.screens.cards.CardsScreen
import com.example.creditcardcontroller.ui.screens.new_movement.NewMovementScreen
import com.example.creditcardcontroller.ui.screens.stats.StatsScreen
import com.example.creditcardcontroller.ui.screens.promos.PromosScreen
import com.example.creditcardcontroller.ui.screens.settings.SettingsScreen

import com.example.creditcardcontroller.ui.screens.editcard.EditCardScreen

@Composable
fun MainScaffold() {
    var currentRoute by remember { mutableStateOf("inicio") }
    var isEditMode by remember { mutableStateOf(false) }

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
                "editar_tarjeta" -> if (isEditMode) "Editar Tarjeta" else "Agregar Tarjeta"
                else -> "Credit Card Controller"
            }
            
            val onBack: (() -> Unit)? = if (currentRoute == "editar_tarjeta") {
                { currentRoute = "tarjetas" }
            } else null

            Toolbar(title = title, onBack = onBack)
        },
        bottomBar = {
            if (currentRoute != "editar_tarjeta") {
                BottomNavigationBar(currentRoute = currentRoute, onNavigate = { currentRoute = it })
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentRoute) {
            "inicio" -> HomeScreen(modifier = modifier)
            "tarjetas" -> CardsScreen(
                modifier = modifier,
                onEditCard = {
                    isEditMode = true
                    currentRoute = "editar_tarjeta"
                },
                onAddCard = {
                    isEditMode = false
                    currentRoute = "editar_tarjeta"
                }
            )
            "editar_tarjeta" -> EditCardScreen(
                modifier = modifier,
                isEditMode = isEditMode,
                onBack = { currentRoute = "tarjetas" }
            )
            "nuevo" -> NewMovementScreen(modifier = modifier)
            "estadisticas" -> StatsScreen(modifier = modifier)
            "promos" -> PromosScreen(modifier = modifier)
            "ajustes" -> SettingsScreen(modifier = modifier)
            else -> HomeScreen(modifier = modifier)
        }
    }
}
