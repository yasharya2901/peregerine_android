package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class CreateProduct(private val repo: InventoryRepository) {
    suspend operator fun invoke(product: Product) = repo.upsertProduct(product)
}