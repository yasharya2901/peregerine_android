package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductInventorySummaryDto
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    barcode = barcode,
    defaultMRP = defaultMRP,
    defaultSellingPrice = defaultSellingPrice,
    defaultCostPrice = defaultCostPrice,
    unit = unit,
    lowStockThreshold = lowStockThreshold,
    notes = notes,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    barcode = barcode,
    defaultMRP = defaultMRP,
    defaultSellingPrice = defaultSellingPrice,
    defaultCostPrice = defaultCostPrice,
    unit = unit,
    lowStockThreshold = lowStockThreshold,
    notes = notes,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun ProductInventorySummaryDto.toDomain(): ProductInventorySummary = ProductInventorySummary(
    product = product.toDomain(),
    totalQtyAvailable = totalQtyAvailable,
    isLowStock = isLowStock,
    isOutOfStock = isOutOfStock
)

