package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveOutOfStockProducts(private val repo: InventoryRepository) {
    operator fun invoke() = repo.observePagedOutOfStockInventory()
}