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

@Composable
fun MainScaffold() {
    var currentRoute by remember { mutableStateOf("inicio") }

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
                else -> "Credit Card Controller"
            }
            Toolbar(title = title)
        },
        bottomBar = {
            BottomNavigationBar(currentRoute = currentRoute, onNavigate = { currentRoute = it })
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (currentRoute) {
            "inicio" -> HomeScreen(modifier = modifier)
            "tarjetas" -> CardsScreen(modifier = modifier)
            "nuevo" -> NewMovementScreen(modifier = modifier)
            "estadisticas" -> StatsScreen(modifier = modifier)
            "promos" -> PromosScreen(modifier = modifier)
            "ajustes" -> SettingsScreen(modifier = modifier)
            else -> HomeScreen(modifier = modifier)
        }
    }
}
