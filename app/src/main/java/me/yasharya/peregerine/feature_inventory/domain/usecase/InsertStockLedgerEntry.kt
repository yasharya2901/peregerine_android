package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class InsertStockLedgerEntry(private val repository: InventoryRepository) {
    suspend operator fun invoke(entry: StockLedgerEntry) = repository.insertStockLedgerEntry(entry)
}
