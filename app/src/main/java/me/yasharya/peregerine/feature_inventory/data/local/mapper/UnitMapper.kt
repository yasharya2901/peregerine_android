package me.yasharya.peregerine.feature_inventory.data.local.mapper

import me.yasharya.peregerine.feature_inventory.data.local.entity.UnitEntity
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit

fun UnitEntity.toDomain() = MeasureUnit(id = id, name = name, isPreset = isPreset)
fun MeasureUnit.toEntity() = UnitEntity(id = id, name = name, isPreset = isPreset)