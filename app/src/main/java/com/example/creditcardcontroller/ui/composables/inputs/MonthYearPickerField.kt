package com.example.creditcardcontroller.ui.composables.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthYearPickerField(
    label: String,
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    val formattedDate = remember(selectedDateMillis) {
        selectedDateMillis?.let {
            if (it == 0L) return@let "Seleccionar"
            val date = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
            val formatter = DateTimeFormatter.ofPattern("MM/yy")
            date.format(formatter)
        } ?: "Seleccionar"
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .clickable { showPicker = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (selectedDateMillis != null && selectedDateMillis != 0L) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showPicker) {
            MonthYearPickerDialog(
                initialDateMillis = selectedDateMillis,
                onDismiss = { showPicker = false },
                onDateSelected = {
                    onDateSelected(it)
                    showPicker = false
                }
            )
        }
    }
}

@Composable
fun MonthYearPickerDialog(
    initialDateMillis: Long?,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val initialDate = remember(initialDateMillis) {
        if (initialDateMillis == null || initialDateMillis == 0L) {
            LocalDate.now()
        } else {
            Instant.ofEpochMilli(initialDateMillis).atZone(ZoneOffset.UTC).toLocalDate()
        }
    }

    var selectedMonth by remember { mutableIntStateOf(initialDate.monthValue) }
    var selectedYear by remember { mutableIntStateOf(initialDate.year) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val date = LocalDate.of(selectedYear, selectedMonth, 1)
                onDateSelected(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli())
            }) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Expiración de Tarjeta") },
        text = {
            Column {
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Month Selector
                    Box(modifier = Modifier.weight(1f)) {
                        val months = (1..12).toList()
                        val listState = rememberLazyListState(initialFirstVisibleItemIndex = (selectedMonth - 3).coerceAtLeast(0))

                        LazyColumn(
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(months) { month ->
                                val isSelected = month == selectedMonth
                                Text(
                                    text = Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
                                        .replaceFirstChar { it.uppercase() },
                                    style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .clickable { selectedMonth = month }
                                )
                            }
                        }
                    }

                    VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                    // Year Selector
                    Box(modifier = Modifier.weight(1f)) {
                        val currentYear = LocalDate.now().year
                        val years = (currentYear - 5..currentYear + 20).toList()
                        val listState = rememberLazyListState(
                            initialFirstVisibleItemIndex = (selectedYear - (currentYear - 5) - 2).coerceAtLeast(0)
                        )

                        LazyColumn(
                            state = listState,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(years) { year ->
                                val isSelected = year == selectedYear
                                Text(
                                    text = year.toString(),
                                    style = if (isSelected) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .clickable { selectedYear = year }
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
            }
        }
    )
}
