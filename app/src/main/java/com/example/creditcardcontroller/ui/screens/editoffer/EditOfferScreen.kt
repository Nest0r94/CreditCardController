package com.example.creditcardcontroller.ui.screens.editoffer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.inputs.OfferDateField
import com.example.creditcardcontroller.ui.composables.inputs.OfferDropdown
import com.example.creditcardcontroller.ui.composables.inputs.OfferOption
import com.example.creditcardcontroller.ui.composables.inputs.OfferRadioGroup
import com.example.creditcardcontroller.ui.composables.inputs.OfferTextField
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun EditOfferScreen(
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    onBack: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    var nombre by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var descuento by rememberSaveable { mutableStateOf("") }
    var montoTope by rememberSaveable { mutableStateOf("") }
    var limitePagos by rememberSaveable { mutableStateOf("") }
    var frecuencia by rememberSaveable { mutableStateOf("Semanal") }
    var fechaMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var modoDescuento by rememberSaveable { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "EDITOR DE BENEFICIOS",
            style = MaterialTheme.typography.labelLarge.copy(
                color = Color(0xFF00FFD1), // Cyan color from image
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
                OfferTextField(label = "Descuento", placeholder = "0", value = descuento, onValueChange = { descuento = it }, trailingText = "%")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                OfferTextField(label = "Monto Tope", placeholder = "Sin tope", value = montoTope, onValueChange = { montoTope = it })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        OfferTextField(label = "Límite de Pagos", placeholder = "Sin límite por defecto", value = limitePagos, onValueChange = { limitePagos = it })
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                OfferDropdown(
                    label = "Frecuencia",
                    options = listOf("Diaria", "Semanal", "Quincenal", "Mensual"),
                    selectedOption = frecuencia,
                    onOptionSelected = { frecuencia = it }
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(modifier = Modifier.weight(1f)) {
                OfferDateField(
                    label = "Día / Fecha",
                    selectedDateMillis = fechaMillis,
                    onDateSelected = { fechaMillis = it }
                )
            }
        }
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
            selectedIndex = modoDescuento,
            onOptionSelected = { modoDescuento = it }
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
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardSelector("Visa Premium", true)
            CardSelector("Saving Card", false)
            CardSelector("Master Gold", false)
        }

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

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Guardar Oferta",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Save
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun CardSelector(name: String, selected: Boolean) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (selected) Color(0xFF00FFD1).copy(alpha = 0.5f) else colors.outlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (selected) Color(0xFF00FFD1) else colors.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurface,
                maxLines = 1
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF00FFD1))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditOfferScreenPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        EditOfferScreen()
    }
}
