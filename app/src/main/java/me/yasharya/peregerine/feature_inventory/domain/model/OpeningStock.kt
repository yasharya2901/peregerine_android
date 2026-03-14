package me.yasharya.peregerine.feature_inventory.domain.model

data class OpeningStock(
    val qty: Double,
    val mrp: Long,
    val costPrice: Long,
    val sellingPrice: Long
)