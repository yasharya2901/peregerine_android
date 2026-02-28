package me.yasharya.peregerine.core.db.typeconverters

import androidx.room.TypeConverter
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType

class Converters {
    @TypeConverter
    fun fromStockChangeType(value: StockChangeType): String {
        return value.name
    }

    @TypeConverter
    fun toStockChangeType(value: String): StockChangeType {
        return StockChangeType.valueOf(value)
    }
}
