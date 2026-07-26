package com.example.creditcardcontroller.ui.scaffold

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        NavigationItem("Inicio", Icons.Default.Home, "inicio"),
        NavigationItem("Tarjetas", Icons.Default.CreditCard, "tarjetas"),
        NavigationItem("Nuevo", Icons.Default.Add, "nuevo"),
        NavigationItem("Datos", Icons.Default.BarChart, "estadisticas"),
        NavigationItem("Promos", Icons.Default.LocalOffer, "promos"),
        NavigationItem("Ajustes", Icons.Default.Settings, "ajustes"),
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
