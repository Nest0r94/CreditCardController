package com.example.creditcardcontroller.ui.composables.settings

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SettingItemSwitch(
    icon: ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.LightGray,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        onClick = { onCheckedChange(!checked) },
    )
}
