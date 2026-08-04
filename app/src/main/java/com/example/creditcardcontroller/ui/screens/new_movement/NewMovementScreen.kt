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
    var selectedCategory by remember { mutableStateOf("Otros") }
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
            .background(Color(0xFF0F172A))
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = Color.White)
            }
            Text(
                text = "Añadir Gasto",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = Color(0xFF1E293B)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Text(
            text = "VALOR DEL GASTO",
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(
                text = "$",
                color = Color(0xFF6366F1),
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
                    color = Color.White,
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
                                color = Color.DarkGray,
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
            color = Color(0xFF1E293B),
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
                    tint = Color(0xFF2DD4BF),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ahorro Estimado: $450.00 (Promo Visa)",
                    color = Color(0xFF2DD4BF),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Categoría Section
        SectionHeader(title = "Categoría", trailing = {
            Text("VER TODAS", color = Color(0xFF6366F1), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        })
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val categoriesItems = listOf(
                "Comida" to Icons.Default.Restaurant,
                "Compras" to Icons.Default.ShoppingBag,
                "Transporte" to Icons.Default.DirectionsCar,
                "Ocio" to Icons.Default.Movie,
                "Otros" to Icons.Default.MoreHoriz
            )
            categoriesItems.forEach { (name, icon) ->
                CategoryItemView(name, icon, selectedCategory == name) { selectedCategory = name }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("TARJETA", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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
                        modifier = Modifier.background(Color(0xFF1E293B))
                    ) {
                        tarjetas.forEach { tarjeta ->
                            DropdownMenuItem(
                                text = { Text(tarjeta.nombre, color = Color.White) },
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
                Text("FECHA", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
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

        Text("APLICAR DESCUENTO", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
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
                modifier = Modifier.fillMaxWidth().background(Color(0xFF1E293B))
            ) {
                descuentos.forEach { descuento ->
                    DropdownMenuItem(
                        text = { Text(descuento.nombre, color = Color.White) },
                        onClick = {
                            selectedDescuento = descuento
                            showDescuentoDropdown = false
                        }
                    )
                }
                if (descuentos.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No hay promociones disponibles", color = Color.Gray) },
                        onClick = { showDescuentoDropdown = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("DESCRIPCIÓN", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            placeholder = { Text("Ej: Cena con amigos", color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFF1E293B),
                focusedContainerColor = Color(0xFF1E293B),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF6366F1),
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Compra en cuotas Card
        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF312E81),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFFFB7185),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Compra en cuotas", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Dividir el pago en meses", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(
                        checked = esCuotas,
                        onCheckedChange = { esCuotas = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF6366F1),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFF0F172A)
                        )
                    )
                }

                if (esCuotas) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFF334155)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("CUOTAS", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(onClick = { if (cantidadCuotas > 1) cantidadCuotas-- }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.White)
                                }
                                Text(
                                    text = cantidadCuotas.toString(),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = { cantidadCuotas++ }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("MENSUALIDAD", color = Color.Gray, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            val monthlyAmount = (amount.toDoubleOrNull() ?: 0.0) / cantidadCuotas
                            Text(
                                text = "$ ${String.format("%.2f", monthlyAmount)}",
                                color = Color(0xFF2DD4BF),
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
                    val catId = categorias.find { it.nombre == selectedCategory }?.id ?: 1L
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
        Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        trailing()
    }
}

@Composable
fun CategoryItemView(name: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = if (isSelected) Color(0xFF312E81) else Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .size(60.dp)
                .clickable { onClick() }
        ) {
            Icon(
                icon,
                contentDescription = name,
                tint = if (isSelected) Color(0xFF6366F1) else Color.Gray,
                modifier = Modifier.padding(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, color = if (isSelected) Color.White else Color.Gray, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun CustomDropdownSelector(text: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        color = Color(0xFF1E293B),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, color = Color.White, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
        }
    }
}
