package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Product

data class InventoryUiState(
    val query: String = "",
    val products: List<Product> = emptyList(),
    val showActiveOnly: Boolean = true
)