package com.example.creditcardcontroller.ui.composables.layout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateHeader(
    selectedDate: YearMonth,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                contentDescription = "Mes anterior",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onMonthClick() }
            )
            Text(
                text = " ",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedDate.year.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onYearClick() }
            )
        }

        IconButton(onClick = onNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Mes siguiente",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MonthPickerDialog(
    onDismiss: () -> Unit,
    onMonthSelected: (Int) -> Unit
) {
    val months = (1..12).toList()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Mes") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp)
            ) {
                items(months) { month ->
                    val monthName = YearMonth.of(2000, month).month.getDisplayName(TextStyle.SHORT, Locale("es", "ES"))
                    TextButton(
                        onClick = { onMonthSelected(month) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(monthName.uppercase())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun YearPickerDialog(
    currentYear: Int,
    onDismiss: () -> Unit,
    onYearSelected: (Int) -> Unit
) {
    val years = (currentYear - 5..currentYear + 5).toList()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Año") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.height(200.dp)
            ) {
                items(years) { year ->
                    TextButton(
                        onClick = { onYearSelected(year) },
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(year.toString())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
