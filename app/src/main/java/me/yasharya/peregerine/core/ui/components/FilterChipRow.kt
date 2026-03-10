package me.yasharya.peregerine.core.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.R
import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryFilter

@Composable
fun FilterChipRow(
    selectedFilter: InventoryFilter,
    onFilterSelected: (InventoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = InventoryFilter.entries

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        filters.forEach { filter ->
            val labelRes = when (filter) {
                InventoryFilter.ALL -> R.string.filter_all
                InventoryFilter.LOW_STOCK -> R.string.filter_low_stock
                InventoryFilter.OUT_OF_STOCK -> R.string.filter_out_of_stock
                InventoryFilter.INACTIVE -> R.string.filter_inactive
                InventoryFilter.NOT_STOCKED -> R.string.filter_not_stocked
            }

            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(stringResource(labelRes)) },
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

@Preview(name = "Not Stocked Selected", showBackground = true)
@Composable
fun FilterChipRowPreview_NotStocked() {
    FilterChipRow(
        selectedFilter = InventoryFilter.NOT_STOCKED,
        onFilterSelected = { }
    )
}

