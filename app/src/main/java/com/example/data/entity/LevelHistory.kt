package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "level_history")
data class LevelHistory(
    @PrimaryKey val levelNumber: Int,
    val score: Int,
    val stars: Int,
    val completionTimeSec: Int,
    val timestamp: Long = System.currentTimeMillis()
)
