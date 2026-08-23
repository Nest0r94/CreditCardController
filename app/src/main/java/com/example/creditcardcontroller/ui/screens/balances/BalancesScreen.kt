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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.creditcardcontroller.data.local.AppDatabase
import com.example.creditcardcontroller.ui.composables.layout.FinancialSurface
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
        )
    )
) {
    val state by viewModel.uiState.collectAsState()

    FinancialSurface(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 24.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { BalancesHeader() }

            item {
                SummaryCard(
                    gastoActual = state.gastoMensual,
                    presupuesto = state.totalPresupuesto,
                    gastoCuotas = state.gastoCuotas,
                    gastoUnPago = state.gastoUnPago
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
    }
}

@Preview(showBackground = true)
@Composable
fun BalancesScreenPreview() {
    CreditCardControllerTheme {
        BalancesScreen()
    }
}
