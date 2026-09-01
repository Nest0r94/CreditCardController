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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.data.local.TipoMedioPago
import com.example.creditcardcontroller.data.local.entities.TarjetaEntity
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.dialogs.UpdateDialog
import com.example.creditcardcontroller.ui.screens.cards.comp.CardView
import com.example.creditcardcontroller.ui.util.proximaFechaDeDia
import kotlinx.coroutines.launch
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
    val scope = rememberCoroutineScope()
    val tarjetaDao = remember { AppDatabase.getDatabase(context).tarjetaDao() }
    val tarjetas by tarjetaDao.getAllTarjetas().collectAsState(initial = emptyList())

    var selectedTarjeta by remember { mutableStateOf<TarjetaEntity?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabTipos = listOf(TipoMedioPago.CREDITO, TipoMedioPago.DEBITO)
    val tipoVisible = tabTipos[selectedTab]
    val tarjetasVisibles = tarjetas.filter { it.tipo == tipoVisible }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TabRow(selectedTabIndex = selectedTab) {
            tabTipos.forEachIndexed { index, tipo ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(tipo.name) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tarjetasVisibles.isEmpty()) {
                Text(
                    text = "No tenés tarjetas de ${tipoVisible.name} agregadas todavía.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                )
            } else {
                tarjetasVisibles.forEach { tarjeta ->
                    val isExpired = tarjeta.vencimientoTarjeta > 0L && tarjeta.vencimientoTarjeta < System.currentTimeMillis()
                    CardView(
                        cardName = tarjeta.nombre,
                        amount = "$0,00",
                        limit = tarjeta.limiteMensual?.let { formatCurrency(it) } ?: "-",
                        closingDate = tarjeta.diaCierreResumen?.let { formatClosingDate(proximaFechaDeDia(it)) } ?: "-",
                        dueDate = tarjeta.diaVencimientoResumen?.let { formatDueDate(proximaFechaDeDia(it)) } ?: "-",
                        cardExpiration = if (tarjeta.vencimientoTarjeta > 0L) formatExpiration(tarjeta.vencimientoTarjeta) else "--",
                        usagePercentage = 0f,
                        isExpired = isExpired,
                        cardType = tarjeta.tipo,
                        onClick = {
                            if (isExpired) {
                                selectedTarjeta = tarjeta
                            } else {
                                onEditCard(tarjeta.id)
                            }
                        }
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

    if (selectedTarjeta != null) {
        UpdateDialog(
            title = "Tarjeta Vencida",
            body = "La tarjeta ${selectedTarjeta!!.nombre} ha vencido. ¿Deseas actualizar la fecha de vencimiento o eliminarla?",
            onDismiss = { selectedTarjeta = null },
            onUpdate = { newDate ->
                scope.launch {
                    tarjetaDao.update(selectedTarjeta!!.copy(vencimientoTarjeta = newDate))
                    selectedTarjeta = null
                }
            },
            onDelete = {
                scope.launch {
                    tarjetaDao.delete(selectedTarjeta!!)
                    selectedTarjeta = null
                }
            },
            initialDateMillis = selectedTarjeta!!.vencimientoTarjeta
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
