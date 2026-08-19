package com.example.creditcardcontroller.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.entities.MovimientoEntity
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria
import com.example.creditcardcontroller.ui.composables.layout.DateHeader
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.composables.layout.MonthPickerDialog
import com.example.creditcardcontroller.ui.composables.layout.YearPickerDialog
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale

@Composable
fun StatsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { AppDatabase.getDatabase(context) }
    
    val movimientos by db.movimientoDao().getAllMovements().collectAsState(initial = emptyList())
    val categorias by db.categoriaDao().getAllCategorias().collectAsState(initial = emptyList())
    val tarjetas by db.tarjetaDao().getAllTarjetas().collectAsState(initial = emptyList())

    var selectedDate by remember { mutableStateOf(YearMonth.now()) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    val filteredMovements = remember(movimientos, selectedDate) {
        movimientos.filter { mov ->
            val movDate = Instant.ofEpochMilli(mov.fecha).atZone(ZoneId.systemDefault()).toLocalDate()
            movDate.year == selectedDate.year && movDate.month == selectedDate.month
        }
    }

    val totalMensual = remember(filteredMovements) {
        filteredMovements.sumOf { it.monto }
    }

    FinancialSurface(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            DateHeader(
                selectedDate = selectedDate,
                onMonthClick = { showMonthPicker = true },
                onYearClick = { showYearPicker = true },
                onPreviousMonth = { selectedDate = selectedDate.minusMonths(1) },
                onNextMonth = { selectedDate = selectedDate.plusMonths(1) }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Gasto Total del Mes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$ ${formatAmount(totalMensual)}",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Text(
                text = "Movimientos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (filteredMovements.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay movimientos para este mes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredMovements) { movimiento ->
                        val categoria = categorias.find { it.id == movimiento.categoriaId }
                        val tarjeta = tarjetas.find { it.id == movimiento.tarjetaId }
                        
                        MovementItem(
                            movimiento = movimiento,
                            categoriaName = categoria?.nombre ?: "Sin categoría",
                            categoriaIcon = iconoDeCategoria(categoria?.icono ?: ""),
                            categoriaColor = colorDeCategoria(categoria?.color ?: "#808080"),
                            tarjetaName = tarjeta?.nombre ?: "Sin tarjeta",
                            onDelete = {
                                scope.launch {
                                    db.movimientoDao().delete(movimiento)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            onDismiss = { showMonthPicker = false },
            onMonthSelected = { month ->
                selectedDate = selectedDate.withMonth(month)
                showMonthPicker = false
            }
        )
    }

    if (showYearPicker) {
        YearPickerDialog(
            currentYear = selectedDate.year,
            onDismiss = { showYearPicker = false },
            onYearSelected = { year ->
                selectedDate = selectedDate.withYear(year)
                showYearPicker = false
            }
        )
    }
}

@Composable
fun MovementItem(
    movimiento: MovimientoEntity,
    categoriaName: String,
    categoriaIcon: androidx.compose.ui.graphics.vector.ImageVector,
    categoriaColor: Color,
    tarjetaName: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoriaColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoriaIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.descripcion.ifBlank { categoriaName },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tarjetaName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (movimiento.esCuotas) {
                        Text(
                            text = " • ${movimiento.cantidadCuotas} cuotas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$ ${formatAmount(movimiento.monto)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp).padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun formatAmount(amount: Double): String {
    return String.format(Locale.US, "%,.2f", amount)
        .replace(',', 'X')
        .replace('.', ',')
        .replace('X', '.')
}
