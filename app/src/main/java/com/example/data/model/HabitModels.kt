package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HabitCategory(val key: String, val iconName: String, val defaultColorHex: String) {
    HEALTH("cat_health", "water_drop", "#A8BFA3"),
    STUDY("cat_study", "menu_book", "#D9C9EB"),
    WORK("cat_work", "business_center", "#E8D7D2"),
    SPIRITUAL("cat_spiritual", "auto_awesome", "#B5D5E6"),
    PERSONAL("cat_personal", "self_improvement", "#F8E3A1"),
    FITNESS("cat_fitness", "fitness_center", "#F4A896"),
    MIND("cat_mind", "spa", "#C5DCA0")
}

enum class FrequencyType {
    DAILY,
    WEEKDAYS,
    WEEKENDS,
    CUSTOM
}

enum class HabitPriority {
    HIGH,
    MEDIUM,
    LOW
}

enum class MoodType(val emoji: String, val labelKey: String, val score: Int) {
    GREAT("😊", "mood_great", 5),
    GOOD("🙂", "mood_good", 4),
    NEUTRAL("😐", "mood_neutral", 3),
    BAD("😔", "mood_bad", 2),
    TERRIBLE("😣", "mood_terrible", 1)
}

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = HabitCategory.HEALTH.name,
    val iconName: String = "check_circle",
    val colorHex: String = "#A8BFA3",
    val startDate: String, // YYYY-MM-DD
    val frequencyType: String = FrequencyType.DAILY.name,
    val customDays: String = "1,2,3,4,5,6,7", // comma separated day numbers
    val timeStart: String = "08:00",
    val timeEnd: String = "08:30",
    val reminderTime: String = "07:55",
    val targetDays: Int = 30,
    val priority: String = HabitPriority.MEDIUM.name,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_completions")
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String, // YYYY-MM-DD
    val completedAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "schedule_tasks")
data class ScheduleTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:mm
    val endTime: String, // HH:mm
    val category: String = HabitCategory.WORK.name,
    val colorHex: String = "#E8D7D2",
    val isCompleted: Boolean = false,
    val reminderTime: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val mood: String = MoodType.GOOD.name,
    val achievements: String = "",
    val challenges: String = "",
    val gratitude: String = "",
    val tomorrowGoals: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Ruxshona",
    val avatarId: String = "avatar_1",
    val language: String = "uz", // "uz", "ru", "en"
    val isDarkMode: Boolean? = null, // null = system, true = dark, false = light
    val notificationsEnabled: Boolean = true,
    val dailyGoalCount: Int = 5,
    val xpPoints: Int = 150,
    val userLevel: Int = 2,
    val hasCompletedOnboarding: Boolean = true
)

data class MotivationalQuote(
    val uz: String,
    val ru: String,
    val en: String,
    val author: String
)

object QuoteLibrary {
    val quotes = listOf(
        MotivationalQuote(
            uz = "Kichik qadamlar har kuni takrorlansa, ulkan natijalarga olib keladi.",
            ru = "Маленькие ежедневные шаги приводят к великим результатам.",
            en = "Small daily improvements over time lead to stunning results.",
            author = "Robin Sharma"
        ),
        MotivationalQuote(
            uz = "Biz takror va takror qiladigan narsamizning hosilasimiz. Demak, mukammallik harakat emas, balki odatdir.",
            ru = "Мы являемся тем, что мы постоянно делаем. Совершенство, следовательно, не действие, а привычка.",
            en = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Aristotle"
        ),
        MotivationalQuote(
            uz = "Bugun ekilgan odat urug'i, ertaga muvaffaqiyat mevasi bo'lib qaytadi.",
            ru = "Семя привычки, посеянное сегодня, завтра станет плодом успеха.",
            en = "The habit you cultivate today becomes the fruit of success tomorrow.",
            author = "NUVIA Daily Wisdom"
        ),
        MotivationalQuote(
            uz = "O'z vaqtingizni boshqarsangiz, o'z hayotingizni boshqargan bo'lasiz.",
            ru = "Управляя своим временем, вы управляете своей жизнью.",
            en = "Master your time, and you master your destiny.",
            author = "Seneca"
        ),
        MotivationalQuote(
            uz = "Har bir yangi tong — yaxshiroq bo'lish uchun berilgan yangi imkoniyat.",
            ru = "Каждое новое утро — это шанс стать лучшей версией себя.",
            en = "Each new morning is a fresh opportunity to bloom.",
            author = "NUVIA Inspiration"
        )
    )

    fun getDailyQuote(dayOfYear: Int): MotivationalQuote {
        val index = (dayOfYear.coerceAtLeast(0)) % quotes.size
        return quotes[index]
    }
}

data class AchievementBadge(
    val id: String,
    val titleUz: String,
    val titleRu: String,
    val titleEn: String,
    val descUz: String,
    val descRu: String,
    val descEn: String,
    val requiredStreak: Int,
    val icon: String,
    val xpReward: Int
)

object AchievementLibrary {
    val badges = listOf(
        AchievementBadge(
            id = "streak_3",
            titleUz = "Yangi boshlanish",
            titleRu = "Новый старт",
            titleEn = "Fresh Start",
            descUz = "3 kun ketma-ket odatlarni bajardingiz",
            descRu = "3 дня подряд выполняли привычки",
            descEn = "Completed habits 3 days in a row",
            requiredStreak = 3,
            icon = "🌱",
            xpReward = 50
        ),
        AchievementBadge(
            id = "streak_7",
            titleUz = "Haftalik g'alaba",
            titleRu = "Недельная победа",
            titleEn = "Weekly Master",
            descUz = "7 kun davomida to'xtamasdan intildingiz",
            descRu = "7 дней подряд без пропусков",
            descEn = "Unstoppable for a full 7 days",
            requiredStreak = 7,
            icon = "⭐",
            xpReward = 120
        ),
        AchievementBadge(
            id = "streak_14",
            titleUz = "Kuchli iroda",
            titleRu = "Сильная воля",
            titleEn = "Resilient Spirit",
            descUz = "14 kunlik barqarorlik",
            descRu = "14 дней стабильности",
            descEn = "14 days of unbroken discipline",
            requiredStreak = 14,
            icon = "🔥",
            xpReward = 250
        ),
        AchievementBadge(
            id = "streak_30",
            titleUz = "Oltin odat",
            titleRu = "Золотая привычка",
            titleEn = "Golden Habit",
            descUz = "1 oy to'liq odat shakllandi!",
            descRu = "Полный месяц успешного закрепления!",
            descEn = "A full month of lifestyle transformation!",
            requiredStreak = 30,
            icon = "👑",
            xpReward = 500
        ),
        AchievementBadge(
            id = "streak_100",
            titleUz = "NUVIA Afsonasi",
            titleRu = "Легенда NUVIA",
            titleEn = "NUVIA Legend",
            descUz = "100 kunlik buyuk muvaffaqiyat",
            descRu = "100 дней легендарного пути",
            descEn = "100 days of legendary dedication",
            requiredStreak = 100,
            icon = "💎",
            xpReward = 1500
        )
    )
}
