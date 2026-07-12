package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_progress")
data class PlayerProgress(
    @PrimaryKey val id: Int = 1,
    val name: String = "Çiftçi",
    val level: Int = 1,
    val coins: Int = 100,
    val energy: Int = 5,
    val maxEnergy: Int = 5,
    val lastEnergyRefillTime: Long = System.currentTimeMillis(),
    val powerUndoCount: Int = 3,
    val powerMagnetCount: Int = 3,
    val powerFreezeCount: Int = 3,
    val soundVolume: Float = 0.8f,
    val musicVolume: Float = 0.8f,
    val isMuted: Boolean = false,

    // Daily Harvest Claim
    val lastDailyRewardClaimTime: Long = 0L,

    // Daily Quests Reset & Progress
    val lastDailyQuestsResetTime: Long = 0L,

    val quest1Progress: Int = 0,
    val quest1Completed: Boolean = false,
    val quest1Claimed: Boolean = false,

    val quest2Progress: Int = 0,
    val quest2Completed: Boolean = false,
    val quest2Claimed: Boolean = false,

    val quest3Progress: Int = 0,
    val quest3Completed: Boolean = false,
    val quest3Claimed: Boolean = false,

    val quest4Progress: Int = 0,
    val quest4Completed: Boolean = false,
    val quest4Claimed: Boolean = false
)
