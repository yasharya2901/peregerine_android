package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveProductLedger(private val repo: InventoryRepository) {
    suspend operator fun invoke(productId: String) = repo.observeProductLedger(productId)
}