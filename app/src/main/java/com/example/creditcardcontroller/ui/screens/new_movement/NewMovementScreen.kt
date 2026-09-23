package com.example.creditcardcontroller.ui.screens.new_movement

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.SettingsDataStore
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.TipoMovimiento
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.PresupuestoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.data.local.periodoDeFrecuencia
import com.example.creditcardcontroller.data.local.resumen.expandirEnCuotas
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import com.example.creditcardcontroller.ui.util.periodoVencimientoResumen
import com.example.creditcardcontroller.ui.util.primeraFechaDelResumen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMovementScreen(
    modifier: Modifier = Modifier,
    movementId: Long? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    val settingsDataStore = remember { SettingsDataStore(context) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    val tarjetas by db.tarjetaDao().getAllTarjetas().collectAsState(initial = emptyList())
    val descuentos by db.descuentoDao().getAllDescuentos().collectAsState(initial = emptyList())
    val categorias by db.categoriaDao().getAllCategorias().collectAsState(initial = emptyList())
    val allPresupuestos by db.presupuestoDao().getAllItems().collectAsState(initial = emptyList())
    val presupuestos by db.presupuestoDao()
        .getItemsByMonth(selectedDate.monthValue, selectedDate.year)
        .collectAsState(initial = emptyList())
    val movimientos by db.movimientoDao().getAllMovements().collectAsState(initial = emptyList())
    val editTimeEnabled by settingsDataStore.editTimeEnabledFlow.collectAsState(initial = false)
    val initialInstallmentEnabled by settingsDataStore.initialInstallmentEnabledFlow.collectAsState(initial = false)

    var selectedTipo by remember { mutableStateOf(TipoMovimiento.GASTO) }
    var amount by remember { mutableStateOf("") }
    var selectedCategoriaId by remember { mutableStateOf<Long?>(null) }
    var selectedTarjeta by remember { mutableStateOf<TarjetaEntity?>(null) }
    var selectedPeriodoResumen by remember { mutableStateOf<YearMonth?>(null) }
    var selectedTime by remember { mutableStateOf<LocalTime?>(null) }
    var selectedDescuento by remember { mutableStateOf<DescuentoEntity?>(null) }
    var selectedPresupuestoId by remember { mutableStateOf<Long?>(null) }

    val filteredDescuentos = remember(selectedTarjeta, descuentos) {
        selectedTarjeta?.let { tarjeta ->
            descuentos.filter { it.tarjetasAplicables.contains(tarjeta.id) }
        } ?: emptyList()
    }
    val presupuestosDeGasto = remember(presupuestos) {
        presupuestos.filter { it.tipo == PresupuestoEntity.TIPO_GASTO }
    }
    var descripcion by remember { mutableStateOf("") }
    var esCuotas by remember { mutableStateOf(false) }
    var cantidadCuotas by remember { mutableStateOf(3) }
    var cuotaInicial by remember { mutableStateOf(1) }

    var isLoaded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showResumenDropdown by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showTarjetaDropdown by remember { mutableStateOf(false) }
    var showDescuentoDropdown by remember { mutableStateOf(false) }
    var showPresupuestoDropdown by remember { mutableStateOf(false) }

    val ahorroEstimado = remember(amount, selectedDescuento, movimientos, selectedDate) {
        val amountDouble = amount.toDoubleOrNull() ?: 0.0
        val descuento = selectedDescuento
        if (descuento != null && amountDouble > 0) {
            val potentialAhorro = amountDouble * (descuento.porcentajeDescuento / 100.0)
            
            if (descuento.montoTope > 0) {
                val periodo = periodoDeFrecuencia(descuento.frecuencia, selectedDate)

                val currentPeriodMovements = movimientos.filter { mov ->
                    val movDate = Instant.ofEpochMilli(mov.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
                    mov.descuentoId == descuento.id && movDate in periodo
                }
                
                val usedAhorro = currentPeriodMovements.sumOf { it.montoReintegrable }
                val remainingLimit = maxOf(0.0, descuento.montoTope - usedAhorro)
                
                minOf(potentialAhorro, remainingLimit)
            } else {
                potentialAhorro
            }
        } else {
            0.0
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: LocalTime.now().hour,
        initialMinute = selectedTime?.minute ?: LocalTime.now().minute
    )

    val fechaCompraMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val diaCierreResumen = selectedTarjeta
        ?.takeIf { it.tipo == TipoMedioPago.CREDITO }
        ?.diaCierreResumen
        ?.takeIf { it in 1..31 }
    val periodoResumenCompra = diaCierreResumen?.let {
        periodoVencimientoResumen(fechaCompraMillis, it)
    }
    val opcionesResumen = periodoResumenCompra?.let { periodoCompra ->
        listOf(periodoCompra.minusMonths(1), periodoCompra, periodoCompra.plusMonths(1))
    }.orEmpty()
    val periodoResumenElegido = selectedPeriodoResumen
        ?.takeIf { it in opcionesResumen }
        ?: periodoResumenCompra
    val formatoResumen = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "AR")) }

    LaunchedEffect(movementId, tarjetas, categorias, descuentos) {
        if (movementId != null && !isLoaded && tarjetas.isNotEmpty() && categorias.isNotEmpty()) {
            val mov = db.movimientoDao().getById(movementId)
            if (mov != null) {
                selectedTipo = mov.tipo
                // Si es cuotas, el monto guardado es el de una cuota. Recuperamos el total original.
                val totalAmount = if (mov.esCuotas) mov.monto * mov.cantidadCuotas else mov.monto
                amount = String.format("%.2f", totalAmount).replace(",", ".")
                
                // Limpiar descripción del sufijo de cuotas
                descripcion = mov.descripcion.replace(Regex(" \\(cuota \\d+/\\d+\\)$"), "")
                
                selectedCategoriaId = mov.categoriaId
                selectedTarjeta = tarjetas.find { it.id == mov.tarjetaId }
                selectedDate = Instant.ofEpochMilli(mov.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
                tarjetas.find { it.id == mov.tarjetaId }
                    ?.takeIf { it.tipo == TipoMedioPago.CREDITO }
                    ?.diaCierreResumen
                    ?.takeIf { it in 1..31 }
                    ?.let { diaCierre ->
                        selectedPeriodoResumen = periodoVencimientoResumen(mov.fechaPresentacion, diaCierre)
                    }
                selectedTime = mov.hora?.let { LocalTime.ofNanoOfDay(it * 1_000_000) }
                selectedDescuento = descuentos.find { it.id == mov.descuentoId }
                selectedPresupuestoId = mov.presupuestoId
                esCuotas = mov.esCuotas
                cantidadCuotas = mov.cantidadCuotas
                isLoaded = true
            }
        }
    }

    LaunchedEffect(tarjetas) {
        if (selectedTarjeta == null && tarjetas.isNotEmpty() && movementId == null) {
            selectedTarjeta = tarjetas.first()
        }
    }

    LaunchedEffect(categorias) {
        if (selectedCategoriaId == null && categorias.isNotEmpty() && movementId == null) {
            selectedCategoriaId = categorias.firstOrNull { it.nombre == "Otros" }?.id ?: categorias.first().id
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        selectedPeriodoResumen = null
                        selectedPresupuestoId = null
                    }
                    showDatePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        selectedTime = null
                        showTimePicker = false
                    }) {
                        Text("Limpiar", color = MaterialTheme.colorScheme.error)
                    }
                    TextButton(onClick = { showTimePicker = false }) {
                        Text("Cancelar")
                    }
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TabRow(
            selectedTabIndex = if (selectedTipo == TipoMovimiento.GASTO) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (selectedTipo == TipoMovimiento.GASTO) 0 else 1]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTipo == TipoMovimiento.GASTO,
                onClick = { selectedTipo = TipoMovimiento.GASTO },
                text = { Text("Gasto") }
            )
            Tab(
                selected = selectedTipo == TipoMovimiento.INGRESO,
                onClick = { selectedTipo = TipoMovimiento.INGRESO },
                text = { Text("Ingreso") }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (selectedTipo == TipoMovimiento.GASTO) "VALOR DEL GASTO" else "VALOR DEL INGRESO",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = "$",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(12.dp))
                BasicTextField(
                    value = amount,
                    onValueChange = { 
                        if (it.isEmpty() || it.toDoubleOrNull() != null || it == ".") {
                            amount = it 
                        }
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.width(IntrinsicSize.Min),
                    decorationBox = { innerTextField ->
                        if (amount.isEmpty()) {
                            Text(
                                text = "0.00",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedDescuento != null) {
                            "Ahorro Estimado: $${String.format("%.2f", ahorroEstimado)} (${selectedDescuento!!.nombre})"
                        } else {
                            "Sin promoción seleccionada"
                        },
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Categoría Section
            SectionHeader(title = "Categoría", trailing = {
                Text(
                    text = "VER TODAS", 
                    color = MaterialTheme.colorScheme.primary, 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Bold
                )
            })
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (categorias.isEmpty()) {
                    Text(
                        text = "No hay categorías disponibles",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    categorias.forEach { categoria ->
                        CategoryItemView(
                            name = categoria.nombre,
                            icon = iconoDeCategoria(categoria.icono),
                            color = colorDeCategoria(categoria.color),
                            isSelected = categoria.id == selectedCategoriaId
                        ) { selectedCategoriaId = categoria.id }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TARJETA", 
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        CustomDropdownSelector(
                            text = selectedTarjeta?.let { "${it.nombre}"} ?: "",
                            icon = Icons.Default.CreditCard
                        ) {
                            showTarjetaDropdown = true
                        }
                        DropdownMenu(
                            expanded = showTarjetaDropdown,
                            onDismissRequest = { showTarjetaDropdown = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            tarjetas.forEach { tarjeta ->
                                DropdownMenuItem(
                                    text = { Text(tarjeta.nombre, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    onClick = {
                                        selectedTarjeta = tarjeta
                                        selectedPeriodoResumen = null
                                        showTarjetaDropdown = false
                                        if (selectedDescuento != null && !selectedDescuento!!.tarjetasAplicables.contains(tarjeta.id)) {
                                            selectedDescuento = null
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FECHA", 
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CustomDropdownSelector(
                        text = if (selectedDate == LocalDate.now()) "Hoy" else selectedDate.format(DateTimeFormatter.ofPattern("dd MMM")),
                        icon = Icons.Default.CalendarToday
                    ) {
                        showDatePicker = true
                    }
                }
                
                if (editTimeEnabled) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HORA", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                            style = MaterialTheme.typography.labelSmall, 
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CustomDropdownSelector(
                            text = selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--",
                            icon = Icons.Default.AccessTime
                        ) {
                            showTimePicker = true
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (movementId != null && periodoResumenCompra != null && periodoResumenElegido != null) {
                Text(
                    text = "RESUMEN",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomDropdownSelector(
                        text = periodoResumenElegido.format(formatoResumen).uppercase(Locale("es", "AR")),
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        showResumenDropdown = true
                    }
                    DropdownMenu(
                        expanded = showResumenDropdown,
                        onDismissRequest = { showResumenDropdown = false },
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        opcionesResumen.forEach { periodo ->
                            DropdownMenuItem(
                                text = { Text(periodo.format(formatoResumen).uppercase(Locale("es", "AR"))) },
                                onClick = {
                                    selectedPeriodoResumen = periodo
                                    showResumenDropdown = false
                                }
                            )
                        }
                    }
                }
                Text(
                    text = "Si ves que el movimiento no corresponde con el resumen real de la tarjeta, puedes modificarlo.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "DESCRIPCIÓN", 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Ej: Cena con amigos", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTipo == TipoMovimiento.GASTO) {
                Text(
                    text = "PRESUPUESTO",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    CustomDropdownSelector(
                        text = allPresupuestos.find { it.id == selectedPresupuestoId }?.titulo
                            ?: "Sin presupuesto",
                        icon = Icons.Default.AccountBalance,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        showPresupuestoDropdown = true
                    }
                    DropdownMenu(
                        expanded = showPresupuestoDropdown,
                        onDismissRequest = { showPresupuestoDropdown = false },
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin presupuesto") },
                            onClick = {
                                selectedPresupuestoId = null
                                showPresupuestoDropdown = false
                            }
                        )
                        presupuestosDeGasto.forEach { presupuesto ->
                            DropdownMenuItem(
                                text = { Text(presupuesto.titulo) },
                                onClick = {
                                    selectedPresupuestoId = presupuesto.id
                                    showPresupuestoDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
                text = "APLICAR DESCUENTO", 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                style = MaterialTheme.typography.labelSmall, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                CustomDropdownSelector(
                    text = when {
                        selectedTarjeta == null -> "Seleccione un medio de pago"
                        selectedDescuento != null -> selectedDescuento!!.nombre
                        else -> "Seleccionar promoción..."
                    },
                    icon = Icons.Default.LocalOffer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (selectedTarjeta != null) {
                        showDescuentoDropdown = true
                    }
                }
                DropdownMenu(
                    expanded = showDescuentoDropdown,
                    onDismissRequest = { showDescuentoDropdown = false },
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    filteredDescuentos.forEach { descuento ->
                        DropdownMenuItem(
                            text = { Text(descuento.nombre, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                selectedDescuento = descuento
                                showDescuentoDropdown = false
                            }
                        )
                    }
                    if (filteredDescuentos.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay promociones disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                            onClick = { showDescuentoDropdown = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Compra en cuotas", 
                                color = MaterialTheme.colorScheme.onSurface, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dividir el pago en meses", 
                                color = MaterialTheme.colorScheme.onSurfaceVariant, 
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = esCuotas,
                            onCheckedChange = { esCuotas = it },
                            enabled = selectedTipo == TipoMovimiento.GASTO,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }

                    if (esCuotas && selectedTipo == TipoMovimiento.GASTO) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "CUOTAS", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                                    style = MaterialTheme.typography.labelSmall, 
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(onClick = { 
                                        if (cantidadCuotas > 1) {
                                            cantidadCuotas--
                                            if (cuotaInicial > cantidadCuotas) {
                                                cuotaInicial = cantidadCuotas
                                            }
                                        }
                                    }, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Remove, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = cantidadCuotas.toString(),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 12.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = { cantidadCuotas++ }, modifier = Modifier.size(32.dp)) {
                                        Icon(
                                            imageVector = Icons.Default.Add, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "MENSUALIDAD", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                                    style = MaterialTheme.typography.labelSmall, 
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                val monthlyAmount = (amount.toDoubleOrNull() ?: 0.0) / cantidadCuotas
                                Text(
                                    text = "$ ${String.format("%.2f", monthlyAmount)}",
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        if (initialInstallmentEnabled) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Column {
                                    Text(
                                        text = "CUOTA INICIAL", 
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                                        style = MaterialTheme.typography.labelSmall, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(onClick = { if (cuotaInicial > 1) cuotaInicial-- }, modifier = Modifier.size(32.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Remove, 
                                                contentDescription = null, 
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Text(
                                            text = cuotaInicial.toString(),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 12.dp),
                                            fontWeight = FontWeight.Bold
                                        )
                                        IconButton(onClick = { if (cuotaInicial < cantidadCuotas) cuotaInicial++ }, modifier = Modifier.size(32.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Add, 
                                                contentDescription = null, 
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = if (movementId != null) "Actualizar" else (if (selectedTipo == TipoMovimiento.GASTO) "Guardar Gasto" else "Guardar Ingreso"),
                onClick = {
                    val catId = selectedCategoriaId ?: categorias.firstOrNull()?.id
                    val tarjetaId = selectedTarjeta?.id
                    
                    if (catId != null && tarjetaId != null && amount.toDoubleOrNull() != null) {
                        scope.launch {
                            val tarjetaOriginalId = if (movementId != null) {
                                val original = db.movimientoDao().getById(movementId)
                                if (original != null) {
                                    if (original.cuotaGroupId != null) {
                                        db.movimientoDao().deleteByGroupId(original.cuotaGroupId)
                                    } else {
                                        db.movimientoDao().delete(original)
                                    }
                                }
                                original?.tarjetaId
                            } else {
                                null
                            }

                            val montoTotal = amount.toDoubleOrNull() ?: 0.0
                            val moverACuotas = esCuotas && cantidadCuotas > 1 && selectedTipo == TipoMovimiento.GASTO
                            val fechaPresentacion = if (movementId != null &&
                                periodoResumenCompra != null &&
                                periodoResumenElegido != null &&
                                diaCierreResumen != null
                            ) {
                                if (periodoResumenElegido == periodoResumenCompra) {
                                    fechaCompraMillis
                                } else {
                                    primeraFechaDelResumen(periodoResumenElegido, diaCierreResumen)
                                }
                            } else {
                                fechaCompraMillis
                            }
                            val movimiento = MovimientoEntity(
                                descripcion = descripcion,
                                monto = montoTotal,
                                esCuotas = moverACuotas,
                                cantidadCuotas = if (moverACuotas) cantidadCuotas else 1,
                                numeroCuota = 0,
                                fecha = fechaCompraMillis,
                                fechaPresentacion = fechaPresentacion,
                                categoriaId = catId,
                                tarjetaId = tarjetaId,
                                descuentoId = selectedDescuento?.id,
                                presupuestoId = selectedPresupuestoId.takeIf { selectedTipo == TipoMovimiento.GASTO },
                                montoReintegrable = ahorroEstimado,
                                montoReintegrado = false,
                                hora = selectedTime?.toNanoOfDay()?.div(1_000_000),
                                tipo = selectedTipo
                            )
                            val movimientos = if (moverACuotas) {
                                expandirEnCuotas(movimiento, selectedTarjeta?.diaCierreResumen, if (initialInstallmentEnabled) cuotaInicial else 1)
                            } else {
                                listOf(movimiento)
                            }
                            db.movimientoDao().insertAll(movimientos)
                            if (tarjetaOriginalId != null && tarjetaOriginalId != tarjetaId) {
                                com.example.creditcardcontroller.data.local.resumen.ResumenGenerator(db).recalcular(tarjetaOriginalId)
                            }
                            com.example.creditcardcontroller.data.local.resumen.ResumenGenerator(db).recalcular(tarjetaId)
                            onBack()
                        }
                    } else {
                        Toast.makeText(context, "Por favor complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                icon = Icons.Default.CheckCircle
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title, 
            color = MaterialTheme.colorScheme.onBackground, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold
        )
        trailing()
    }
}

@Composable
fun CategoryItemView(name: String, icon: ImageVector, color: Color, isSelected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = if (isSelected) colors.primaryContainer else colors.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .size(60.dp)
                .clickable { onClick() }
        ) {
            Icon(
                icon,
                contentDescription = name,
                tint = if (isSelected) color else color.copy(alpha = 0.6f),
                modifier = Modifier.padding(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name, 
            color = if (isSelected) colors.onBackground else colors.onSurfaceVariant, 
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun CustomDropdownSelector(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        color = colors.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = colors.onSurfaceVariant, 
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text, 
                color = colors.onSurface, 
                modifier = Modifier.weight(1f), 
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown, 
                contentDescription = null, 
                tint = colors.onSurfaceVariant
            )
        }
    }
}
