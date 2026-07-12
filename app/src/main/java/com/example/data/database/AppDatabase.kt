package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LevelHistoryDao
import com.example.data.dao.PlayerProgressDao
import com.example.data.entity.LevelHistory
import com.example.data.entity.PlayerProgress

@Database(entities = [PlayerProgress::class, LevelHistory::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerProgressDao(): PlayerProgressDao
    abstract fun levelHistoryDao(): LevelHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "veggie_match_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
