package com.example.creditcardcontroller.ui.screens.onboarding

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddChart
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.data.local.backup.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val settingsDataStore = remember { SettingsDataStore(context) }
    val backupManager = remember { BackupManager(db) }
    val viewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModel.Factory(db.presupuestoDao(), settingsDataStore, backupManager)
    )

    var currentStep by remember { mutableIntStateOf(1) }
    var importing by remember { mutableStateOf(false) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            importing = true
            scope.launch {
                try {
                    val json = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(selectedUri)?.bufferedReader()?.use { it.readText() }
                    }
                    if (json != null) {
                        viewModel.restoreBackup(json) {
                            Toast.makeText(context, "Backup restaurado con éxito", Toast.LENGTH_SHORT).show()
                            onFinished()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al cargar el backup", Toast.LENGTH_SHORT).show()
                } finally {
                    importing = false
                }
            }
        }
    }

    val ingreso by viewModel.ingresoMensual.collectAsState()
    val limite1 by viewModel.limiteUnPago.collectAsState()
    val limiteC by viewModel.limiteCuotas.collectAsState()
    val tax by viewModel.impuestoSellos.collectAsState()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (currentStep > 1) {
                    TextButton(onClick = { currentStep-- }) {
                        Text("Atrás")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 4) {
                            currentStep++
                        } else {
                            viewModel.completeOnboarding(onFinished)
                        }
                    },
                    enabled = when(currentStep) {
                        1 -> ingreso.isNotEmpty()
                        2 -> limite1.isNotEmpty()
                        3 -> limiteC.isNotEmpty()
                        4 -> tax.isNotEmpty()
                        else -> false
                    }
                ) {
                    Text(if (currentStep < 4) "Siguiente" else "Comenzar")
                    if (currentStep < 4) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val isSelected = currentStep == index + 1
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                    )
                }
            }

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "onboarding_step"
            ) { step ->
                when (step) {
                    1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        StepContent(
                            title = "Ingreso Mensual",
                            description = "¿Cuál es tu sueldo mensual?",
                            icon = Icons.Default.AddChart,
                            value = ingreso,
                            onValueChange = viewModel::updateIngreso,
                            label = "Monto del ingreso"
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "O si ya usaste la app antes:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        TextButton(
                            onClick = { importLauncher.launch(arrayOf("application/json")) },
                            enabled = !importing
                        ) {
                            if (importing) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Restaurar desde Backup")
                            }
                        }
                    }
                    2 -> StepContent(
                        title = "Límite 1 Pago",
                        description = "Ingresá un límite para tus gastos mensuales en tarjetas de créditos en 1 pago. No permitas que el resumen de tu tarjeta sobrepase tu salario.",
                        icon = Icons.Default.CreditCard,
                        value = limite1,
                        onValueChange = viewModel::updateLimiteUnPago,
                        label = "Límite 1 pago"
                    )
                    3 -> StepContent(
                        title = "Límite en Cuotas",
                        description = "Ingresá un límite para la cantidad de plata que vas a gastar en cuotas en un mes. Controlá las cuotas por separado para evitar que las cuotas se coman todo el gasto mensual de crédito.",
                        icon = Icons.Default.AccountBalanceWallet,
                        value = limiteC,
                        onValueChange = viewModel::updateLimiteCuotas,
                        label = "Límite cuotas"
                    )
                    4 -> StepContent(
                        title = "Impuesto a los Sellos",
                        description = "Cada provincia tiene su propio impuesto que afecta directamente al resumen de la tarjeta.",
                        icon = Icons.Default.Percent,
                        value = tax,
                        onValueChange = viewModel::updateImpuestoSellos,
                        label = "Porcentaje (%)"
                    )
                }
            }
        }
    }
}

@Composable
private fun StepContent(
    title: String,
    description: String,
    icon: ImageVector,
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            prefix = { if (title.contains("Impuesto")) null else Text("$ ") },
            suffix = { if (title.contains("Impuesto")) Text("%") else null },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = MaterialTheme.shapes.large
        )
    }
}
