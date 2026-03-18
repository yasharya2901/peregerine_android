package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Product

data class BatchListUiState (
    val product: Product? = null,
    val filter: BatchFilter = BatchFilter.ALL
)

enum class BatchFilter {ALL, ACTIVE, INACTIVE}

