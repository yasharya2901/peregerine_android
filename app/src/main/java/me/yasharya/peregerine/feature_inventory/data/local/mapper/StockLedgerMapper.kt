package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerEntity
import me.yasharya.peregerine.feature_inventory.data.local.entity.StockLedgerWithProductDto
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntryWithProduct


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

fun StockLedgerWithProductDto.toDomain(): StockLedgerEntryWithProduct = StockLedgerEntryWithProduct(
    id = id,
    productId = productId,
    productName = productName,
    productUnit = productUnit,
    batchId = batchId,
    type = StockChangeType.valueOf(type),
    deltaQty = deltaQty,
    referenceId = referenceId,
    note = note,
    createdAt = createdAt,
)