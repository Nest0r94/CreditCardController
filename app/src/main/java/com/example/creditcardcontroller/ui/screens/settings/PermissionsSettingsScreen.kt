package com.example.creditcardcontroller.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.settings.SettingGroup
import com.example.creditcardcontroller.ui.composables.settings.SettingInfoCard
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSwitch

@Composable
fun PermissionsSettingsScreen(modifier: Modifier = Modifier) {
    var cameraPermission by remember { mutableStateOf(false) }
    var locationPermission by remember { mutableStateOf(true) }
    var contactsPermission by remember { mutableStateOf(false) }
    var anonymousAnalysis by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingInfoCard(
            title = "Configuración de Seguridad",
            subtitle = "Tú tienes el control total sobre cómo accedemos a tu información.",
            icon = Icons.Default.Security
        )

        SettingGroup(title = "Accesos del Dispositivo") {
            SettingItemSwitch(
                icon = Icons.Default.CameraAlt,
                iconColor = Color(0xFF9575CD),
                title = "Cámara",
                subtitle = "Permite escanear recibos físicos y códigos QR para pagos instantáneos sin errores de digitación.",
                checked = cameraPermission,
                onCheckedChange = { cameraPermission = it }
            )
            SettingItemSwitch(
                icon = Icons.Default.LocationOn,
                iconColor = Color(0xFF4DB6AC),
                title = "Localización",
                subtitle = "Encuentra cajeros cercanos y recibe ofertas exclusivas de comercios aliados basados en tu ubicación actual.",
                checked = locationPermission,
                onCheckedChange = { locationPermission = it }
            )
            SettingItemSwitch(
                icon = Icons.Default.Contacts,
                iconColor = Color(0xFFE57373),
                title = "Contactos",
                subtitle = "Sincroniza tus contactos para enviar dinero a tus amigos de forma rápida, buscando por nombre o teléfono.",
                checked = contactsPermission,
                onCheckedChange = { contactsPermission = it }
            )
        }

        SettingGroup(title = "Privacidad y Datos") {
            SettingItemSwitch(
                title = "Análisis anónimo",
                subtitle = "Ayúdanos a mejorar la aplicación enviando datos de uso que no te identifican personalmente.",
                checked = anonymousAnalysis,
                onCheckedChange = { anonymousAnalysis = it }
            )
        }
    }
}
