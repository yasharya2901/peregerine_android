package me.yasharya.peregerine.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import me.yasharya.peregerine.feature_inventory.data.local.dao.ProductDao
import me.yasharya.peregerine.feature_inventory.data.local.dao.StockLedgerDao
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity

@Database(
    entities = [ProductEntity::class, StockLedgerEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun stockLedgerDao(): StockLedgerDao
}