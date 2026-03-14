package me.yasharya.peregerine.feature_inventory.domain.usecase

import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository

class InsertUnit(private val repository: InventoryRepository) {
    suspend operator fun invoke(unit: MeasureUnit) = repository.insertUnit(unit)
}