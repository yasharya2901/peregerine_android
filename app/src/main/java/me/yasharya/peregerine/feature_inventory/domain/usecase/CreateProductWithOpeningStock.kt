package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.OpeningStock
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class CreateProductWithOpeningStock(private val repository: InventoryRepository) {
    suspend operator fun invoke(product: Product, openingStock: OpeningStock) = repository.createProductWithOpeningStock(product, openingStock)
}




