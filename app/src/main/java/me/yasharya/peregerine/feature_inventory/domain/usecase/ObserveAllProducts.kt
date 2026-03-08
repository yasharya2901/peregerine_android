package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveAllProducts(private val repo: InventoryRepository) {
    operator fun invoke(active: Boolean = true, inactive: Boolean = false) = repo.observePagedProducts("", active, inactive)
}