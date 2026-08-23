package com.example.creditcardcontroller.ui.screens.balances.comp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SummaryCard(gastoActual: Double, presupuesto: Double, gastoCuotas: Double, gastoUnPago: Double) {
    val progress = (gastoActual / presupuesto).coerceIn(0.0, 1.0).toFloat()
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
                    val totalGastoCalculado = gastoCuotas + gastoUnPago
                    val cuotasRatio = if (totalGastoCalculado > 0) (gastoCuotas / totalGastoCalculado).toFloat() else 0f
                    val unPagoRatio = if (totalGastoCalculado > 0) (gastoUnPago / totalGastoCalculado).toFloat() else 0f

                    Column(modifier = Modifier.weight(1f)) {
                        Text("GASTO EN CUOTAS", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                        Text(formatCurrency(gastoCuotas), style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { cuotasRatio },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(6.dp).clip(CircleShape),
                            color = Color(0xFF4DB6AC),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("GASTO EN UN PAGO", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                        Text(formatCurrency(gastoUnPago), style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { unPagoRatio },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp).height(6.dp).clip(CircleShape),
                            color = Color(0xFF4DB6AC),
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF4DB6AC),
                    strokeWidth = 8.dp,
                    trackColor = Color.White.copy(alpha = 0.2f),
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardItem(tarjeta: TarjetaEntity) {
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
                text = "BALANCE DISPONIBLE",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF78909C)
            )
            Text(
                text = formatCurrency(tarjeta.limiteMensual), // Simplified
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row {
                Column {
                    Text("CIERRE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF78909C))
                    Text(formatDateShort(tarjeta.fechaCierreResumen), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                }
                Spacer(modifier = Modifier.width(24.dp))
                Column {
                    Text("VENCE", style = MaterialTheme.typography.labelSmall, color = Color(0xFF78909C))
                    Text(formatDateShort(tarjeta.fechaVencimientoResumen), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
                }
            }
        }
    }
}

@Composable
fun CategoryExpensesSection(movimientos: List<MovimientoEntity>, categorias: List<CategoriaEntity>) {
    val categoryTotals = movimientos.groupBy { it.categoriaId }
        .mapValues { it.value.sumOf { m -> m.monto } }
    
    val totalAmount = categoryTotals.values.sum()
    
    val categoryPercentages = if (totalAmount > 0) {
        categoryTotals.mapValues { (it.value / totalAmount) * 100 }
    } else {
        emptyMap()
    }

    val sortedCategories = categorias.filter { categoryTotals.containsKey(it.id) }
        .sortedByDescending { categoryTotals[it.id] }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Gastos por Categoría",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                    DonutChart(categoryPercentages, categorias)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("100%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    sortedCategories.take(3).forEach { cat ->
                        val percentage = categoryPercentages[cat.id] ?: 0.0
                        CategoryLegendItem(cat.nombre, "${percentage.toInt()}%", colorDeCategoria(cat.color))
                    }
                }
            }
        }
    }
}

@Composable
fun DonutChart(percentages: Map<Long, Double>, categorias: List<CategoriaEntity>) {
    Canvas(modifier = Modifier.size(120.dp)) {
        var startAngle = -90f
        percentages.forEach { (catId, percentage) ->
            val sweepAngle = (percentage.toFloat() / 100f) * 360f
            val cat = categorias.find { it.id == catId }
            val color = colorDeCategoria(cat?.color ?: "#757575")
            
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
        
        if (percentages.isEmpty()) {
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun CategoryLegendItem(name: String, percentage: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(name, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
        Text(percentage, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
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
fun MovementItem(movimiento: MovimientoEntity, categoria: CategoriaEntity?, tarjetaNombre: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                    formatDateFull(movimiento.fecha),
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
            }
        }
        
        Text(
            "-${formatCurrency(movimiento.monto)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFAB91)
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

fun formatDateFull(timestamp: Long): String {
    val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return date.format(DateTimeFormatter.ofPattern("dd MMM, HH:mm"))
}
