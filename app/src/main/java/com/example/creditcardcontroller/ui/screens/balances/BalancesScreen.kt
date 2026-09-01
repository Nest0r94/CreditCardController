package com.example.creditcardcontroller.ui.screens.balances

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.ui.composables.layout.DateHeader
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
import com.example.creditcardcontroller.ui.composables.layout.MonthPickerDialog
import com.example.creditcardcontroller.ui.composables.layout.YearPickerDialog
import com.example.creditcardcontroller.ui.screens.balances.comp.*
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun BalancesScreen(
    modifier: Modifier = Modifier,
    viewModel: BalancesViewModel = viewModel(
        factory = BalancesViewModel.Factory(
            AppDatabase.getDatabase(LocalContext.current).tarjetaDao(),
            AppDatabase.getDatabase(LocalContext.current).movimientoDao(),
            AppDatabase.getDatabase(LocalContext.current).categoriaDao(),
            AppDatabase.getDatabase(LocalContext.current).presupuestoDao(),
        )
    )
) {
    val state by viewModel.uiState.collectAsState()
    var showMonthPicker by remember { mutableStateOf(false) }
    var showYearPicker by remember { mutableStateOf(false) }

    FinancialSurface(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                DateHeader(
                    selectedDate = state.selectedDate,
                    onMonthClick = { showMonthPicker = true },
                    onYearClick = { showYearPicker = true },
                    onPreviousMonth = { viewModel.updateSelectedDate(state.selectedDate.minusMonths(1)) },
                    onNextMonth = { viewModel.updateSelectedDate(state.selectedDate.plusMonths(1)) }
                )
            }

            item {
                SummaryCard(
                    gastoActual = state.gastoMensual,
                    presupuesto = state.totalPresupuesto,
                    gastoCuotas = state.gastoCuotas,
                    gastoUnPago = state.gastoUnPago,
                    limiteCuotas = state.limiteCuotas,
                    limiteUnPago = state.limiteUnPago
                )
            }

            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tus Tarjetas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "VER TODO",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 0.dp)
                    ) {
                        items(state.tarjetas) { tarjeta ->
                            CardItem(tarjeta = tarjeta)
                        }
                    }
                }
            }

            item {
                CategoryExpensesSection(
                    movimientos = state.movimientos,
                    categorias = state.categorias
                )
            }

            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Movimientos del mes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "VER TODAS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    MovementsFilter(
                        tarjetas = state.tarjetas,
                        selectedId = state.selectedTarjetaId,
                        onSelect = viewModel::selectTarjeta
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    state.movimientos.forEach { movimiento ->
                        val categoria = state.categorias.find { it.id == movimiento.categoriaId }
                        val tarjeta = state.tarjetas.find { it.id == movimiento.tarjetaId }
                        MovementItem(
                            movimiento = movimiento,
                            categoria = categoria,
                            tarjetaNombre = tarjeta?.nombre ?: "Desconocida"
                        )
                    }
                }
            }
        }

        if (showMonthPicker) {
            MonthPickerDialog(
                onDismiss = { showMonthPicker = false },
                onMonthSelected = { month ->
                    viewModel.updateSelectedDate(state.selectedDate.withMonth(month))
                    showMonthPicker = false
                }
            )
        }

        if (showYearPicker) {
            YearPickerDialog(
                currentYear = state.selectedDate.year,
                onDismiss = { showYearPicker = false },
                onYearSelected = { year ->
                    viewModel.updateSelectedDate(state.selectedDate.withYear(year))
                    showYearPicker = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BalancesScreenPreview() {
    CreditCardControllerTheme {
        BalancesScreen()
    }
}
