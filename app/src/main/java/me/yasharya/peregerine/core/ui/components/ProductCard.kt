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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    item: ProductInventorySummary,
    onAddToPO: ((ProductInventorySummary) -> Unit)? = null
){
    val stockColor = when {
        item.isOutOfStock -> MaterialTheme.colorScheme.error
        item.isLowStock -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    val stockLabel = when {
        item.isOutOfStock -> "Out of Stock"
        item.isLowStock -> "${item.totalQtyAvailable} ${item.product.unit}"
        else -> "${item.totalQtyAvailable} ${item.product.unit}"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
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
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    item.product.barcode?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item.product.defaultSellingPrice?.let {price ->
                    Text(
                        text = "₹${price / 100}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }


            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = stockColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stockLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = stockColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                val showButton = (item.isLowStock || item.isOutOfStock) && onAddToPO != null

                Surface(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = stockColor.copy(alpha = if (showButton) 0.15f else 0f),
                    border = if (showButton) BorderStroke(1.dp, stockColor.copy(alpha = 0.5f)) else null,
                    onClick = { if (showButton) onAddToPO.invoke(item) },
                    enabled = showButton
                ) {
                    Text(
                        text = "+ Add to PO",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (showButton) stockColor else stockColor.copy(alpha = 0f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

private fun createSampleProduct(
    name: String,
    barcode: String? = null,
    price: Long? = 29900L,
    unit: String = "pcs",
    lowStockThreshold: Double? = 10.0
) = Product(
    id = "preview-id",
    name = name,
    barcode = barcode,
    unit = unit,
    defaultSellingPrice = price,
    defaultCostPrice = price?.let { (it * 0.7).toLong() },
    defaultMRP = price?.let { (it * 1.1).toLong() },
    lowStockThreshold = lowStockThreshold,
    isActive = true,
    notes = null,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

@Preview(name = "Normal Stock", showBackground = true)
@Composable
fun ProductCardPreview_NormalStock() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("Samsung Galaxy S24", "8801234567890"),
                totalQtyAvailable = 50.0,
                isLowStock = false,
                isOutOfStock = false
            )
        )
    }
}

@Preview(name = "Low Stock - With PO Button", showBackground = true)
@Composable
fun ProductCardPreview_LowStock() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("Samsung Galaxy S24", "8801234567890", 99900L),
                totalQtyAvailable = 5.0,
                isLowStock = true,
                isOutOfStock = false
            ),
            onAddToPO = {}
        )
    }
}

@Preview(name = "Out of Stock - With PO Button", showBackground = true)
@Composable
fun ProductCardPreview_OutOfStock() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("OnePlus 12", "8802345678902", 54999L),
                totalQtyAvailable = 0.0,
                isLowStock = false,
                isOutOfStock = true
            ),
            onAddToPO = {}
        )
    }
}

@Preview(name = "Without Barcode", showBackground = true)
@Composable
fun ProductCardPreview_NoBarcode() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("Generic Phone Case", barcode = null, price = 1499L),
                totalQtyAvailable = 100.0,
                isLowStock = false,
                isOutOfStock = false
            )
        )
    }
}

@Preview(name = "Low Stock - Without PO Button", showBackground = true)
@Composable
fun ProductCardPreview_LowStockNoPO() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("Screen Protector", unit = "pack", price = 999L),
                totalQtyAvailable = 3.0,
                isLowStock = true,
                isOutOfStock = false
            ),
            onAddToPO = null
        )
    }
}

@Preview(name = "Kilogram Unit", showBackground = true)
@Composable
fun ProductCardPreview_KgUnit() {
    MaterialTheme {
        ProductCard(
            item = ProductInventorySummary(
                product = createSampleProduct("Rice Premium", unit = "kg", price = 8000L, lowStockThreshold = 50.0),
                totalQtyAvailable = 250.5,
                isLowStock = false,
                isOutOfStock = false
            )
        )
    }
}


