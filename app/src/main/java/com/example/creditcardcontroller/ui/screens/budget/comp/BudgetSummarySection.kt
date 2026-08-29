package com.example.creditcardcontroller.ui.screens.budget.comp

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.composables.cards.MetricChip
import com.example.creditcardcontroller.ui.screens.budget.model.formatAmount

@Composable
fun BudgetSummarySection(
    totalIncome: Double,
    gastosChip: Double,
    ahorroChip: Double
) {
    val savingsColor = Color(0xFFFFC107) // Amarillo para Ahorro
    val expenseColor = Color(0xFFF44336)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Table/Grid of metrics
        Column(
            modifier = Modifier.weight(1.1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Ingresos",
                value = "$ ${formatAmount(totalIncome)}",
                color = Color(0xFF4CAF50)
            )
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Gastos",
                value = "$ ${formatAmount(gastosChip)}",
                color = expenseColor
            )
            MetricChip(
                modifier = Modifier.fillMaxWidth(),
                title = "Ahorro",
                value = "$ ${formatAmount(ahorroChip)}",
                color = savingsColor
            )
        }

        // Pie Chart
        Box(
            modifier = Modifier
                .weight(0.9f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            val total = totalIncome.coerceAtLeast(1.0)
            val expenseAngle = ((gastosChip.coerceAtLeast(0.0)) / total * 360f).toFloat().coerceIn(0f, 360f)
            val savingsAngle = 360f - expenseAngle

            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = expenseColor,
                    startAngle = -90f,
                    sweepAngle = expenseAngle,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx())
                )
                drawArc(
                    color = savingsColor, // Amarillo para la porción de ahorro
                    startAngle = -90f + expenseAngle,
                    sweepAngle = savingsAngle,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx())
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val percentage = if (totalIncome > 0) (ahorroChip / totalIncome * 100).toInt().coerceIn(0, 100) else 0
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Ahorro",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
