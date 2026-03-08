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
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toDomain
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toEntity
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventoryDetail
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

    override fun observePagedProducts(query: String, active: Boolean, inactive: Boolean): Flow<PagingData<Product>> {
        return Pager(config = PagingConfig(
            pageSize = Constants.DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
        ),
            pagingSourceFactory = {
                if (query.isEmpty()) {
                    productDao.getPagedProducts(active, inactive)
                } else {
                    productDao.searchPagedProducts(query, active)
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




}