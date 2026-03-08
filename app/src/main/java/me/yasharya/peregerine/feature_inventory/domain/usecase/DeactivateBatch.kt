package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class DeactivateBatch(private val repository: InventoryRepository) {
    suspend operator fun invoke(batchId: String) = repository.deactivateBatch(batchId)
}
