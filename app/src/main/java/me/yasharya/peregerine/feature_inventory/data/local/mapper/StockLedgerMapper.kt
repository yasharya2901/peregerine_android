package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry


fun StockLedgerEntity.toDomain(): StockLedgerEntry = StockLedgerEntry(
    id = id,
    productId = productId,
    batchId = batchId,
    type = type,
    deltaQty = deltaQty,
    referenceId = referenceId,
    note = note,
    createdAt = createdAt,
)

fun StockLedgerEntry.toEntity(): StockLedgerEntity = StockLedgerEntity(
    id = id,
    productId = productId,
    batchId = batchId,
    type = type,
    deltaQty = deltaQty,
    referenceId = referenceId,
    note = note,
    createdAt = createdAt,
)
