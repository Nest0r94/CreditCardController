package com.example.creditcardcontroller.ui.composables.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

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
