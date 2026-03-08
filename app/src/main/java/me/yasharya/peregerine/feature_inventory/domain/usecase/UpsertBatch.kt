package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class UpsertBatch(private val repository: InventoryRepository) {
    suspend operator fun invoke(batch: Batch) = repository.upsertBatch(batch)
}
