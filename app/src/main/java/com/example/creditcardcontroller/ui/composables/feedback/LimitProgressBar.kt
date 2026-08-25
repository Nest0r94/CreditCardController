package com.example.creditcardcontroller.ui.composables.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.creditcardcontroller.ui.theme.CreditCardControllerTheme

@Composable
fun LimitProgressBar(
    progress: Float,
    leftLabel: String,
    modifier: Modifier = Modifier,
    rightLabel: String? = null,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    indicatorColor: Color? = null,
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
    barHeight: Dp = 4.dp
) {
    val finalIndicatorColor = indicatorColor ?: getProgressColor(progress)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = leftLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = labelColor
            )
            if (rightLabel != null) {
                Text(
                    text = rightLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = labelColor
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
                .clip(RoundedCornerShape(2.dp)),
            color = finalIndicatorColor,
            trackColor = trackColor,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}

private fun getProgressColor(progress: Float): Color {
    return if (progress >= 1f) {
        Color(0xFFF44336) // Rojo
    } else {
        lerp(
            start = Color(0xFF4CAF50), // Verde
            stop = Color(0xFFFF9800),  // Naranja
            fraction = progress
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LimitProgressBarPreview() {
    CreditCardControllerTheme {
        LimitProgressBar(
            progress = 0.3f,
            leftLabel = "Reembolsado $1500 / $5000",
            rightLabel = "30%",
            modifier = Modifier.padding(16.dp)
        )
    }
}
