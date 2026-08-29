package com.example.creditcardcontroller.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.content.FileProvider
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.AppTheme
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.data.local.backup.BackupManager
import com.example.creditcardcontroller.ui.composables.settings.IconBox
import com.example.creditcardcontroller.ui.composables.settings.SettingGroup
import com.example.creditcardcontroller.ui.composables.settings.SettingInfoCard
import com.example.creditcardcontroller.ui.composables.settings.SettingItemBase
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSelection
import com.example.creditcardcontroller.ui.composables.settings.SettingItemSwitch
import com.example.creditcardcontroller.ui.composables.settings.ThemeSelector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AppPreferencesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsDataStore = remember { SettingsDataStore(context) }
    
    val currentTheme by settingsDataStore.themeFlow.collectAsState(initial = AppTheme.SYSTEM)
    val biometricEnabled by settingsDataStore.biometricEnabledFlow.collectAsState(initial = false)
    val autoLockEnabled by settingsDataStore.autoLockEnabledFlow.collectAsState(initial = false)

    val themeLabel = when (currentTheme) {
        AppTheme.DARK -> "Oscuro"
        AppTheme.LIGHT -> "Claro"
        AppTheme.SYSTEM -> "Sistema"
    }

    val db = remember { AppDatabase.getDatabase(context) }
    val backupManager = remember { BackupManager(db) }

    var exporting by remember { mutableStateOf(false) }
    var importing by remember { mutableStateOf(false) }
    var pendingRestore by remember { mutableStateOf<Uri?>(null) }

    fun shareBackup() {
        if (exporting) return
        exporting = true
        scope.launch {
            try {
                val json = backupManager.exportarJson()
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val fileName = "backup_datos_$timestamp.json"
                val file = File(context.cacheDir, fileName)
                withContext(Dispatchers.IO) { file.writeText(json) }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/json"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_SUBJECT, "Backup de datos - Credit Card Controller")
                }
                context.startActivity(Intent.createChooser(sendIntent, "Compartir backup"))
            } catch (e: Exception) {
                Toast.makeText(context, "Error al exportar datos", Toast.LENGTH_SHORT).show()
            } finally {
                exporting = false
            }
        }
    }

    fun performRestore(uri: Uri) {
        if (importing) return
        importing = true
        scope.launch {
            val success = try {
                val json = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                }
                if (json == null) {
                    false
                } else {
                    backupManager.restaurarJson(json)
                }
            } catch (e: Exception) {
                false
            } finally {
                importing = false
            }
            val text = if (success) "Datos restaurados correctamente" else "Error al restaurar datos"
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            pendingRestore = it
        }
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
                checked = biometricEnabled,
                onCheckedChange = { 
                    scope.launch {
                        settingsDataStore.setBiometricEnabled(it)
                    }
                }
            )
            if (biometricEnabled) {
                SettingItemSwitch(
                    title = "Bloqueo automático",
                    subtitle = "Al salir o minimizar la app",
                    checked = autoLockEnabled,
                    onCheckedChange = {
                        scope.launch {
                            settingsDataStore.setAutoLockEnabled(it)
                        }
                    }
                )
            }
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
                subtitle = "Compartir backup en JSON (WhatsApp, mail, etc.)",
                trailing = {
                    if (exporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                },
                onClick = { shareBackup() }
            )
            SettingItemBase(
                icon = { 
                    IconBox(
                        icon = Icons.Default.Upload, 
                        color = androidx.compose.ui.graphics.Color(0xFF64B5F6)
                    ) 
                },
                title = "Importar datos",
                subtitle = "Restaurar desde un archivo de backup",
                trailing = {
                    if (importing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                },
                onClick = { importLauncher.launch(arrayOf("application/json")) }
            )
        }

        pendingRestore?.let { uri ->
            AlertDialog(
                onDismissRequest = { pendingRestore = null },
                title = { Text("Confirmar restauración") },
                text = {
                    Text("Esto reemplazará todos tus datos actuales con los del archivo de backup. ¿Continuar?")
                },
                confirmButton = {
                    TextButton(onClick = {
                        pendingRestore = null
                        performRestore(uri)
                    }) {
                        Text("Restaurar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingRestore = null }) {
                        Text("Cancelar")
                    }
                }
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
