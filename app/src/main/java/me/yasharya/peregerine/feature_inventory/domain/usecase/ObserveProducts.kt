package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveProducts(private val repo: InventoryRepository) {
    suspend operator fun invoke(activeOnly: Boolean = true) = repo.observeProducts(activeOnly)
}