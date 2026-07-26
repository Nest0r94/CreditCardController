package com.example.creditcardcontroller.ui.composables.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun FinancialHeader(title: String, subtitle: String) {
    val colors = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FinancialHeaderPreview() {
    CreditCardControllerTheme {
        FinancialHeader(
            title = "Control financiero",
            subtitle = "Tu centro de mando para tarjetas, metas y gastos."
        )
    }
}
