package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.LevelHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface LevelHistoryDao {
    @Query("SELECT * FROM level_history ORDER BY levelNumber ASC")
    fun getAllHistory(): Flow<List<LevelHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: LevelHistory)

    @Query("DELETE FROM level_history")
    suspend fun clearAll()
}
