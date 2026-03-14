package me.yasharya.peregerine.feature_inventory.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.domain.model.OpeningStock
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry

interface InventoryRepository {
    // Products
    fun observePagedProducts(query: String = "", active: Boolean = true, inactive: Boolean = false): Flow<PagingData<Product>>

    fun observeProductById(productId: String): Flow<Product?>
    suspend fun upsertProduct(product: Product)
    suspend fun deactivateProduct(productId: String)
    suspend fun activateProduct(productId: String)

    fun observePagedInventorySummary(activeOnly: Boolean = true): Flow<PagingData<ProductInventorySummary>>
    fun observePagedLowStockInventory(): Flow<PagingData<ProductInventorySummary>>
    fun observePagedOutOfStockInventory(): Flow<PagingData<ProductInventorySummary>>
    fun observePagedNotStockedInventorySummary(): Flow<PagingData<ProductInventorySummary>>
    suspend fun createProductWithOpeningStock(product: Product, openingStock: OpeningStock)
    fun observeTotalActiveProductCount(): Flow<Int>
    fun observeLowStockCount(): Flow<Int>
    fun observeOutOfStockCount(): Flow<Int>

    // Batch
    fun observeBatchesForProduct(productId: String): Flow<List<Batch>>
    fun observeBatchById(batchId: String): Flow<Batch?>
    suspend fun getBatchById(batchId: String): Batch?

    suspend fun upsertBatch(batch: Batch)
    suspend fun deactivateBatch(batchId: String)
    suspend fun activateBatch(batchId: String)

    suspend fun addBatchTransactional(batch: Batch)


    // Stock Ledger
    fun observeStockLedgerForProduct(
        productId: String
    ): Flow<PagingData<StockLedgerEntry>>

    fun observeAllStockLedger(): Flow<PagingData<StockLedgerEntry>>

    suspend fun insertStockLedgerEntry(entry: StockLedgerEntry)
    suspend fun insertMultipleStockLedgerEntry(entries: List<StockLedgerEntry>)

    suspend fun adjustStockTransactional(productId: String, batchId: String, deltaQty: Double, type: StockChangeType, referenceId: String?, note: String?)
    fun observeRecentStockLedgerForProduct(productId: String, limit: Int): Flow<List<StockLedgerEntry>>
    fun observeUnits(): Flow<List<MeasureUnit>>
    fun searchUnits(query: String): Flow<List<MeasureUnit>>
    suspend fun insertUnit(unit: MeasureUnit)

}