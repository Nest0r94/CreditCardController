package com.example.creditcardcontroller.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionButtonRow(
    primaryLabel: String,
    secondaryLabel: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onPrimaryClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(primaryLabel)
        }

        OutlinedButton(
            onClick = onSecondaryClick,
            modifier = Modifier.weight(1f),
            border = BorderStroke(1.dp, colors.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(secondaryLabel)
        }
    }
}
