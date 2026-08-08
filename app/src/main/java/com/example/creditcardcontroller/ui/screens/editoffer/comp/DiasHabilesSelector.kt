package com.example.creditcardcontroller.ui.screens.editoffer.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme
import java.util.Calendar

private val diasDeLaSemana = listOf(
    "Lunes" to Calendar.MONDAY,
    "Martes" to Calendar.TUESDAY,
    "Miércoles" to Calendar.WEDNESDAY,
    "Jueves" to Calendar.THURSDAY,
    "Viernes" to Calendar.FRIDAY,
    "Sábado" to Calendar.SATURDAY,
    "Domingo" to Calendar.SUNDAY
)

@Composable
fun DiasHabilesSelector(
    selectedDays: Set<Int>,
    onDaysChange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val allDaysValues = diasDeLaSemana.map { it.second }.toSet()
    val isAllSelected = selectedDays.size == diasDeLaSemana.size

    Column(modifier = modifier) {
        Text(
            text = "Días Hábiles",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Selecciona los días de la semana",
            style = MaterialTheme.typography.bodySmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // "Todos los días" option
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        1.dp,
                        if (isAllSelected) Color(0xFF00FFD1).copy(alpha = 0.6f) else colors.outlineVariant.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        if (isAllSelected) {
                            onDaysChange(emptySet())
                        } else {
                            onDaysChange(allDaysValues)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Bold,
                    color = if (isAllSelected) Color(0xFF00FFD1) else colors.onSurface,
                    textAlign = TextAlign.Center
                )
            }

            diasDeLaSemana.forEach { (nombre, valor) ->
                val selected = selectedDays.contains(valor)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.5f))
                        .border(
                            1.dp,
                            if (selected) Color(0xFF00FFD1).copy(alpha = 0.6f) else colors.outlineVariant.copy(alpha = 0.2f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            onDaysChange(
                                if (selected) selectedDays - valor else selectedDays + valor
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = nombre.take(2),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color(0xFF00FFD1) else colors.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiasHabilesSelectorPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        DiasHabilesSelector(
            selectedDays = setOf(Calendar.MONDAY, Calendar.WEDNESDAY),
            onDaysChange = {}
        )
    }
}
