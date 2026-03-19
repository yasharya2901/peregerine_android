package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class ObserveAllStockLedger(private val repository: InventoryRepository) {
    operator fun invoke(productId: String? = null, type: StockChangeType? = null) = repository.observeAllStockLedger(productId, type)
}
