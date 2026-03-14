package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class SearchUnits(private val repository: InventoryRepository) {
    operator fun invoke(query: String) = repository.searchUnits(query)
}




