package me.yasharya.peregerine.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.yasharya.peregerine.core.db.typeconverters.Converters
import me.yasharya.peregerine.feature_inventory.data.local.dao.BatchDao
import me.yasharya.peregerine.feature_inventory.data.local.dao.ProductDao
import me.yasharya.peregerine.feature_inventory.data.local.dao.StockLedgerDao
import me.yasharya.peregerine.feature_inventory.data.local.entity.BatchEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductInventorySummaryDto
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity

@Database(
    entities = [ProductEntity::class, StockLedgerEntity::class, BatchEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun stockLedgerDao(): StockLedgerDao
    abstract fun batchDao(): BatchDao
}