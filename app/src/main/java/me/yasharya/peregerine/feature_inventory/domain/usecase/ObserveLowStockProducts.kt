package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveLowStockProducts(private val repo: InventoryRepository) {
    suspend operator fun invoke() = repo.observeLowStockProducts()
}