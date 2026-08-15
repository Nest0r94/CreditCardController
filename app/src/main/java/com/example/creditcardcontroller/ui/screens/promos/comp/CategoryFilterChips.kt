package com.example.creditcardcontroller.ui.screens.promos.comp

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.data.local.entities.CategoriaEntity
import com.example.creditcardcontroller.ui.composables.categories.colorDeCategoria
import com.example.creditcardcontroller.ui.composables.categories.iconoDeCategoria

@Composable
fun CategoryFilterChips(
    categorias: List<CategoriaEntity>,
    selectedIds: Set<Long>,
    onToggle: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categorias.forEach { categoria ->
            val selected = selectedIds.contains(categoria.id)
            val color = colorDeCategoria(categoria.color)
            FilterChip(
                selected = selected,
                onClick = { onToggle(categoria.id) },
                label = { 
                    Text(
                        text = categoria.nombre,
                        style = if (selected) MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold) 
                               else MaterialTheme.typography.labelLarge
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = iconoDeCategoria(categoria.icono),
                        contentDescription = null,
                        tint = if (selected) color else color.copy(alpha = 0.6f)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = color.copy(alpha = 0.35f),
                    selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                    selectedLeadingIconColor = color,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = color.copy(alpha = 0.5f),
                    selectedBorderColor = color,
                    borderWidth = if (selected) 2.dp else 1.dp
                )
            )
        }
    }
}
