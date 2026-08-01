package com.example.creditcardcontroller.ui.composables.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

data class OfferOption(
    val title: String,
    val subtitle: String
)

@Composable
fun OfferRadioGroup(
    options: List<OfferOption>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        options.forEachIndexed { index, option ->
            OfferOptionRow(
                title = option.title,
                subtitle = option.subtitle,
                selected = index == selectedIndex,
                onClick = { onOptionSelected(index) }
            )
            if (index != options.lastIndex) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun OfferOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) colors.surfaceVariant.copy(alpha = 0.8f) else colors.surfaceVariant.copy(alpha = 0.3f))
            .border(
                1.dp,
                if (selected) Color(0xFF00FFD1).copy(alpha = 0.5f) else colors.outlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF00FFD1))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfferRadioGroupPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        OfferRadioGroup(
            options = listOf(
                OfferOption("Descuento en pago", "Afecta el precio real de compra"),
                OfferOption("Reintegro en tarjeta", "Confirmación manual posterior"),
                OfferOption("Reintegro en cuenta", "Crédito inmediato en ahorro")
            ),
            selectedIndex = 0,
            onOptionSelected = {}
        )
    }
}
