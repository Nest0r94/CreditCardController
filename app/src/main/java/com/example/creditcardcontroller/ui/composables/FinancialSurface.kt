package com.example.creditcardcontroller.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun FinancialSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background,
        content = content
    )
}
