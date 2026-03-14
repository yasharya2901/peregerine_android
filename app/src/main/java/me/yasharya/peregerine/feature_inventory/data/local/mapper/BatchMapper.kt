package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.BatchEntity
import me.yasharya.peregerine.feature_inventory.domain.model.Batch

fun BatchEntity.toDomain(): Batch = Batch(
    id = id,
    productId = productId,
    purchaseDate = purchaseDate,
    mrp = mrp,
    costPrice = costPrice,
    sellingPrice = sellingPrice,
    purchaseQty = purchaseQty,
    qtyOnHand = qtyOnHand,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Batch.toEntity(): BatchEntity = BatchEntity(
    id = id,
    productId = productId,
    purchaseDate = purchaseDate,
    mrp = mrp,
    costPrice = costPrice,
    sellingPrice = sellingPrice,
    purchaseQty = purchaseQty,
    qtyOnHand = qtyOnHand,
    isActive = isActive,
    createdAt = createdAt,
    updatedAt = updatedAt
)
