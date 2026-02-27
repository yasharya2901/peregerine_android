package me.yasharya.peregerine.feature_inventory.domain.usecase


import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class UpdateProduct(private val repo: InventoryRepository) {
    suspend operator fun invoke(product: Product) = repo.updateProduct(product)
}