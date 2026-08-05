package com.example.creditcardcontroller.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppTheme
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.ui.composables.settings.IconBox
import com.example.creditcardcontroller.ui.composables.settings.SettingGroup
import com.example.creditcardcontroller.ui.composables.settings.SettingInfoCard
import com.example.creditcardcontroller.ui.composables.settings.SettingItemBase
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSelection
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSwitch
import com.example.creditcardcontroller.ui.composables.settings.ThemeSelector
import kotlinx.coroutines.launch

@Composable
fun AppPreferencesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsDataStore = remember { SettingsDataStore(context) }
    
    val currentTheme by settingsDataStore.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
    
    var biometryEnabled by remember { mutableStateOf(true) }
    var autoLockEnabled by remember { mutableStateOf(false) }

    val themeLabel = when (currentTheme) {
        AppTheme.DARK -> "Oscuro"
        AppTheme.LIGHT -> "Claro"
        AppTheme.SYSTEM -> "Sistema"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        SettingInfoCard(
            title = "Configuración",
            subtitle = "Personaliza tu experiencia financiera",
            icon = Icons.Default.Settings
        )

        SettingGroup(title = "Visualización") {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SettingItemSelection(
                    label = "Idioma",
                    value = "Español",
                    onClick = { Toast.makeText(context, "Seleccionar Idioma", Toast.LENGTH_SHORT).show() }
                )
                SettingItemSelection(
                    label = "Moneda Principal",
                    value = "ARS ($)",
                    onClick = { Toast.makeText(context, "Seleccionar Moneda", Toast.LENGTH_SHORT).show() }
                )
                
                Text(
                    text = "Tema",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                ThemeSelector(
                    selectedTheme = themeLabel,
                    onThemeSelected = { label ->
                        val newTheme = when (label) {
                            "Oscuro" -> AppTheme.DARK
                            "Claro" -> AppTheme.LIGHT
                            else -> AppTheme.SYSTEM
                        }
                        scope.launch {
                            settingsDataStore.setTheme(newTheme)
                        }
                    }
                )
            }
        }

        SettingGroup(title = "Seguridad") {
            SettingItemSwitch(
                title = "Acceso con Biometría",
                subtitle = "FaceID o Huella dactilar",
                checked = biometryEnabled,
                onCheckedChange = { biometryEnabled = it }
            )
            SettingItemSwitch(
                title = "Bloqueo automático",
                subtitle = "Al salir o minimizar la app",
                checked = autoLockEnabled,
                onCheckedChange = { autoLockEnabled = it }
            )
        }

        SettingGroup(title = "Gestión de Datos") {
            SettingItemBase(
                icon = { 
                    IconBox(
                        icon = Icons.Default.CleaningServices, 
                        color = androidx.compose.ui.graphics.Color(0xFFE57373)
                    ) 
                },
                title = "Limpiar caché",
                subtitle = "Libera 124 MB de espacio",
                trailing = {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                onClick = { Toast.makeText(context, "Caché limpiado", Toast.LENGTH_SHORT).show() }
            )
            SettingItemBase(
                icon = { 
                    IconBox(
                        icon = Icons.Default.Download, 
                        color = androidx.compose.ui.graphics.Color(0xFF4DB6AC)
                    ) 
                },
                title = "Exportar mis datos",
                subtitle = "Descargar historial en CSV",
                trailing = {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                onClick = { Toast.makeText(context, "Exportando datos...", Toast.LENGTH_SHORT).show() }
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Versión 2.4.1 (Build 882)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Text(
                text = "Hecho con ❤️ para tu libertad financiera",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
