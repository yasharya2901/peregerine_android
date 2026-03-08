package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveBatchesForProduct(private val repository: InventoryRepository) {
    operator fun invoke(productId: String) = repository.observeBatchesForProduct(productId)
}
