package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryFilter

@Composable
fun FilterChipRow(
    selectedFilter: InventoryFilter,
    onFilterSelected: (InventoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        InventoryFilter.ALL to "All",
        InventoryFilter.LOW_STOCK to "Low Stock",
        InventoryFilter.OUT_OF_STOCK to "Out of Stock",
        InventoryFilter.INACTIVE to "Inactive"
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        filters.forEach { (filter, label) ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = {onFilterSelected(filter)},
                label = { Text(label) },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview(name = "All Selected", showBackground = true)
@Composable
fun FilterChipRowPreview_All() {
    FilterChipRow(
        selectedFilter = InventoryFilter.ALL,
        onFilterSelected = { }
    )
}

@Preview(name = "Low Stock Selected", showBackground = true)
@Composable
fun FilterChipRowPreview_LowStock() {
    FilterChipRow(
        selectedFilter = InventoryFilter.LOW_STOCK,
        onFilterSelected = { }
    )
}

@Preview(name = "Out of Stock Selected", showBackground = true)
@Composable
fun FilterChipRowPreview_OutOfStock() {
    FilterChipRow(
        selectedFilter = InventoryFilter.OUT_OF_STOCK,
        onFilterSelected = { }
    )
}

@Preview(name = "Inactive Selected", showBackground = true)
@Composable
fun FilterChipRowPreview_Inactive() {
    FilterChipRow(
        selectedFilter = InventoryFilter.INACTIVE,
        onFilterSelected = { }
    )
}

