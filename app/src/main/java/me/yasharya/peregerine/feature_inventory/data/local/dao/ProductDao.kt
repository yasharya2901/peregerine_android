package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductInventorySummaryDto

@Dao
interface ProductDao {
    @Query("""
        SELECT * FROM products
        WHERE (:active = 1 AND isActive = 1) 
        OR 
        (:inactives = 1 AND (isActive = 0 OR isActive IS NULL))
        ORDER BY createdAt DESC
    """)
    fun getPagedProducts(active: Boolean, inactives: Boolean): PagingSource<Int, ProductEntity>

    @Query("""
        SELECT * FROM products
        WHERE (isActive = :activeOnly OR :includeInactive = 1)
        AND (
            name LIKE '%' || :query || '%'
            OR barcode = :query
        )
        ORDER BY createdAt DESC
    """)
    fun searchPagedProducts(
        query: String,
        activeOnly: Boolean,
        includeInactive: Boolean
    ): PagingSource<Int, ProductEntity>

    @Query("""
        SELECT * FROM products
        WHERE id = :productId
        LIMIT 1
    """)
    fun getProductById(productId: String): Flow<ProductEntity?>

    @Query("""
        SELECT * FROM products
        WHERE id = :productId
        LIMIT 1
    """)
    suspend fun getProductByIdOnce(productId: String): ProductEntity? // For transaction

    @Upsert
    suspend fun upsert(product: ProductEntity)

    @Query("""
        UPDATE products
        SET isActive = :isActive, updatedAt = :updatedAt
        WHERE id = :productId
    """)
    suspend fun changeActiveStatusProduct(productId: String, isActive: Boolean, updatedAt: Long)

    @Query("""
        SELECT 
            p.*,
            q.totalQtyAvailable,
            CASE 
                WHEN q.totalQtyAvailable <= COALESCE(p.lowStockThreshold, 0)
                     AND q.totalQtyAvailable > 0
                THEN 1 ELSE 0
            END as isLowStock,
            CASE 
                WHEN q.totalQtyAvailable <= 0
                THEN 1 ELSE 0
            END as isOutOfStock
        FROM products p
        LEFT JOIN (
            SELECT 
                productId,
                COALESCE(SUM(
                    CASE WHEN isActive = 1 THEN qtyOnHand ELSE 0 END
                ), 0) as totalQtyAvailable
            FROM batch
            GROUP BY productId
        ) q ON p.id = q.productId
        WHERE p.isActive = :activeOnly
        ORDER BY p.name ASC
    """)
    fun getPagedProductInventorySummary(activeOnly: Boolean): PagingSource<Int, ProductInventorySummaryDto>

    @Query("""
        SELECT 
            p.*,
            q.totalQtyAvailable,
            CASE
                WHEN q.totalQtyAvailable <= COALESCE(p.lowStockThreshold, 0)
                AND q.totalQtyAvailable > 0
                THEN 1 ELSE 0
            END as isLowStock,
            CASE
                WHEN q.totalQtyAvailable <= 0
                THEN 1 ELSE 0
            END as isOutOfStock
        FROM products p
        LEFT JOIN (
            SELECT
                productId,
                COALESCE(SUM(
                    CASE WHEN isActive = 1 THEN qtyOnHand ELSE 0 END
                ),0) as totalQtyAvailable
                FROM batch
                GROUP BY productId
        ) as q ON p.id = q.productId
        WHERE p.isActive = 1
        AND q.totalQtyAvailable <= COALESCE(p.lowStockThreshold, 0)
        AND q.totalQtyAvailable > 0
        ORDER BY p.createdAt ASC
    """)
    fun getPagedLowStockInventorySummary(): PagingSource<Int, ProductInventorySummaryDto>

    @Query("""
        SELECT 
            p.*,
            q.totalQtyAvailable,
            CASE
                WHEN q.totalQtyAvailable <= COALESCE(p.lowStockThreshold, 0)
                AND q.totalQtyAvailable > 0
                THEN 1 ELSE 0
            END as isLowStock,
            CASE
                WHEN q.productId IS NOT NULL AND q.totalQtyAvailable <= 0
                THEN 1 ELSE 0
            END as isOutOfStock
            FROM products p
        LEFT JOIN (
            SELECT 
                productId,
                SUM(CASE WHEN isActive = 1 THEN qtyOnHand ELSE 0 END) as totalQtyAvailable
            FROM batch
            GROUP BY productId
        ) q ON p.id = q.productId
        WHERE p.isActive = 1
        AND q.productId IS NOT NULL
        AND q.totalQtyAvailable <= 0
        ORDER BY p.createdAt ASC
    """)
    fun getPagedOutOfStockInventorySummary(): PagingSource<Int, ProductInventorySummaryDto>

    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    fun observeTotalActiveProductCount(): Flow<Int>

    @Query("""
       SELECT COUNT(*) FROM products p
       LEFT JOIN (
            SELECT productId, SUM(CASE WHEN isActive = 1 THEN qtyOnHand ELSE 0 END) AS totalQtyAvailable
            FROM batch
            GROUP BY productId
       ) q ON p.id = q.productId
       WHERE p.isActive = 1
       AND COALESCE(q.totalQtyAvailable, 0) <= COALESCE(p.lowStockThreshold, 0)
       AND COALESCE(q.totalQtyAvailable, 0) > 0
    """)
    fun observeLowStockCount(): Flow<Int>

    @Query("""
       SELECT COUNT(*) FROM products p
       LEFT JOIN (
            SELECT productId, SUM(CASE WHEN isActive = 1 THEN qtyOnHand ELSE 0 END) AS totalQtyAvailable
            FROM batch
            GROUP BY productId
       ) q ON p.id = q.productId
       WHERE p.isActive = 1
       AND q.productId IS NOT NULL
       AND COALESCE(q.totalQtyAvailable, 0) <= 0
    """)
    fun observeOutOfStockCount(): Flow<Int>

    @Query("""
        SELECT 
            p.*,
            0.0 as totalQtyAvailable,
            0 as isLowStock,
            0 as isOutOfStock
        FROM products p
        LEFT JOIN batch b ON p.id = b.productId
        WHERE p.isActive = 1
        AND b.productId IS NULL
        ORDER BY p.createdAt ASC
    """)
    fun getPagedNotStockedInventorySummary(): PagingSource<Int, ProductInventorySummaryDto>
}