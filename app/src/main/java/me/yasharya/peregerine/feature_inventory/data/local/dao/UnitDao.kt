package me.yasharya.peregerine.feature_inventory.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import me.yasharya.peregerine.feature_inventory.data.local.entity.UnitEntity

@Dao
interface UnitDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(units: List<UnitEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(unit: UnitEntity)

    @Query("SELECT * FROM units WHERE name LIKE '%' || :query || '%' ORDER BY isPreset DESC, name ASC")
    fun search(query: String): Flow<List<UnitEntity>>

    @Query("SELECT * FROM units ORDER BY isPreset DESC, name ASC")
    fun observeAll(): Flow<List<UnitEntity>>

    @Query("SELECT COUNT(*) FROM units WHERE isPreset = 1")
    suspend fun presetCount(): Int
}