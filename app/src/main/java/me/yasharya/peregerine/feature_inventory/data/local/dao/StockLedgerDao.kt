package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity

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
}