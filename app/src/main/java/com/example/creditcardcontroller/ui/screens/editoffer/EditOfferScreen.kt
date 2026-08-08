package com.example.creditcardcontroller.ui.screens.editoffer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.Frecuencia
import com.example.creditcardcontroller.data.local.TipoDescuento
import com.example.creditcardcontroller.data.local.entities.DescuentoEntity
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.screens.promos.comp.CardSelector
import com.example.creditcardcontroller.ui.composables.inputs.OfferDateField
import com.example.creditcardcontroller.ui.composables.inputs.OfferDropdown
import com.example.creditcardcontroller.ui.composables.inputs.OfferOption
import com.example.creditcardcontroller.ui.composables.inputs.OfferRadioGroup
import com.example.creditcardcontroller.ui.composables.inputs.OfferTextField
import com.example.creditcardcontroller.ui.screens.editoffer.comp.DiasHabilesSelector
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import kotlinx.coroutines.launch

private fun esDecimalValido(texto: String): Boolean {
    if (texto.isEmpty()) return true
    if (texto.length > 1 && (texto.startsWith(".") || texto.startsWith(","))) return false
    val tienePunto = texto.contains(".")
    val tieneComa = texto.contains(",")
    if (tienePunto && tieneComa) return false
    return texto.count { it == '.' || it == ',' } <= 1 &&
        texto.filter { it != '.' && it != ',' }.all { it.isDigit() }
}

