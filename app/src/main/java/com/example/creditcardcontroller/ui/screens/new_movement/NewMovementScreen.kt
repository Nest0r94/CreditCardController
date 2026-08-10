package com.example.creditcardcontroller.ui.screens.new_movement

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMovementScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    
    val tarjetas by db.tarjetaDao().getAllTarjetas().collectAsState(initial = emptyList())
    val descuentos by db.descuentoDao().getAllDescuentos().collectAsState(initial = emptyList())
    val categorias by db.categoriaDao().getAllCategorias().collectAsState(initial = emptyList())

    var amount by remember { mutableStateOf("") }
    var selectedCategoriaId by remember { mutableStateOf<Long?>(null) }
    var selectedTarjeta by remember { mutableStateOf<TarjetaEntity?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedDescuento by remember { mutableStateOf<DescuentoEntity?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var esCuotas by remember { mutableStateOf(false) }
    var cantidadCuotas by remember { mutableStateOf(3) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTarjetaDropdown by remember { mutableStateOf(false) }
    var showDescuentoDropdown by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    LaunchedEffect(tarjetas) {
        if (selectedTarjeta == null && tarjetas.isNotEmpty()) {
            selectedTarjeta = tarjetas.first()
        }
    }

    LaunchedEffect(categorias) {
        if (selectedCategoriaId == null && categorias.isNotEmpty()) {
            selectedCategoriaId = categorias.firstOrNull { it.nombre == "Otros" }?.id ?: categorias.first().id
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Custom Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Regresar", 
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "Añadir Gasto",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Text(
            text = "VALOR DEL GASTO",
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
                    text = "Ahorro Estimado: $450.00 (Promo Visa)",
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
                        text = selectedTarjeta?.let { "${it.nombre} •• ${it.id % 100}" } ?: "Visa •• 42",
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
                                    showTarjetaDropdown = false
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
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                text = selectedDescuento?.nombre ?: "Seleccionar promoción...",
                icon = Icons.Default.LocalOffer,
                modifier = Modifier.fillMaxWidth()
            ) {
                showDescuentoDropdown = true
            }
            DropdownMenu(
                expanded = showDescuentoDropdown,
                onDismissRequest = { showDescuentoDropdown = false },
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                descuentos.forEach { descuento ->
                    DropdownMenuItem(
                        text = { Text(descuento.nombre, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        onClick = {
                            selectedDescuento = descuento
                            showDescuentoDropdown = false
                        }
                    )
                }
                if (descuentos.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay promociones disponibles", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                        onClick = { showDescuentoDropdown = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        // Compra en cuotas Card
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
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                if (esCuotas) {
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
                                IconButton(onClick = { if (cantidadCuotas > 1) cantidadCuotas-- }, modifier = Modifier.size(32.dp)) {
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
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Guardar Gasto",
            onClick = {
                scope.launch {
                    val catId = selectedCategoriaId ?: categorias.firstOrNull()?.id ?: 0
                    val movement = MovimientoEntity(
                        descripcion = descripcion,
                        monto = amount.toDoubleOrNull() ?: 0.0,
                        esCuotas = esCuotas,
                        cantidadCuotas = if (esCuotas) cantidadCuotas else 1,
                        fecha = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        categoriaId = catId,
                        tarjetaId = selectedTarjeta?.id ?: 0,
                        descuentoId = selectedDescuento?.id,
                        montoReintegrable = 0.0,
                        montoReintegrado = false
                    )
                    db.movimientoDao().insert(movement)
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            icon = Icons.Default.CheckCircle
        )
        
        Spacer(modifier = Modifier.height(24.dp))
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
