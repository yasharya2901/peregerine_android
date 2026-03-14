package me.yasharya.peregerine.feature_inventory.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.core.util.Constants
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.data.local.entity.BatchEntity
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toDomain
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toEntity
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.domain.model.OpeningStock
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class InventoryRepositoryImpl (
    private val db: AppDatabase
) : InventoryRepository {

    private val productDao = db.productDao()
    private val ledgerDao = db.stockLedgerDao()
    private val batchDao = db.batchDao()
    private val unitDao = db.unitDao()

    override fun observePagedProducts(query: String, active: Boolean, inactive: Boolean): Flow<PagingData<Product>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ),
            pagingSourceFactory = {
                if (query.isEmpty()) {
                    productDao.getPagedProducts(active, inactive)
                } else {
                    productDao.searchPagedProducts(query, active, inactive)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map {it.toDomain()}
        }
    }

    override fun observeProductById(productId: String): Flow<Product?> {
        return productDao.getProductById(productId).map { it?.toDomain() }
    }

    override suspend fun upsertProduct(product: Product) {
        return productDao.upsert(product.toEntity())
    }

    override suspend fun deactivateProduct(productId: String) {
        productDao.changeActiveStatusProduct(productId,false, Time.nowEpochMillis())
    }

    override suspend fun activateProduct(productId: String) {
        productDao.changeActiveStatusProduct(productId,true, Time.nowEpochMillis())
    }

    override fun observePagedInventorySummary(activeOnly: Boolean): Flow<PagingData<ProductInventorySummary>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ),
            pagingSourceFactory = {productDao.getPagedProductInventorySummary(activeOnly)}
        ).flow.map{pagingData ->
            pagingData.map {it.toDomain()}
        }
    }

    override fun observePagedLowStockInventory(): Flow<PagingData<ProductInventorySummary>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ), pagingSourceFactory = {productDao.getPagedLowStockInventorySummary()}
        ).flow.map {pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun observePagedOutOfStockInventory(): Flow<PagingData<ProductInventorySummary>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ), pagingSourceFactory = {productDao.getPagedOutOfStockInventorySummary()}
        ).flow.map {pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override fun observePagedNotStockedInventorySummary(): Flow<PagingData<ProductInventorySummary>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ), pagingSourceFactory = {productDao.getPagedNotStockedInventorySummary()}
        ).flow.map {pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun createProductWithOpeningStock(
        product: Product,
        openingStock: OpeningStock
    ) {
        db.withTransaction {
            productDao.upsert(product.toEntity())

            // create new batch
            val batchId = Ids.newId()
            val now = Time.nowEpochMillis()

            batchDao.upsertBatch(
                BatchEntity(
                    id = batchId,
                    productId = product.id,
                    purchaseDate = now,
                    mrp = openingStock.mrp,
                    costPrice = openingStock.costPrice,
                    sellingPrice = openingStock.sellingPrice,
                    purchaseQty = openingStock.qty,
                    qtyOnHand = openingStock.qty,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now,
                )
            )

            // insert ledger entry
            ledgerDao.insert(
                StockLedgerEntry(
                    id = Ids.newId(),
                    productId = product.id,
                    batchId = batchId,
                    type = StockChangeType.OPENING,
                    deltaQty = openingStock.qty,
                    referenceId = null,
                    note = "Opening Stock",
                    createdAt = now,
                ).toEntity()
            )

        }
    }

    override fun observeTotalActiveProductCount(): Flow<Int> {
        return productDao.observeTotalActiveProductCount()
    }

    override fun observeLowStockCount(): Flow<Int> {
        return productDao.observeLowStockCount()
    }

    override fun observeOutOfStockCount(): Flow<Int> {
        return productDao.observeOutOfStockCount()
    }

    override fun observeBatchesForProduct(productId: String): Flow<List<Batch>> {
        return batchDao.observeBatchesForProduct(productId).map { batchEntities ->
            batchEntities.map { it.toDomain() }
        }
    }

    override fun observeBatchById(batchId: String): Flow<Batch?> {
        return batchDao.observeBatchById(batchId).map { it?.toDomain() }
    }

    override suspend fun getBatchById(batchId: String): Batch? {
        return batchDao.getBatchById(batchId)?.toDomain()
    }

    override suspend fun upsertBatch(batch: Batch) {
        return batchDao.upsertBatch(batch.toEntity())
    }


    override suspend fun deactivateBatch(batchId: String) {
        return batchDao.changeActiveStatusBatch(batchId, false, Time.nowEpochMillis())
    }

    override suspend fun activateBatch(batchId: String) {
        return batchDao.changeActiveStatusBatch(batchId, true, Time.nowEpochMillis())
    }

    override suspend fun addBatchTransactional(batch: Batch) {
        db.withTransaction {
            val product = productDao.getProductByIdOnce(batch.productId)
                ?: throw IllegalStateException("Product Not Found")

            if (!product.isActive) {
                throw IllegalStateException("Cannot add stock to an inactive product. Activate the product first.")
            }

            batchDao.upsertBatch(batch.toEntity())

            ledgerDao.insert(
                StockLedgerEntry(
                    id = Ids.newId(),
                    productId = batch.productId,
                    batchId = batch.id,
                    type = StockChangeType.PURCHASE_RECEIPT,
                    deltaQty = batch.purchaseQty,
                    referenceId = null,
                    note = null,
                    createdAt = Time.nowEpochMillis()
                ).toEntity()
            )
        }
    }

    override fun observeStockLedgerForProduct(productId: String): Flow<PagingData<StockLedgerEntry>> {
        return Pager(config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false), pagingSourceFactory = {ledgerDao.pagingSourceByProduct(productId)}).flow.map { stockledgers ->
            stockledgers.map {it.toDomain()}
        }
    }

    override fun observeAllStockLedger(): Flow<PagingData<StockLedgerEntry>> {
        return Pager(config = PagingConfig(pageSize = Constants.DEFAULT_PAGE_SIZE, enablePlaceholders = false), pagingSourceFactory = {ledgerDao.pagingSource()}).flow.map { stockLedgers ->
            stockLedgers.map {it.toDomain()}
        }
    }

    override suspend fun insertStockLedgerEntry(entry: StockLedgerEntry) {
        return ledgerDao.insert(entry.toEntity())
    }

    override suspend fun insertMultipleStockLedgerEntry(entries: List<StockLedgerEntry>) {
        return ledgerDao.insertAll(entries.map{it.toEntity()})
    }

    override suspend fun adjustStockTransactional(
        productId: String,
        batchId: String,
        deltaQty: Double,
        type: StockChangeType,
        referenceId: String?,
        note: String?
    ) {
        db.withTransaction {
            val batch = batchDao.getBatchById(batchId)
                ?: throw IllegalStateException("Batch $batchId not found")

            if (deltaQty > 0) {
                val product = productDao.getProductByIdOnce(productId)
                    ?: throw IllegalStateException("Product $productId not found")
                if (!product.isActive) {
                    throw IllegalStateException("Cannot add stock to an inactive product. Activate the product first.")
                }
            }

            val newQty = batch.qtyOnHand + deltaQty
            if (newQty < 0) throw IllegalStateException("Stock cannot go negative")

            batchDao.upsertBatch(
                batch.copy(
                    qtyOnHand = newQty,
                    updatedAt = Time.nowEpochMillis()
                )
            )

            ledgerDao.insert(
                StockLedgerEntry(
                    id = Ids.newId(),
                    productId = productId,
                    batchId = batchId,
                    type = type,
                    deltaQty = deltaQty,
                    referenceId = referenceId,
                    note = note,
                    createdAt = Time.nowEpochMillis()
                ).toEntity()
            )
        }
    }

    override fun observeRecentStockLedgerForProduct(
        productId: String,
        limit: Int
    ): Flow<List<StockLedgerEntry>> {
        return ledgerDao.getRecentByProduct(productId, limit).map {list ->
            list.map { it.toDomain() }
        }
    }

    override fun observeUnits(): Flow<List<MeasureUnit>> = unitDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun searchUnits(query: String): Flow<List<MeasureUnit>> = unitDao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun insertUnit(unit: MeasureUnit) = unitDao.insert(unit.toEntity())
}