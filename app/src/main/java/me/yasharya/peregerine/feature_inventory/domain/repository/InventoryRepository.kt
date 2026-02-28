package me.yasharya.peregerine.feature_inventory.domain.repository

import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry

interface InventoryRepository {
    fun observeProducts(activeOnly: Boolean = true): Flow<List<Product>>
    fun searchProducts(query: String, activeOnly: Boolean = true): Flow<List<Product>>
    fun observeLowStockProducts(): Flow<List<Product>>

    suspend fun getProductById(productId: String): Product?
    suspend fun getProductByBarcode(barcode: String): Product?
    suspend fun createProduct(product: Product)
    suspend fun updateProduct(product: Product)
    suspend fun deactivateProduct(productId: String)

    suspend fun adjustStock(productId: String, deltaQty: Double, type: StockChangeType, note: String?, referenceId: String? = null)

    fun observeProductLedger(productId: String): Flow<List<StockLedgerEntry>>
}