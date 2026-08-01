package com.example.creditcardcontroller.ui.composables.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingItemGoto(
    icon: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    SettingItemBase(
        icon = icon?.let {
            {
                IconBox(icon = it, color = iconColor)
            }
        },
        title = title,
        subtitle = subtitle,
        trailing = {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            )
        },
        onClick = onClick,
    )
}
