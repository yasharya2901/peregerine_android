package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.core.util.formatQty
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val batchCardShape = RoundedCornerShape(16.dp)
private val batchShortDateFormatter = SimpleDateFormat("d MMM", Locale.getDefault())

@Composable
fun BatchCard(
    batch: Batch,
    unit: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val progress = if (batch.purchaseQty > 0) (batch.qtyOnHand / batch.purchaseQty).toFloat().coerceIn(0f, 1f) else 0f
    val stockColor = when {
        !batch.isActive || batch.qtyOnHand <= 0 -> MaterialTheme.colorScheme.error
        progress < 0.3f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if(onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = batchCardShape,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = batchShortDateFormatter.format(Date(batch.purchaseDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (!batch.isActive) {
                        Text(
                            text = "Inactive",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${formatQty(batch.qtyOnHand)} left",
                        style = MaterialTheme.typography.labelMedium,
                        color = stockColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (onClick != null) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BatchPriceItem("MRP", batch.mrp)
                BatchPriceItem("Cost", batch.costPrice)
                BatchPriceItem("Sell", batch.sellingPrice)
            }

            Spacer(Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = stockColor,
                trackColor = stockColor.copy(alpha = 0.15f)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${formatQty(batch.qtyOnHand)}/${formatQty(batch.purchaseQty)} $unit",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}


@Composable
private fun BatchPriceItem(label: String, paise: Long) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "₹${paise / 100}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}