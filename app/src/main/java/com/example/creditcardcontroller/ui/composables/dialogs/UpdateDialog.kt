package com.example.creditcardcontroller.ui.composables.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.creditcardcontroller.ui.composables.actions.PrimaryButton
import com.example.creditcardcontroller.ui.composables.actions.TextActionButton
import com.example.creditcardcontroller.ui.composables.inputs.FormInput
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun UpdateDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit,
    initialDate: String = "20/05/2024"
) {
    Dialog(onDismissRequest = onDismiss) {
        UpdateDialogContent(
            title = title,
            body = body,
            onDismiss = onDismiss,
            onUpdate = onUpdate,
            onDelete = onDelete,
            initialDate = initialDate
        )
    }
}

@Composable
fun UpdateDialogContent(
    title: String,
    body: String,
    onDismiss: () -> Unit,
    onUpdate: (String) -> Unit,
    onDelete: () -> Unit,
    initialDate: String = "20/05/2024",
    modifier: Modifier = Modifier
) {
    var dateValue by remember { mutableStateOf(initialDate) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Body
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Input
            FormInput(
                label = "Actualizar fecha de vencimiento",
                value = dateValue,
                onValueChange = { dateValue = it },
                trailingIcon = Icons.Default.CalendarToday,
                uppercaseLabel = false
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Actions
            PrimaryButton(
                text = "Actualizar",
                onClick = { onUpdate(dateValue) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextActionButton(
                text = "Eliminar",
                onClick = onDelete,
                icon = Icons.Default.Delete,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogDarkPreview() {
    CreditCardControllerTheme(darkTheme = true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                UpdateDialogContent(
                    title = "Oferta Vencida",
                    body = "Esta promoción ha expirado. Qué deseas hacer?",
                    onDismiss = {},
                    onUpdate = {},
                    onDelete = {}
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDialogLightPreview() {
    CreditCardControllerTheme(darkTheme = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                UpdateDialogContent(
                    title = "Sesión Expirada",
                    body = "Tu sesión ha terminado por inactividad. Por favor, vuelve a ingresar.",
                    onDismiss = {},
                    onUpdate = {},
                    onDelete = {}
                )
            }
        }
    }
}
