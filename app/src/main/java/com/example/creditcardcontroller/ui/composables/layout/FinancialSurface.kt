package com.example.creditcardcontroller.ui.composables.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun FinancialSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier.fillMaxSize(),
        color = colors.background,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun FinancialSurfacePreview() {
    CreditCardControllerTheme {
        FinancialSurface {
            Text(text = "Contenido dentro de la superficie")
        }
    }
}
