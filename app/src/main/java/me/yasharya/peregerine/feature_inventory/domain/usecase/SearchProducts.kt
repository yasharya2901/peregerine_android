package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class SearchProducts(private val repo: InventoryRepository) {
    suspend operator fun invoke(query: String, activeOnly: Boolean = true) = repo.searchProducts(query, activeOnly)
}