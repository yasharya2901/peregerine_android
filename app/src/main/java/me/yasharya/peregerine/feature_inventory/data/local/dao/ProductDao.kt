package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity


@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE (:activeOnly = 0 OR isActive = 1) ORDER BY name ASC")
    fun observeProducts(activeOnly: Int): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products
        WHERE (:activeOnly = 0 OR isActive = 1)
        AND (name LIKE '%' || :q || '%' OR barcode = :q)
        ORDER BY name ASC
    """)
    fun searchProducts(q: String, activeOnly: Int): Flow<List<ProductEntity>>

    @Query("""
        SELECT * FROM products
        WHERE isActive = 1
        AND lowStockThreshold IS NOT NULL
        AND stockQty <= lowStockThreshold
        ORDER BY stockQty ASC, name ASC
    """)
    fun observeLowStockProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE barcode = :barcode LIMIT 1")
    suspend fun getByBarcode(barcode: String): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("UPDATE products SET isActive = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun deactivate(id: String, updatedAt: Long)

    @Query("UPDATE products SET stockQty = stockQty + :deltaQty, updatedAt = :updatedAt WHERE id = :id")
    suspend fun addToStock(id: String, deltaQty: Double, updatedAt: Long)
}