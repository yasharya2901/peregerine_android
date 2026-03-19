package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType

data class StockLedgerUiState (
    val productQuery: String = "",
    val isSearchBarVisible: Boolean = true,
    val selectedProduct: Product? = null,
    val selectedType: StockChangeType? = null,
)