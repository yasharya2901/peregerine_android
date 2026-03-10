package me.yasharya.peregerine.di

import android.content.Context
import androidx.room.Room
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.feature_inventory.data.InventoryRepositoryImpl
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository
import me.yasharya.peregerine.feature_inventory.domain.usecase.*

class AppContainer(context: Context) {
    val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "shopapp.db"
    )
        .fallbackToDestructiveMigration(false)
        .build()

    val inventoryRepository: InventoryRepository = InventoryRepositoryImpl(db)

    val inventoryUseCases = InventoryUseCases(
        observeAllProducts = ObserveAllProducts(inventoryRepository),
        searchProducts = SearchProducts(inventoryRepository),
        observeProduct = ObserveProduct(inventoryRepository),
        createProduct = CreateProduct(inventoryRepository),
        activateProduct = ActivateProduct(inventoryRepository),
        deactivateProduct = DeactivateProduct(inventoryRepository),
        observeProductInventorySummary = ObserveProductInventorySummary(inventoryRepository),
        observeLowStockProducts = ObserveLowStockProducts(inventoryRepository),
        observeOutOfStockProducts = ObserveOutOfStockProducts(inventoryRepository),
        observeBatchesForProduct = ObserveBatchesForProduct(inventoryRepository),
        upsertBatch = UpsertBatch(inventoryRepository),
        activateBatch = ActivateBatch(inventoryRepository),
        deactivateBatch = DeactivateBatch(inventoryRepository),
        observeStockLedgerForProduct = ObserveStockLedgerForProduct(inventoryRepository),
        observeAllStockLedger = ObserveAllStockLedger(inventoryRepository),
        insertStockLedgerEntry = InsertStockLedgerEntry(inventoryRepository),
        insertMultipleStockLedgerEntries = InsertMultipleStockLedgerEntries(inventoryRepository),
        adjustStock = AdjustStock(inventoryRepository),
        observeTotalActiveProductCount = ObserveTotalActiveProductCount(inventoryRepository),
        observeLowStockCount = ObserveLowStockCount(inventoryRepository),
        observeOutOfStockCount = ObserveOutOfStockCount(inventoryRepository),
        observeNotStockedProducts = ObserveNotStockedProducts(inventoryRepository)
    )
}
