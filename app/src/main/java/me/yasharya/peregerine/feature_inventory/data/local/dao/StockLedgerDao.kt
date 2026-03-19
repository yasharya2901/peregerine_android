package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerWithProductDto

@Dao
interface StockLedgerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: StockLedgerEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(entries: List<StockLedgerEntity>)

    @Query("""
        SELECT * FROM stock_ledger
        ORDER BY createdAt DESC
    """)
    fun pagingSource(): PagingSource<Int, StockLedgerEntity>

    @Query("""
        SELECT * FROM stock_ledger
        WHERE productId = :productId
        ORDER BY createdAt DESC
    """)
    fun pagingSourceByProduct(productId: String): PagingSource<Int, StockLedgerEntity>


    @Query("""
        SELECT * FROM stock_ledger
        WHERE productId = :productId
        ORDER BY createdAt DESC
        LIMIT :limit
    """)
    fun getRecentByProduct(productId: String, limit: Int): Flow<List<StockLedgerEntity>>

    @Query("""
        SELECT sl.*, p.name AS productName, p.unit AS productUnit
        FROM stock_ledger sl
        INNER JOIN products p ON sl.productId = p.id
        WHERE (:productId IS NULL OR sl.productId = :productId)
        AND (:type IS NULL OR sl.type = :type)
        ORDER BY sl.createdAt DESC
    """)
    fun pagingSourceFiltered(productId: String?, type: String?): PagingSource<Int, StockLedgerWithProductDto>
}