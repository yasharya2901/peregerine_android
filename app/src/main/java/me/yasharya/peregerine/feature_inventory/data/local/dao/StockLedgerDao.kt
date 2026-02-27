package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity

@Dao
interface StockLedgerDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: StockLedgerEntity)

    @Query("SELECT * FROM stock_ledger WHERE productId = :productId ORDER BY createdAt DESC")
    fun observeByProduct(productId: String): Flow<List<StockLedgerEntity>>
}