package com.example.creditcardcontroller.ui.composables.actions

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    CreditCardControllerTheme {
        PrimaryButton(
            text = "Transferir",
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
