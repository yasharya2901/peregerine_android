package me.yasharya.peregerine.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.core.util.formatQty
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.ui.theme.PeregerineTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val shortDateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

@Composable
fun LedgerEntryRow(
    modifier: Modifier = Modifier,
    type: StockChangeType,
    deltaQty: Double,
    note: String?,
    createdAt: Long,
    unit: String,
    productName: String? = null,
) {
    val isPositive = deltaQty > 0
    val deltaColor = if (isPositive) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error

    val (icon, label, defaultSubtitle) = when (type) {
        StockChangeType.OPENING -> Triple(Icons.AutoMirrored.Filled.TrendingUp, "Opening", "Initial stock")
        StockChangeType.PURCHASE_RECEIPT -> Triple(Icons.AutoMirrored.Filled.TrendingUp, "Purchase", "Restocked")
        StockChangeType.ADJUSTMENT -> Triple(Icons.Default.Build, "Adjustment", "Manual Adjustment")
        StockChangeType.SALE -> Triple(Icons.Default.ShoppingCart, "Billed to Customer", "Sale")
        StockChangeType.VOID -> Triple(Icons.Default.Close, "Void", "Voided Transaction")
        StockChangeType.RETURN -> Triple(Icons.Default.Replay, "Return", "Customer Return")
    }

    val hasLongNote = !note.isNullOrBlank()
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (hasLongNote) Modifier.clickable {expanded = !expanded } else Modifier)
            .padding(vertical = 10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = deltaColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp).padding()
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = deltaColor, modifier = Modifier.size(18.dp))
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = defaultSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                )
                if (productName != null) {
                    Text(text = productName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isPositive) "+" else ""}${formatQty(deltaQty)} $unit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = deltaColor
                )
                Text(
                    text = shortDateFormatter.format(Date(createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (hasLongNote) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Note:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private const val PREVIEW_CREATED_AT = 1_711_664_800_000L
private const val PREVIEW_LONG_NOTE =
    "Count variance after cycle check. 2 units were damaged in transit and moved out of sellable stock."

@Preview(name = "Ledger Row - Collapsed", showBackground = true)
@Composable
private fun LedgerEntryRowCollapsedPreview() {
    PeregerineTheme(dynamicColor = false) {
        LedgerEntryRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            type = StockChangeType.PURCHASE_RECEIPT,
            deltaQty = 24.0,
            note = PREVIEW_LONG_NOTE,
            createdAt = PREVIEW_CREATED_AT,
            unit = "pcs",
            productName = "Stainless Steel Water Bottle"
        )
    }
}

@Preview(name = "Ledger Row - Negative", showBackground = true)
@Composable
private fun LedgerEntryRowNegativePreview() {
    PeregerineTheme(dynamicColor = false) {
        LedgerEntryRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            type = StockChangeType.ADJUSTMENT,
            deltaQty = -2.0,
            note = PREVIEW_LONG_NOTE,
            createdAt = PREVIEW_CREATED_AT,
            unit = "pcs",
            productName = "Stainless Steel Water Bottle"
        )
    }
}

