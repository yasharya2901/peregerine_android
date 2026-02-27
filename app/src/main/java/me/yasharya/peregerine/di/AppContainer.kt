package me.yasharya.peregerine.di

import android.content.Context
import androidx.room.Room
import me.yasharya.peregerine.core.db.AppDatabase
import me.yasharya.peregerine.feature_inventory.data.InventoryRepositoryImpl
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository
import me.yasharya.peregerine.feature_inventory.domain.usecase.AdjustStock
import me.yasharya.peregerine.feature_inventory.domain.usecase.CreateProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.DeactivateProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveLowStockProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveProductLedger
import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.SearchProducts
import me.yasharya.peregerine.feature_inventory.domain.usecase.UpdateProduct

class AppContainer(context: Context) {
    val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "shopadd.db"
    )
        .fallbackToDestructiveMigration(false)
        .build()

    val inventoryRepository: InventoryRepository = InventoryRepositoryImpl(db)

    val createProduct = CreateProduct(inventoryRepository)
    val updateProduct = UpdateProduct(inventoryRepository)
    val deactivateProduct = DeactivateProduct(inventoryRepository)
    val adjustStock = AdjustStock(inventoryRepository)
    val observeProducts = ObserveProducts(inventoryRepository)
    val searchProducts = SearchProducts(inventoryRepository)
    val observeLowStock = ObserveLowStockProducts(inventoryRepository)
    val observeProductLedger = ObserveProductLedger(inventoryRepository)
}