package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.ScheduleTaskEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class,
        ScheduleTaskEntity::class,
        JournalEntryEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NuviaDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun journalDao(): JournalDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: NuviaDatabase? = null

        fun getDatabase(context: Context): NuviaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NuviaDatabase::class.java,
                    "nuvia_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
