package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.PlayerProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProgressDao {
    @Query("SELECT * FROM player_progress WHERE id = 1")
    fun getProgress(): Flow<PlayerProgress?>

    @Query("SELECT * FROM player_progress WHERE id = 1")
    suspend fun getProgressDirect(): PlayerProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: PlayerProgress)
}
