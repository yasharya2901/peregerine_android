package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Product


enum class InventoryFilter {
    ALL, LOW_STOCK, OUT_OF_STOCK, INACTIVE
}
data class InventoryUiState(
    val filter: InventoryFilter = InventoryFilter.ALL,
    val totalCount: Int = 0,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val searchQuery: String = ""
)