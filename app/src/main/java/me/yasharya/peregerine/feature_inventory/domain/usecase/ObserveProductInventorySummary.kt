package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveProductInventorySummary(private val repository: InventoryRepository) {
    operator fun invoke(activeOnly: Boolean = true) = repository.observePagedInventorySummary(activeOnly)
}
