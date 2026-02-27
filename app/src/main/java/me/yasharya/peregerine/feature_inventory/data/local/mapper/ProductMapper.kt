package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.ProductEntity
import me.yasharya.peregerine.feature_inventory.domain.model.Product

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    barcode = barcode,
    sellingPrice = sellingPrice,
    costPrice = costPrice,
    unit = unit,
    stockQty = stockQty,
    lowStockThreshold = lowStockThreshold,
    notes = notes,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Product.toEntity(): ProductEntity = ProductEntity(
    id = id,
    name = name,
    barcode = barcode,
    sellingPrice = sellingPrice,
    costPrice = costPrice,
    unit = unit,
    stockQty = stockQty,
    lowStockThreshold = lowStockThreshold,
    notes = notes,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)