@Composable
fun EditOfferScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    offerId: Long? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val tarjetaDao = remember { database.tarjetaDao() }
    val descuentoDao = remember { database.descuentoDao() }
    val coroutineScope = rememberCoroutineScope()
    
    val tarjetas by tarjetaDao.getAllTarjetas().collectAsState(initial = emptyList())
    
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var descuento by rememberSaveable { mutableStateOf("") }
    var montoTope by rememberSaveable { mutableStateOf("") }
    var limitePagos by rememberSaveable { mutableStateOf("") }
    var frecuencia by rememberSaveable { mutableStateOf("Semanal") }
    var fechaVencimiento by rememberSaveable { mutableStateOf<Long?>(null) }
    var modoDescuentoIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedTarjetaIds by rememberSaveable { mutableStateOf(setOf<Long>()) }
    var selectedDiasHabiles by rememberSaveable(
        stateSaver = listSaver<Set<Int>, Int>(
            save = { it.toList() },
            restore = { it.toSet() }
        )
    ) { mutableStateOf<Set<Int>>(emptySet()) }

    LaunchedEffect(offerId) {
        if (isEditMode && offerId != null) {
            val entity = descuentoDao.getById(offerId)
            entity?.let {
                nombre = it.nombre
                descripcion = it.descripcion
                descuento = it.porcentajeDescuento.toString()
                montoTope = it.montoTope.toString()
                limitePagos = it.cantidadLimitePagos.toString()
                frecuencia = when (it.frecuencia) {
                    Frecuencia.DIARIA -> "Diaria"
                    Frecuencia.SEMANAL -> "Semanal"
                    Frecuencia.MENSUAL -> "Mensual"
                    Frecuencia.ANUAL -> "Anual"
                }
                fechaVencimiento = it.fechaVencimiento.takeIf { f -> f > 0 }
                modoDescuentoIndex = it.tipoDescuento.ordinal
                selectedTarjetaIds = it.tarjetasAplicables.toSet()
                selectedDiasHabiles = it.diasHabiles.toSet()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "EDITOR DE BENEFICIOS",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF00FFD1),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Configura los detalles de tu promoción bancaria para un seguimiento preciso.",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        OfferTextField(label = "Nombre de la Oferta", placeholder = "Ej: Especial Miércoles", value = nombre, onValueChange = { nombre = it })
        Spacer(modifier = Modifier.height(16.dp))
        OfferTextField(label = "Descripción", placeholder = "Ej: Supermercado Coto Miércoles", value = descripcion, onValueChange = { descripcion = it })
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OfferTextField(
                    label = "Descuento",
                    placeholder = "0",
                    value = descuento,
                    onValueChange = { if (esDecimalValido(it)) descuento = it },
                    trailingText = "%",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                OfferTextField(
                    label = "Monto Tope",
                    placeholder = "Sin tope",
                    value = montoTope,
                    onValueChange = { if (esDecimalValido(it)) montoTope = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OfferTextField(
            label = "Límite de Pagos",
            placeholder = "Sin límite por defecto",
            value = limitePagos,
            onValueChange = { 
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    limitePagos = it 
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OfferDropdown(
                    label = "Frecuencia",
                    options = listOf("Diaria", "Semanal", "Mensual", "Anual"),
                    selectedOption = frecuencia,
                    onOptionSelected = { frecuencia = it }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                OfferDateField(
                    label = "Fecha de vencimiento",
                    selectedDateMillis = fechaVencimiento,
                    onDateSelected = { fechaVencimiento = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        DiasHabilesSelector(
            selectedDays = selectedDiasHabiles,
            onDaysChange = { selectedDiasHabiles = it }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Modo de Descuento / Reintegro",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OfferRadioGroup(
            options = listOf(
                OfferOption("Descuento en pago", "Afecta el precio real de compra"),
                OfferOption("Reintegro en tarjeta", "Confirmación manual posterior"),
                OfferOption("Reintegro en cuenta", "Crédito inmediato en ahorro")
            ),
            selectedIndex = modoDescuentoIndex,
            onOptionSelected = { modoDescuentoIndex = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tarjetas Aplicables",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurfaceVariant
        )
        Text(
            text = "Selecciona las tarjetas que participan de este beneficio",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tarjetas.forEach { tarjeta ->
                CardSelector(
                    name = tarjeta.nombre,
                    selected = selectedTarjetaIds.contains(tarjeta.id),
                    onClick = {
                        selectedTarjetaIds = if (selectedTarjetaIds.contains(tarjeta.id)) {
                            selectedTarjetaIds - tarjeta.id
                        } else {
                            selectedTarjetaIds + tarjeta.id
                        }
                    }
                )
            }
            if (tarjetas.isEmpty()) {
                Text(
                    text = "No hay tarjetas registradas",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        if (isEditMode) {
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceVariant.copy(alpha = 0.3f),
                border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00FFD1).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00FFD1),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Estado de Oferta",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant
                        )
                        Text(
                            text = "Activa",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF00FFD1)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (isEditMode) "Actualizar Oferta" else "Guardar Oferta",
            onClick = {
                coroutineScope.launch {
                    val freq = when (frecuencia) {
                        "Diaria" -> Frecuencia.DIARIA
                        "Semanal" -> Frecuencia.SEMANAL
                        "Mensual" -> Frecuencia.MENSUAL
                        else -> Frecuencia.ANUAL
                    }

                    val entity = DescuentoEntity(
                        id = offerId ?: 0L,
                        nombre = nombre,
                        descripcion = descripcion,
                        porcentajeDescuento = descuento.toDoubleOrNull() ?: 0.0,
                        montoTope = montoTope.toDoubleOrNull() ?: 0.0,
                        cantidadLimitePagos = limitePagos.toIntOrNull() ?: 0,
                        frecuencia = freq,
                        fechaVencimiento = fechaVencimiento ?: 0L,
                        diasHabiles = selectedDiasHabiles.toList(),
                        tarjetasAplicables = selectedTarjetaIds.toList(),
                        tipoDescuento = TipoDescuento.values()[modoDescuentoIndex]
                    )

                    if (isEditMode && offerId != null) {
                        descuentoDao.update(entity)
                    } else {
                        descuentoDao.insert(entity)
                    }
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Save
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun EditOfferScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        EditOfferScreen()
    }
}
