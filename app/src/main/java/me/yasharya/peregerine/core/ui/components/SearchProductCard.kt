package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.core.util.fromPaise
import me.yasharya.peregerine.feature_inventory.domain.model.Product

@Composable
fun SearchProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                product.barcode?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            product.defaultMRP?.let { price ->
                Column(
                    horizontalAlignment = Alignment.End
                ){
                    Text(text = "MRP", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "₹${price.fromPaise()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchProductCardPreview() {
    MaterialTheme {
        SearchProductCard(
            product = Product(
                id = "1",
                name = "Parle-G Biscuits",
                barcode = "8901719110106",
                unit = "pcs",
                defaultSellingPrice = 1000L,
                defaultCostPrice = 800L,
                defaultMRP = 1000L,
                lowStockThreshold = 10.0,
                notes = null,
                isActive = true,
                createdAt = 0L,
                updatedAt = 0L
            )
        )
    }
}