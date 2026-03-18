package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.BatchEntity

@Dao
interface BatchDao {
    @Upsert
    suspend fun upsertBatch(batch: BatchEntity)

    @Query("UPDATE batch SET isActive = :isActive, updatedAt = :updatedAt WHERE id = :batchId")
    suspend fun changeActiveStatusBatch(batchId: String, isActive: Boolean, updatedAt: Long)

    @Query("SELECT * FROM batch WHERE productId = :productId")
    fun observeBatchesForProduct(productId: String): Flow<List<BatchEntity>>

    @Query("SELECT * FROM batch WHERE id = :batchId LIMIT 1")
    fun observeBatchById(batchId: String): Flow<BatchEntity?>

    @Query("SELECT * FROM batch WHERE id = :batchId LIMIT 1")
    suspend fun getBatchById(batchId: String): BatchEntity?

    @Query("""
        SELECT * FROM batch
        WHERE productId = :productId
        AND (
            (:showActive = 1 AND isActive = 1)
            OR
            (:showInactive = 1 AND isActive = 0)
        )
        ORDER BY isActive DESC, purchaseDate ASC
    """)
    fun getPagedBatchesForProduct(productId: String, showActive: Boolean, showInactive: Boolean): PagingSource<Int, BatchEntity>


}