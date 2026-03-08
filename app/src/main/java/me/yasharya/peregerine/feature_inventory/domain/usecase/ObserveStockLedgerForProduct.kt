package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveStockLedgerForProduct(private val repository: InventoryRepository) {
    operator fun invoke(productId: String) = repository.observeStockLedgerForProduct(productId)
}
