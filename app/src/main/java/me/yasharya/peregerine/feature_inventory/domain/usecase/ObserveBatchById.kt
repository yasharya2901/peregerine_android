package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveBatchById(private val repository: InventoryRepository) {
    operator fun invoke(batchId: String) = repository.observeBatchById(batchId)
}