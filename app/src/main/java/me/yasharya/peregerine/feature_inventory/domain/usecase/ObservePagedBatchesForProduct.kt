package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObservePagedBatchesForProduct(private val repository: InventoryRepository) {
    operator fun invoke(productId: String, showActive: Boolean = true, showInactive: Boolean = true) = repository.observePagedBatchesForProduct(productId, showActive, showInactive)
}