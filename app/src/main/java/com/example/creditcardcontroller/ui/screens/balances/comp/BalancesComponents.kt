package com.example.creditcardcontroller.ui.screens.balances.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.data.local.TipoMovimiento
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import com.example.creditcardcontroller.ui.composables.feedback.LimitProgressBar
import com.example.creditcardcontroller.ui.util.proximaFechaDeDia
import com.example.creditcardcontroller.ui.util.proximaFechaDeVencimiento
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SummaryCard(
    gastoActual: Double, 
    presupuesto: Double, 
    gastoCuotas: Double, 
    gastoUnPago: Double,
    limiteCuotas: Double,
    limiteUnPago: Double
) {
    val rawProgress = if (presupuesto > 0) (gastoActual / presupuesto).toFloat() else 0f
    val progress = rawProgress.coerceIn(0f, 1f)
    val mainColor = getProgressColor(rawProgress)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF7E57C2), Color(0xFF5E35B1))
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PERÍODO ACTUAL",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatCurrency(gastoActual),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "de un presupuesto de ${formatCurrency(presupuesto)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    val cuotasRatio = if (limiteCuotas > 0) (gastoCuotas / limiteCuotas).toFloat() else 0f
                    val unPagoRatio = if (limiteUnPago > 0) (gastoUnPago / limiteUnPago).toFloat() else 0f

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GASTO EN UN PAGO",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        LimitProgressBar(
                            progress = unPagoRatio,
                            leftLabel = formatCurrency(gastoUnPago),
                            bottomLabel = "TOTAL ${formatCurrency(limiteUnPago)}",
                            modifier = Modifier.fillMaxWidth(),
                            labelColor = Color.White,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            barHeight = 6.dp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GASTO EN CUOTAS",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold
                        )
                        LimitProgressBar(
                            progress = cuotasRatio,
                            leftLabel = formatCurrency(gastoCuotas),
                            bottomLabel = "TOTAL ${formatCurrency(limiteCuotas)}",
                            modifier = Modifier.fillMaxWidth(),
                            labelColor = Color.White,
                            trackColor = Color.White.copy(alpha = 0.2f),
                            barHeight = 6.dp
                        )
                    }
                }
            }
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp).padding(top = 8.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = mainColor,
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(rawProgress * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardItem(tarjeta: TarjetaEntity, consumoDelMes: Double) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8EAF6)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = tarjeta.nombre.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            Text(
                text = "CONSUMO DEL MES",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF78909C)
            )
            Text(
                text = formatCurrency(consumoDelMes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row {
                Column {
                    Text("CIERRE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF78909C))
                    Text(tarjeta.diaCierreResumen?.let { formatDateShort(proximaFechaDeDia(it)) } ?: "-", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text("VENCE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF78909C))
                    Text(tarjeta.diaVencimientoResumen?.let {
                        formatDateShort(proximaFechaDeVencimiento(tarjeta.primerVencimientoResumen, it))
                    } ?: "-", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                }
            }
        }
    }
}

@Composable
fun MovementsFilter(tarjetas: List<TarjetaEntity>, selectedId: Long?, onSelect: (Long?) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        item {
            FilterChip(
                selected = selectedId == null,
                onClick = { onSelect(null) },
                label = { Text("TODAS") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFC2C1FF),
                    selectedLabelColor = Color(0xFF1A09A1)
                )
            )
        }
        items(tarjetas) { tarjeta ->
            FilterChip(
                selected = selectedId == tarjeta.id,
                onClick = { onSelect(tarjeta.id) },
                label = { Text(tarjeta.nombre.uppercase()) }
            )
        }
    }
}

@Composable
fun MovementItem(
    movimiento: MovimientoEntity,
    categoria: CategoriaEntity?,
    tarjetaNombre: String,
    presupuestoNombre: String? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(
                imageVector = iconoDeCategoria(categoria?.icono ?: "MoreHoriz"),
                contentDescription = null,
                modifier = Modifier.padding(12.dp),
                tint = colorDeCategoria(categoria?.color ?: "#757575")
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                movimiento.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    formatDateFull(movimiento.fecha, movimiento.hora),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        tarjetaNombre.uppercase(),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp
                    )
                }
                if (movimiento.tipo == TipoMovimiento.GASTO && !presupuestoNombre.isNullOrBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            presupuestoNombre.uppercase(),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 8.sp
                        )
                    }
                }
            }
        }
        
        val color = when (movimiento.tipo) {
            TipoMovimiento.GASTO -> Color(0xFFFFAB91)
            TipoMovimiento.INGRESO -> Color(0xFF81C784)
            TipoMovimiento.REINTEGRO -> Color(0xFF64B5F6)
        }
        val prefix = when (movimiento.tipo) {
            TipoMovimiento.GASTO -> "-"
            TipoMovimiento.INGRESO -> "+"
            TipoMovimiento.REINTEGRO -> ""
        }

        Text(
            "$prefix${formatCurrency(movimiento.monto)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

// Helpers
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("es", "AR"))
    return format.format(amount)
}

fun formatDateShort(timestamp: Long): String {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd MMM"))
}

fun formatDateFull(timestamp: Long, hora: Long?): String {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    val datePart = date.format(DateTimeFormatter.ofPattern("dd MMM"))
    return if (hora != null) {
        val time = LocalTime.ofNanoOfDay(hora * 1_000_000)
        "$datePart, ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}"
    } else {
        datePart
    }
}

private fun getProgressColor(progress: Float): Color {
    return if (progress >= 1f) {
        Color(0xFFF44336) // Rojo si es 100% o más
    } else {
        lerp(
            start = Color(0xFF4CAF50), // Verde
            stop = Color(0xFFFF9800),  // Naranja
            fraction = progress
        )
    }
}
