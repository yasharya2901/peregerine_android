package me.yasharya.peregerine.feature_inventory.domain.model

enum class StockChangeType {
    OPENING,
    PURCHASE_RECEIPT,
    ADJUSTMENT,
    SALE,
    VOID,
    RETURN
}