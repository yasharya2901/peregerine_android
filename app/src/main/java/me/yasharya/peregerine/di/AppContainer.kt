package me.yasharya.peregerine.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.feature_inventory.data.InventoryRepositoryImpl
import me.yasharya.peregerine.feature_inventory.data.local.entity.UnitEntity
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository
import me.yasharya.peregerine.feature_inventory.domain.usecase.ActivateBatch
import me.yasharya.peregerine.feature_inventory.domain.usecase.ActivateProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.AddBatch
import me.yasharya.peregerine.feature_inventory.domain.usecase.AdjustStock
import me.yasharya.peregerine.feature_inventory.domain.usecase.CreateProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.CreateProductWithOpeningStock
import me.yasharya.peregerine.feature_inventory.domain.usecase.DeactivateBatch
import me.yasharya.peregerine.feature_inventory.domain.usecase.DeactivateProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.InsertMultipleStockLedgerEntries
import me.yasharya.peregerine.feature_inventory.domain.usecase.InsertStockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.usecase.InsertUnit
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveAllProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveAllStockLedger
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveBatchById
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveBatchesForProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveLowStockCount
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveLowStockProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveNotStockedProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveOutOfStockCount
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveOutOfStockProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObservePagedBatchesForProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveProductInventorySummary
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveRecentStockLedgerForProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveStockLedgerForProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveTotalActiveProductCount
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveUnits
import me.yasharya.peregerine.feature_inventory.domain.usecase.SearchProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.SearchUnits
import me.yasharya.peregerine.feature_inventory.domain.usecase.UpsertBatch

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
        observeNotStockedProducts = ObserveNotStockedProducts(inventoryRepository),
        createProductWithOpeningStock = CreateProductWithOpeningStock(inventoryRepository),
        observeUnits = ObserveUnits(inventoryRepository),
        searchUnits = SearchUnits(inventoryRepository),
        insertUnit = InsertUnit(inventoryRepository),
        addBatch = AddBatch(inventoryRepository),
        observeRecentStockLedgerForProduct = ObserveRecentStockLedgerForProduct(inventoryRepository),
        observeBatchById = ObserveBatchById(inventoryRepository),
        observePagedBatchesForProduct = ObservePagedBatchesForProduct(inventoryRepository)
    )

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    init {
        applicationScope.launch {
            seedPresetsIfNeeded()
        }
    }

    private suspend fun seedPresetsIfNeeded() {
        if (db.unitDao().presetCount() > 0) return

        val presets = listOf("pcs", "kg", "g", "mg", "litre", "ml", "box", "unit")

        db.unitDao().insertAll(
            presets.map { name ->
                UnitEntity(id = Ids.newId(), name = name, isPreset = true)
            }
        )
    }
}
