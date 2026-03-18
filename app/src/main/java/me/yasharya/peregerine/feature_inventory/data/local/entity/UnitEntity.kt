package me.yasharya.peregerine.feature_inventory.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
    indices = [Index(value = ["name"], unique = true)]
)
data class UnitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val isPreset: Boolean
)