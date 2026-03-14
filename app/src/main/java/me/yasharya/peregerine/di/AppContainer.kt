package me.yasharya.peregerine.di

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.feature_inventory.data.InventoryRepositoryImpl
import me.yasharya.peregerine.feature_inventory.data.local.entity.UnitEntity
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
        observeNotStockedProducts = ObserveNotStockedProducts(inventoryRepository),
        createProductWithOpeningStock = CreateProductWithOpeningStock(inventoryRepository),
        observeUnits = ObserveUnits(inventoryRepository),
        searchUnits = SearchUnits(inventoryRepository),
        insertUnit = InsertUnit(inventoryRepository),
        addBatch = AddBatch(inventoryRepository),
        observeRecentStockLedgerForProduct = ObserveRecentStockLedgerForProduct(inventoryRepository)
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedPresetsIfNeeded()
        }
    }

    private suspend fun seedPresetsIfNeeded() {
        if (db.unitDao().presetCount() > 0) return

        val presets = listOf(
            "pcs", "kg", "g", "mg", "litre", "ml",
            "box", "pack", "bag", "dozen", "pair",
            "bottle", "can", "roll", "sheet", "strip",
            "tablet", "capsule", "sachet", "pouch",
            "carton", "tray", "bundle", "set", "unit"
        )

        db.unitDao().insertAll(
            presets.map { name ->
                UnitEntity(id = Ids.newId(), name = name, isPreset = true)
            }
        )
    }
}
