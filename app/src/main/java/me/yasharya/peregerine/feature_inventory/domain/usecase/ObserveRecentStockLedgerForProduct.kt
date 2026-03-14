package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveRecentStockLedgerForProduct(private val repository: InventoryRepository) {
    operator fun invoke(productId: String, limit: Int = 5) = repository.observeRecentStockLedgerForProduct(productId, limit)
}