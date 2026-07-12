package com.example.data.repository

import com.example.data.dao.LevelHistoryDao
import com.example.data.dao.PlayerProgressDao
import com.example.data.entity.LevelHistory
import com.example.data.entity.PlayerProgress
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val playerProgressDao: PlayerProgressDao,
    private val levelHistoryDao: LevelHistoryDao
) {
    val playerProgress: Flow<PlayerProgress?> = playerProgressDao.getProgress()

    suspend fun getProgressDirect(): PlayerProgress? = playerProgressDao.getProgressDirect()

    suspend fun saveProgress(progress: PlayerProgress) {
        playerProgressDao.saveProgress(progress)
    }

    val levelHistory: Flow<List<LevelHistory>> = levelHistoryDao.getAllHistory()

    suspend fun insertHistory(history: LevelHistory) {
        levelHistoryDao.insertHistory(history)
    }

    suspend fun clearHistory() {
        levelHistoryDao.clearAll()
    }
}
