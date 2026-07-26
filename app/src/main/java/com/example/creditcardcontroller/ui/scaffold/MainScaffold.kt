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
import com.example.creditcardcontroller.ui.screens.HomeScreen

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
            else -> Text(
                text = "Pantalla de ${currentRoute.replaceFirstChar { it.uppercase() }}",
                modifier = modifier.padding(24.dp)
            )
        }
    }
}
