package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveNotStockedProducts(private val repo: InventoryRepository) {
    operator fun invoke() = repo.observePagedNotStockedInventorySummary()
}