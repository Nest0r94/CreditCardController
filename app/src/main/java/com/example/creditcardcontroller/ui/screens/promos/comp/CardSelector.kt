package com.example.creditcardcontroller.ui.screens.promos.comp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CardSelector(name: String, selected: Boolean, onClick: () -> Unit = {}) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surfaceVariant.copy(alpha = 0.5f))
            .border(
                1.dp,
                if (selected) Color(0xFF00FFD1).copy(alpha = 0.5f) else colors.outlineVariant.copy(alpha = 0.2f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.CreditCard,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (selected) Color(0xFF00FFD1) else colors.onSurfaceVariant
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onSurface,
                maxLines = 1
            )
        }
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF00FFD1))
            )
        }
    }
}
