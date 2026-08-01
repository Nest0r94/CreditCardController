package com.example.creditcardcontroller.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.settings.SettingGroup
import com.example.creditcardcontroller.ui.composables.settings.SettingItemBase
import com.example.creditcardcontroller.ui.composables.settings.SettingItemGoto

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val onNotImplemented = {
        Toast.makeText(context, "NO IMPLEMENTADO", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingGroup(title = "Ajustes") {
            SettingItemGoto(
                icon = Icons.Default.NotificationsNone,
                iconColor = Color(0xFF64B5F6),
                title = "Notificaciones",
                subtitle = "Configura tus alertas y avisos",
                onClick = onNotImplemented
            )
            SettingItemGoto(
                icon = Icons.Default.Security,
                iconColor = Color(0xFF4DB6AC),
                title = "Permisos",
                subtitle = "Administra los accesos de la aplicación",
                onClick = onNotImplemented
            )
            SettingItemGoto(
                icon = Icons.Default.Settings,
                iconColor = Color(0xFFE57373),
                title = "Preferencias de la App",
                subtitle = "Idioma, tema y notificaciones push",
                onClick = onNotImplemented
            )
        }

        SettingGroup(title = "Soporte") {
            SettingItemGoto(
                icon = Icons.Default.HelpOutline,
                iconColor = Color(0xFF90A4AE),
                title = "Centro de Ayuda",
                subtitle = "Preguntas frecuentes y tutoriales",
                onClick = onNotImplemented
            )
        }
    }
}
