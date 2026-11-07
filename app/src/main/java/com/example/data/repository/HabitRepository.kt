package com.example.data.repository

import android.content.Context
import com.example.data.db.NuviaDatabase
import com.example.data.model.HabitCategory
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitPriority
import com.example.data.model.HabitTemplates
import com.example.data.model.JournalEntryEntity
import com.example.data.model.MoodType
import com.example.data.model.ScheduleTaskEntity
import com.example.data.model.UserProfileEntity
import com.example.receiver.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitRepository(
    private val database: NuviaDatabase,
    private val context: Context
) {
    private val habitDao = database.habitDao()
    private val scheduleDao = database.scheduleDao()
    private val journalDao = database.journalDao()
    private val userProfileDao = database.userProfileDao()

    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val activeHabits: Flow<List<HabitEntity>> = habitDao.getActiveHabits()
    val allCompletions: Flow<List<HabitCompletionEntity>> = habitDao.getAllCompletions()
    val allScheduleTasks: Flow<List<ScheduleTaskEntity>> = scheduleDao.getAllTasks()
    val allJournalEntries: Flow<List<JournalEntryEntity>> = journalDao.getAllJournalEntries()
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    fun getCompletionsForDate(date: String): Flow<List<HabitCompletionEntity>> {
        return habitDao.getCompletionsForDate(date)
    }

    fun getScheduleForDate(date: String): Flow<List<ScheduleTaskEntity>> {
        return scheduleDao.getTasksForDate(date)
    }

    fun getJournalForDate(date: String): Flow<JournalEntryEntity?> {
        return journalDao.getJournalForDate(date)
    }

    suspend fun insertHabit(habit: HabitEntity): Long = withContext(Dispatchers.IO) {
        val id = habitDao.insertHabit(habit)
        if (habit.reminderTime.isNotBlank()) {
            NotificationHelper.scheduleHabitAlarm(context, id, habit.title, habit.reminderTime)
        }
        id
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
        if (habit.reminderTime.isNotBlank()) {
            NotificationHelper.scheduleHabitAlarm(context, habit.id, habit.title, habit.reminderTime)
        } else {
            NotificationHelper.cancelHabitAlarm(context, habit.id)
        }
    }

    suspend fun deleteHabit(habitId: Long) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
        habitDao.deleteCompletionsForHabit(habitId)
        NotificationHelper.cancelHabitAlarm(context, habitId)
    }

    suspend fun toggleHabitCompletion(habitId: Long, date: String): Boolean = withContext(Dispatchers.IO) {
        val existing = habitDao.getCompletionsForDateSync(date).find { it.habitId == habitId }
        val isNowCompleted: Boolean
        if (existing != null) {
            habitDao.deleteCompletion(habitId, date)
            isNowCompleted = false
        } else {
            habitDao.insertCompletion(
                HabitCompletionEntity(
                    habitId = habitId,
                    date = date,
                    completedAt = System.currentTimeMillis()
                )
            )
            isNowCompleted = true
            addXp(15)
        }
        isNowCompleted
    }

    suspend fun insertScheduleTask(task: ScheduleTaskEntity): Long = withContext(Dispatchers.IO) {
        scheduleDao.insertTask(task)
    }

    suspend fun updateScheduleTask(task: ScheduleTaskEntity) = withContext(Dispatchers.IO) {
        scheduleDao.updateTask(task)
    }

    suspend fun toggleScheduleTaskCompletion(id: Long, currentCompleted: Boolean) = withContext(Dispatchers.IO) {
        scheduleDao.updateTaskCompletion(id, !currentCompleted)
        if (!currentCompleted) {
            addXp(10)
        }
    }

    suspend fun deleteScheduleTask(id: Long) = withContext(Dispatchers.IO) {
        scheduleDao.deleteTaskById(id)
    }

    suspend fun saveJournalEntry(entry: JournalEntryEntity) = withContext(Dispatchers.IO) {
        journalDao.insertOrUpdateJournal(entry)
        addXp(20)
    }

    suspend fun updateProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun addXp(amount: Int) = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val newXp = profile.xpPoints + amount
        val newLevel = (newXp / 100) + 1
        userProfileDao.insertOrUpdateProfile(
            profile.copy(
                xpPoints = newXp,
                userLevel = newLevel
            )
        )
    }

    // Smart conflict detection: Checks if two tasks overlap on the same date
    fun findScheduleConflicts(tasks: List<ScheduleTaskEntity>): List<Pair<ScheduleTaskEntity, ScheduleTaskEntity>> {
        val conflicts = mutableListOf<Pair<ScheduleTaskEntity, ScheduleTaskEntity>>()
        for (i in tasks.indices) {
            for (j in i + 1 until tasks.size) {
                val t1 = tasks[i]
                val t2 = tasks[j]
                if (isTimeOverlap(t1.startTime, t1.endTime, t2.startTime, t2.endTime)) {
                    conflicts.add(Pair(t1, t2))
                }
            }
        }
        return conflicts
    }

    private fun isTimeOverlap(start1: String, end1: String, start2: String, end2: String): Boolean {
        val s1 = timeToMinutes(start1)
        val e1 = timeToMinutes(end1)
        val s2 = timeToMinutes(start2)
        val e2 = timeToMinutes(end2)
        return maxOf(s1, s2) < minOf(e1, e2)
    }

    private fun timeToMinutes(time: String): Int {
        val parts = time.split(":")
        if (parts.size != 2) return 0
        val h = parts[0].toIntOrNull() ?: 0
        val m = parts[1].toIntOrNull() ?: 0
        return h * 60 + m
    }

    // Streak calculation
    suspend fun calculateCurrentStreak(completions: List<HabitCompletionEntity>): Int = withContext(Dispatchers.Default) {
        if (completions.isEmpty()) return@withContext 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val completedDates = completions.map { it.date }.toSet()

        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        // Check if user completed something today or yesterday to maintain active streak
        if (!completedDates.contains(todayStr) && !completedDates.contains(yesterdayStr)) {
            return@withContext 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!completedDates.contains(todayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dStr = dateFormat.format(checkCal.time)
            if (completedDates.contains(dStr)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        streak
    }

    // Export data to JSON string
    suspend fun exportDataJson(): String = withContext(Dispatchers.IO) {
        val json = JSONObject()
        val habitsArr = JSONArray()
        val completionsArr = JSONArray()
        val scheduleArr = JSONArray()
        val journalArr = JSONArray()

        val profile = userProfileDao.getUserProfileSync() ?: UserProfileEntity()
        val profileObj = JSONObject().apply {
            put("name", profile.name)
            put("language", profile.language)
            put("dailyGoalCount", profile.dailyGoalCount)
            put("xpPoints", profile.xpPoints)
            put("userLevel", profile.userLevel)
        }
        json.put("profile", profileObj)
        json.put("exportedAt", System.currentTimeMillis())
        json.toString(2)
    }

    // Clear all and reseed initial demo data
    suspend fun populateDemoData(clearExisting: Boolean = false) = withContext(Dispatchers.IO) {
        if (clearExisting) {
            habitDao.clearAllHabits()
            habitDao.clearAllCompletions()
            scheduleDao.clearAllTasks()
            journalDao.clearAllJournals()
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        // Create user profile
        userProfileDao.insertOrUpdateProfile(
            UserProfileEntity(
                name = "Ruxshona",
                language = "uz",
                dailyGoalCount = 5,
                xpPoints = 320,
                userLevel = 4,
                hasCompletedOnboarding = true
            )
        )

        // Add pre-made habits
        val starterTemplates = HabitTemplates.starterHabits.take(6)
        val createdHabitIds = mutableListOf<Long>()

        for (template in starterTemplates) {
            val habit = HabitEntity(
                title = template.titleUz,
                description = template.descUz,
                category = template.category.name,
                iconName = template.iconName,
                colorHex = template.colorHex,
                startDate = todayStr,
                timeStart = template.timeStart,
                timeEnd = template.timeEnd,
                reminderTime = template.reminderTime,
                priority = template.priority.name
            )
            val id = habitDao.insertHabit(habit)
            createdHabitIds.add(id)
            if (template.reminderTime.isNotBlank()) {
                NotificationHelper.scheduleHabitAlarm(context, id, habit.title, habit.reminderTime)
            }
        }

        // Add realistic completions for past 7 days to establish active streak & beautiful charts
        val completionsList = mutableListOf<HabitCompletionEntity>()
        for (dayOffset in 0..6) {
            val dCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -dayOffset) }
            val dateStr = dateFormat.format(dCal.time)

            // Complete 3-5 habits each day
            val habitsToComplete = if (dayOffset == 0) createdHabitIds.take(3) else createdHabitIds.take(4)
            for (hId in habitsToComplete) {
                completionsList.add(
                    HabitCompletionEntity(
                        habitId = hId,
                        date = dateStr,
                        completedAt = dCal.timeInMillis
                    )
                )
            }
        }
        habitDao.insertAllCompletions(completionsList)

        // Add today's schedule tasks
        val scheduleList = listOf(
            ScheduleTaskEntity(
                title = "Ertalabki rejalashtirish & NUVIA",
                description = "Kungi maqsadlarni ko'rib chiqish",
                date = todayStr,
                startTime = "06:45",
                endTime = "07:15",
                category = HabitCategory.PERSONAL.name,
                colorHex = "#F8E3A1",
                isCompleted = true
            ),
            ScheduleTaskEntity(
                title = "Ingliz tili darsi",
                description = "Speaking va grammatika mashg'uloti",
                date = todayStr,
                startTime = "08:30",
                endTime = "09:30",
                category = HabitCategory.STUDY.name,
                colorHex = "#D9C9EB",
                isCompleted = true
            ),
            ScheduleTaskEntity(
                title = "Loyiha ustida ishlash",
                description = "Asosiy modullarni yakunlash",
                date = todayStr,
                startTime = "10:00",
                endTime = "12:30",
                category = HabitCategory.WORK.name,
                colorHex = "#E8D7D2",
                isCompleted = false
            ),
            ScheduleTaskEntity(
                title = "Tushlik va ochiq havoda sayr",
                description = "Dam olish va tetiklashish",
                date = todayStr,
                startTime = "13:00",
                endTime = "14:00",
                category = HabitCategory.HEALTH.name,
                colorHex = "#A8BFA3",
                isCompleted = false
            ),
            ScheduleTaskEntity(
                title = "Sport zali mashg'uloti",
                description = "Fitnes va jismoniy faollik",
                date = todayStr,
                startTime = "18:30",
                endTime = "19:45",
                category = HabitCategory.FITNESS.name,
                colorHex = "#F4A896",
                isCompleted = false
            ),
            ScheduleTaskEntity(
                title = "Kitob mutolaasi",
                description = "Foydali bo'limlarni o'qish",
                date = todayStr,
                startTime = "21:00",
                endTime = "21:40",
                category = HabitCategory.STUDY.name,
                colorHex = "#B5D5E6",
                isCompleted = false
            )
        )
        scheduleDao.insertAllTasks(scheduleList)

        // Add today's journal
        journalDao.insertOrUpdateJournal(
            JournalEntryEntity(
                date = todayStr,
                mood = MoodType.GREAT.name,
                achievements = "Bugun ertalab 06:00 da uyg'ondim va kunlik rejamni to'liq belgilab oldim.",
                challenges = "Kun o'rtasida chalg'ituvchi xabarlar ko'p bo'ldi, ammo fokus rejimini yoqib uddaladim.",
                gratitude = "Oila a'zolarimning qo'llab-quvvatlashi va yangi imkoniyatlar uchun minnatdorman.",
                tomorrowGoals = "Ertaga kamida 25 sahifa kitob o'qish va 5 km yugurish."
            )
        )
    }
}
