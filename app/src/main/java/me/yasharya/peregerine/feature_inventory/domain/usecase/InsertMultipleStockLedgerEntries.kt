package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class InsertMultipleStockLedgerEntries(private val repository: InventoryRepository) {
    suspend operator fun invoke(entries: List<StockLedgerEntry>) = repository.insertMultipleStockLedgerEntry(entries)
}
