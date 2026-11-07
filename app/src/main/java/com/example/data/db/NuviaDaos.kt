package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.JournalEntryEntity
import com.example.data.model.ScheduleTaskEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY id ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Long)

    @Query("DELETE FROM habits")
    suspend fun clearAllHabits()

    // Completions
    @Query("SELECT * FROM habit_completions ORDER BY date DESC, completedAt DESC")
    fun getAllCompletions(): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    suspend fun getCompletionsForDateSync(date: String): List<HabitCompletionEntity>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date DESC")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCompletions(completions: List<HabitCompletionEntity>)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: Long, date: String)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long)

    @Query("DELETE FROM habit_completions")
    suspend fun clearAllCompletions()
}

@Dao
interface ScheduleDao {
    @Query("SELECT * FROM schedule_tasks WHERE date = :date ORDER BY startTime ASC")
    fun getTasksForDate(date: String): Flow<List<ScheduleTaskEntity>>

    @Query("SELECT * FROM schedule_tasks ORDER BY date DESC, startTime ASC")
    fun getAllTasks(): Flow<List<ScheduleTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ScheduleTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<ScheduleTaskEntity>)

    @Update
    suspend fun updateTask(task: ScheduleTaskEntity)

    @Query("UPDATE schedule_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun updateTaskCompletion(id: Long, completed: Boolean)

    @Query("DELETE FROM schedule_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("DELETE FROM schedule_tasks")
    suspend fun clearAllTasks()
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY date DESC")
    fun getAllJournalEntries(): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun getJournalForDate(date: String): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getJournalForDateSync(date: String): JournalEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJournal(entry: JournalEntryEntity): Long

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteJournalById(id: Long)

    @Query("DELETE FROM journal_entries")
    suspend fun clearAllJournals()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)
}
