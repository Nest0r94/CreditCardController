package com.example.creditcardcontroller.ui.screens.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.screens.cards.comp.CardView
import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CardsScreen(
    modifier: Modifier = Modifier,
    onEditCard: (Long) -> Unit,
    onAddCard: () -> Unit
) {
    val context = LocalContext.current
    val tarjetaDao = remember { AppDatabase.getDatabase(context).tarjetaDao() }
    val tarjetas by tarjetaDao.getAllTarjetas().collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tarjetas.isEmpty()) {
                Text(
                    text = "No tenés tarjetas agregadas todavía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
            } else {
                tarjetas.forEach { tarjeta ->
                    val isExpired = tarjeta.vencimientoTarjeta < System.currentTimeMillis()
                    CardView(
                        cardName = tarjeta.nombre,
                        amount = "$0,00",
                        limit = formatCurrency(tarjeta.limiteMensual),
                        closingDate = formatClosingDate(tarjeta.fechaCierreResumen),
                        dueDate = formatDueDate(tarjeta.fechaVencimientoResumen),
                        cardExpiration = formatExpiration(tarjeta.vencimientoTarjeta),
                        usagePercentage = 0f,
                        isExpired = isExpired,
                        onClick = { onEditCard(tarjeta.id) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Agregar Tarjeta",
            onClick = onAddCard,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Add
        )
    }
}

private fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getIntegerInstance(Locale.US)
    return "$${formatter.format(value)}"
}

private fun formatClosingDate(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es"))
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

private fun formatDueDate(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale("es"))
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

private fun formatExpiration(millis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("MM/yy", Locale("es"))
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}
