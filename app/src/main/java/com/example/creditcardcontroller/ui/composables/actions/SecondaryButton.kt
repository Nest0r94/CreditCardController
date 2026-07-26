package com.example.creditcardcontroller.ui.composables.actions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(1.dp, colors.primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun SecondaryButtonPreview() {
    CreditCardControllerTheme {
        SecondaryButton(
            text = "Guardar",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
