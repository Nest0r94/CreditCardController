package com.example.creditcardcontroller.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.settings.SettingGroup
import com.example.creditcardcontroller.ui.composables.settings.SettingInfoCard
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSwitch

@Composable
fun NotificationsSettingsScreen(modifier: Modifier = Modifier) {
    var monthlyLimit by remember { mutableStateOf(true) }
    var installmentsLimit by remember { mutableStateOf(false) }
    var unusualSpending by remember { mutableStateOf(true) }
    
    var cardExpiry by remember { mutableStateOf(true) }
    var statementClosing by remember { mutableStateOf(true) }
    var newPromos by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingInfoCard(
            title = "Mantente al día",
            subtitle = "Configura cómo y cuándo quieres recibir actualizaciones de tu salud financiera.",
            icon = Icons.Default.NotificationsActive
        )

        SettingGroup(title = "Alertas de Gasto") {
            SettingItemSwitch(
                title = "Límite Mensual alcanzado (80%)",
                subtitle = "Te avisamos antes de que se agote el presupuesto",
                checked = monthlyLimit,
                onCheckedChange = { monthlyLimit = it }
            )
            SettingItemSwitch(
                title = "Límite de Cuotas superado",
                subtitle = "Alerta al exceder el máximo de financiamiento",
                checked = installmentsLimit,
                onCheckedChange = { installmentsLimit = it }
            )
            SettingItemSwitch(
                title = "Gasto inusual detectado",
                subtitle = "Seguridad avanzada por movimientos extraños",
                checked = unusualSpending,
                onCheckedChange = { unusualSpending = it }
            )
        }

        SettingGroup(title = "Recordatorios") {
            SettingItemSwitch(
                title = "Vencimiento de tarjeta",
                subtitle = "3 días antes de la fecha límite",
                checked = cardExpiry,
                onCheckedChange = { cardExpiry = it }
            )
            SettingItemSwitch(
                title = "Cierre de resumen",
                subtitle = "Notificar cuando el ciclo de facturación termina",
                checked = statementClosing,
                onCheckedChange = { statementClosing = it }
            )
            SettingItemSwitch(
                title = "Nuevas promociones",
                subtitle = "Descuentos exclusivos en comercios adheridos",
                checked = newPromos,
                onCheckedChange = { newPromos = it }
            )
        }
    }
}
