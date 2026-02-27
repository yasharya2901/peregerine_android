package me.yasharya.peregerine.feature_inventory.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toDomain
import me.yasharya.peregerine.feature_inventory.data.local.mapper.toEntity
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class InventoryRepositoryImpl (
    private val db: AppDatabase
) : InventoryRepository {

    private val productDao = db.productDao()
    private val ledgerDao = db.stockLedgerDao()

    override fun observeProducts(activeOnly: Boolean): Flow<List<Product>> = productDao.observeProducts(if (activeOnly) 1 else 0).map{ list -> list.map {it.toDomain()} }

    override fun searchProducts(
        query: String,
        activeOnly: Boolean
    ): Flow<List<Product>> = productDao.searchProducts(query, if (activeOnly) 1 else 0).map{ list -> list.map {it.toDomain()} }

    override fun observeLowStockProducts(): Flow<List<Product>> = productDao.observeLowStockProducts().map { list -> list.map {it.toDomain()} }

    override suspend fun getProductById(productId: String): Product? = productDao.getById(productId)?.toDomain()

    override suspend fun getProductByBarcode(barcode: String): Product? = productDao.getByBarcode(barcode)?.toDomain()

    override suspend fun createProduct(product: Product) {
        productDao.insert(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        productDao.update(product.toEntity())
    }

    override suspend fun deactivateProduct(productId: String) {
        productDao.deactivate(productId, Time.nowEpochMillis())
    }

    override suspend fun adjustStock(
        productId: String,
        deltaQty: Double,
        type: String,
        note: String?,
        referenceId: String?
    ) {
        val existing = productDao.getById(productId) ?: return
        val newStockQty = existing.stockQty + deltaQty

        require(newStockQty >= 0.0) { "Stock Quantity cannot go negative" }

        val now = Time.nowEpochMillis()

        db.withTransaction {
            // update cached stock
            productDao.addToStock(productId, deltaQty, now)

            // immutable ledger entry
            ledgerDao.insert(
                StockLedgerEntity(
                    id = Ids.newId(),
                    productId = productId,
                    type = type,
                    deltaQty = deltaQty,
                    referenceId = referenceId,
                    note = note,
                    createdAt = now
                )
            )
        }
    }

    override fun observeProductLedger(productId: String): Flow<List<StockLedgerEntry>> =
        ledgerDao.observeByProduct(productId).map{it.map{e -> e.toDomain()}}

}