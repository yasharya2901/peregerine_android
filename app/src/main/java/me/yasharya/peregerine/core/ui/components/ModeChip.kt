package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ModeChip(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val bg = when {
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}