package com.example.data.model

object Localization {

    enum class AppLanguage(val code: String, val title: String) {
        UZ("uz", "O'zbekcha"),
        RU("ru", "Русский"),
        EN("en", "English")
    }

    private val translations = mapOf(
        "app_title" to mapOf(
            "uz" to "NUVIA",
            "ru" to "NUVIA",
            "en" to "NUVIA"
        ),
        "app_tagline" to mapOf(
            "uz" to "Aqlli va estetik odatlar kuzatuvchisi",
            "ru" to "Умный и эстетичный трекер привычек",
            "en" to "Smart & aesthetic habit tracker"
        ),
        "nav_home" to mapOf("uz" to "Bosh sahifa", "ru" to "Главная", "en" to "Home"),
        "nav_habits" to mapOf("uz" to "Odatlar", "ru" to "Привычки", "en" to "Habits"),
        "nav_schedule" to mapOf("uz" to "Jadval", "ru" to "Расписание", "en" to "Schedule"),
        "nav_statistics" to mapOf("uz" to "Statistika", "ru" to "Статистика", "en" to "Stats"),
        "nav_profile" to mapOf("uz" to "Profil", "ru" to "Профиль", "en" to "Profile"),
        "nav_journal" to mapOf("uz" to "Kundalik", "ru" to "Дневник", "en" to "Journal"),
        "nav_focus" to mapOf("uz" to "Fokus", "ru" to "Фокус", "en" to "Focus"),
        "nav_calendar" to mapOf("uz" to "Kalendar", "ru" to "Календарь", "en" to "Calendar"),

        // Greetings
        "greeting_morning" to mapOf("uz" to "Xayrli tong", "ru" to "Доброе утро", "en" to "Good morning"),
        "greeting_afternoon" to mapOf("uz" to "Xayrli kun", "ru" to "Добрый день", "en" to "Good afternoon"),
        "greeting_evening" to mapOf("uz" to "Xayrli oqshom", "ru" to "Добрый вечер", "en" to "Good evening"),

        // Home
        "today_progress" to mapOf(
            "uz" to "Bugungi maqsadingizning %d%% bajarildi!",
            "ru" to "Выполнено %d%% сегодняшней цели!",
            "en" to "%d%% of today's goal completed!"
        ),
        "streak_days" to mapOf("uz" to "kunlik streak", "ru" to "дн. серия", "en" to "day streak"),
        "today_habits" to mapOf("uz" to "Bugungi odatlar", "ru" to "Привычки на сегодня", "en" to "Today's Habits"),
        "today_schedule" to mapOf("uz" to "Bugungi reja", "ru" to "План на сегодня", "en" to "Today's Schedule"),
        "completed_count" to mapOf("uz" to "bajarildi", "ru" to "выполнено", "en" to "completed"),
        "morning_routine" to mapOf("uz" to "Ertalabki tartib", "ru" to "Утренняя рутина", "en" to "Morning Routine"),
        "evening_routine" to mapOf("uz" to "Kechki tartib", "ru" to "Вечерняя рутина", "en" to "Evening Routine"),
        "quick_actions" to mapOf("uz" to "Tezkor amallar", "ru" to "Быстрые действия", "en" to "Quick Actions"),
        "view_all" to mapOf("uz" to "Hammasi", "ru" to "Все", "en" to "View All"),

        // Habit details
        "new_habit" to mapOf("uz" to "Yangi odat", "ru" to "Новая привычка", "en" to "New Habit"),
        "edit_habit" to mapOf("uz" to "Odatni tahrirlash", "ru" to "Редактировать", "en" to "Edit Habit"),
        "habit_name" to mapOf("uz" to "Odat nomi", "ru" to "Название привычки", "en" to "Habit Name"),
        "habit_desc" to mapOf("uz" to "Tavsifi (ixtiyoriy)", "ru" to "Описание (необязательно)", "en" to "Description (optional)"),
        "habit_category" to mapOf("uz" to "Kategoriya", "ru" to "Категория", "en" to "Category"),
        "habit_frequency" to mapOf("uz" to "Takrorlanish", "ru" to "Повторение", "en" to "Frequency"),
        "freq_daily" to mapOf("uz" to "Har kuni", "ru" to "Каждый день", "en" to "Daily"),
        "freq_weekdays" to mapOf("uz" to "Ish kunlari", "ru" to "Будни", "en" to "Weekdays"),
        "freq_weekends" to mapOf("uz" to "Dam olish kunlari", "ru" to "Выходные", "en" to "Weekends"),
        "freq_custom" to mapOf("uz" to "Maxsus kunlar", "ru" to "Выборочные дни", "en" to "Custom Days"),
        "start_time" to mapOf("uz" to "Boshlanish vaqti", "ru" to "Время начала", "en" to "Start Time"),
        "end_time" to mapOf("uz" to "Tugash vaqti", "ru" to "Время окончания", "en" to "End Time"),
        "reminder_time" to mapOf("uz" to "Eslatma vaqti", "ru" to "Время напоминания", "en" to "Reminder Time"),
        "target_days" to mapOf("uz" to "Maqsad davomiyligi", "ru" to "Целевая длительность", "en" to "Target Duration"),
        "priority" to mapOf("uz" to "Muhimlik darajasi", "ru" to "Приоритет", "en" to "Priority"),
        "priority_high" to mapOf("uz" to "Yuqori", "ru" to "Высокий", "en" to "High"),
        "priority_medium" to mapOf("uz" to "O'rtacha", "ru" to "Средний", "en" to "Medium"),
        "priority_low" to mapOf("uz" to "Past", "ru" to "Низкий", "en" to "Low"),
        "color_label" to mapOf("uz" to "Rang tanlash", "ru" to "Выбор цвета", "en" to "Choose Color"),
        "icon_label" to mapOf("uz" to "Ikonka yoki belgi", "ru" to "Иконка", "en" to "Icon"),
        "save" to mapOf("uz" to "Saqlash", "ru" to "Сохранить", "en" to "Save"),
        "cancel" to mapOf("uz" to "Bekor qilish", "ru" to "Отмена", "en" to "Cancel"),
        "delete" to mapOf("uz" to "O'chirish", "ru" to "Удалить", "en" to "Delete"),
        "archive" to mapOf("uz" to "To'xtatish (Arxiv)", "ru" to "Архивировать", "en" to "Archive"),
        "templates" to mapOf("uz" to "Tayyor to'plamlar", "ru" to "Шаблоны", "en" to "Templates"),

        // Categories
        "cat_health" to mapOf("uz" to "Salomatlik", "ru" to "Здоровье", "en" to "Health"),
        "cat_study" to mapOf("uz" to "O'qish & Ta'lim", "ru" to "Учеба", "en" to "Study"),
        "cat_work" to mapOf("uz" to "Ish & Biznes", "ru" to "Работа", "en" to "Work"),
        "cat_spiritual" to mapOf("uz" to "Ma'naviyat", "ru" to "Духовность", "en" to "Spiritual"),
        "cat_personal" to mapOf("uz" to "Shaxsiy rivojlanish", "ru" to "Личное", "en" to "Personal"),
        "cat_fitness" to mapOf("uz" to "Fitnes & Sport", "ru" to "Спорт", "en" to "Fitness"),
        "cat_mind" to mapOf("uz" to "Meditatsiya & Ong", "ru" to "Осознанность", "en" to "Mindfulness"),

        // Schedule
        "schedule_title" to mapOf("uz" to "Kunlik jadval", "ru" to "Расписание на день", "en" to "Daily Schedule"),
        "add_task" to mapOf("uz" to "Vazifa qo'shish", "ru" to "Добавить задачу", "en" to "Add Task"),
        "task_name" to mapOf("uz" to "Vazifa nomi", "ru" to "Название задачи", "en" to "Task Name"),
        "conflict_alert" to mapOf(
            "uz" to "Diqqat: Bir vaqtda bir nechta vazifa rejalashtirilgan!",
            "ru" to "Внимание: Запланировано несколько задач на одно время!",
            "en" to "Warning: Multiple tasks scheduled at the same time!"
        ),
        "now_indicator" to mapOf("uz" to "Hozirgi vaqt", "ru" to "Сейчас", "en" to "Now"),

        // Statistics
        "stats_title" to mapOf("uz" to "Statistika va Tahlil", "ru" to "Статистика и Анализ", "en" to "Statistics & Analysis"),
        "weekly_progress" to mapOf("uz" to "Haftalik natijalar", "ru" to "Недельный прогресс", "en" to "Weekly Progress"),
        "monthly_progress" to mapOf("uz" to "Oylik natijalar", "ru" to "Месячный прогресс", "en" to "Monthly Progress"),
        "best_habit" to mapOf("uz" to "Eng ko'p bajarilgan odat", "ru" to "Лучшая привычка", "en" to "Most Consistent"),
        "worst_habit" to mapOf("uz" to "Rivojlanish kerak bo'lgan odat", "ru" to "Требует внимания", "en" to "Needs Attention"),
        "smart_suggestions" to mapOf("uz" to "Aqlli tavsiyalar", "ru" to "Умные советы", "en" to "Smart Suggestions"),
        "total_completions" to mapOf("uz" to "Umumiy bajarilganlar", "ru" to "Всего выполнений", "en" to "Total Completions"),
        "current_streak" to mapOf("uz" to "Joriy streak", "ru" to "Текущая серия", "en" to "Current Streak"),
        "longest_streak" to mapOf("uz" to "Eng uzoq streak", "ru" to "Лучшая серия", "en" to "Best Streak"),

        // Gamification
        "xp_label" to mapOf("uz" to "Tajriba ballari", "ru" to "Очки опыта", "en" to "Experience"),
        "level_label" to mapOf("uz" to "Bosqich", "ru" to "Уровень", "en" to "Level"),
        "achievements_title" to mapOf("uz" to "Yutuqlar va Nishonlar", "ru" to "Достижения", "en" to "Achievements"),
        "challenges_title" to mapOf("uz" to "Kunlik sinovlar", "ru" to "Ежедневные челленджи", "en" to "Daily Challenges"),

        // Journal
        "journal_title" to mapOf("uz" to "Shaxsiy kundalik", "ru" to "Личный дневник", "en" to "Personal Journal"),
        "mood_prompt" to mapOf("uz" to "Bugungi kayfiyatingiz qanday?", "ru" to "Какое настроение сегодня?", "en" to "How's your mood today?"),
        "mood_great" to mapOf("uz" to "A'lo", "ru" to "Отлично", "en" to "Great"),
        "mood_good" to mapOf("uz" to "Yaxshi", "ru" to "Хорошо", "en" to "Good"),
        "mood_neutral" to mapOf("uz" to "O'rtacha", "ru" to "Нормально", "en" to "Neutral"),
        "mood_bad" to mapOf("uz" to "Yomon", "ru" to "Плохо", "en" to "Bad"),
        "mood_terrible" to mapOf("uz" to "Qiyin", "ru" to "Тяжело", "en" to "Rough"),
        "achievements_field" to mapOf("uz" to "Bugungi yutuqlarim", "ru" to "Мои победы за день", "en" to "Today's wins"),
        "challenges_field" to mapOf("uz" to "Qiyinchiliklar", "ru" to "Трудности и уроки", "en" to "Challenges & lessons"),
        "gratitude_field" to mapOf("uz" to "Minnatdorchilik yozuvlari", "ru" to "Благодарность за...", "en" to "Gratitude notes"),
        "tomorrow_goals_field" to mapOf("uz" to "Ertangi kun uchun maqsadlar", "ru" to "Цели на завтра", "en" to "Goals for tomorrow"),

        // Focus Mode
        "focus_title" to mapOf("uz" to "Fokus rejimi", "ru" to "Режим фокуса", "en" to "Focus Mode"),
        "focus_work" to mapOf("uz" to "Ish vaqti", "ru" to "Фокус", "en" to "Focus Work"),
        "focus_break" to mapOf("uz" to "Tanaffus", "ru" to "Перерыв", "en" to "Short Break"),
        "start" to mapOf("uz" to "Boshlash", "ru" to "Старт", "en" to "Start"),
        "pause" to mapOf("uz" to "Pauza", "ru" to "Пауза", "en" to "Pause"),
        "resume" to mapOf("uz" to "Davom ettirish", "ru" to "Продолжить", "en" to "Resume"),
        "reset" to mapOf("uz" to "Qayta boshlash", "ru" to "Сброс", "en" to "Reset"),

        // Profile & Settings
        "profile_title" to mapOf("uz" to "Profil va Sozlamalar", "ru" to "Профиль и Настройки", "en" to "Profile & Settings"),
        "language_setting" to mapOf("uz" to "Ilova tili", "ru" to "Язык приложения", "en" to "App Language"),
        "theme_setting" to mapOf("uz" to "Mavzu rejimi", "ru" to "Тема оформления", "en" to "Theme Mode"),
        "theme_light" to mapOf("uz" to "Yorug' (Pastel Cream)", "ru" to "Светлая", "en" to "Light (Pastel Cream)"),
        "theme_dark" to mapOf("uz" to "Qorong'i (Deep Charcoal)", "ru" to "Темная", "en" to "Dark (Deep Charcoal)"),
        "notifications_setting" to mapOf("uz" to "Bildirishnomalar", "ru" to "Уведомления", "en" to "Notifications"),
        "send_test_notification" to mapOf("uz" to "Test bildirishnoma yuborish", "ru" to "Тестовое уведомление", "en" to "Send Test Notification"),
        "export_data" to mapOf("uz" to "Ma'lumotlarni eksport qilish (JSON)", "ru" to "Экспорт данных (JSON)", "en" to "Export Data (JSON)"),
        "restore_data" to mapOf("uz" to "Zaxirani tiklash", "ru" to "Восстановить из копии", "en" to "Restore Backup"),
        "reset_demo" to mapOf("uz" to "Demo odatlarni qayta yuklash", "ru" to "Перезагрузить демо-данные", "en" to "Reload Demo Habits"),
        "clear_all_data" to mapOf("uz" to "Barcha ma'lumotlarni tozalash", "ru" to "Очистить все данные", "en" to "Clear All Data"),

        // Onboarding
        "onboarding_welcome" to mapOf("uz" to "NUVIA ga xush kelibsiz", "ru" to "Добро пожаловать в NUVIA", "en" to "Welcome to NUVIA"),
        "onboarding_subtitle" to mapOf("uz" to "Kuningizni rejalashtiring, foydali odatlarni shakllantiring va natijalaringizdan rohatlaning.", "ru" to "Планируйте свой день, формируйте полезные привычки и наслаждайтесь результатами.", "en" to "Plan your day, cultivate mindful habits, and celebrate your progress."),
        "your_name" to mapOf("uz" to "Ismingiz", "ru" to "Ваше имя", "en" to "Your Name"),
        "daily_goal_label" to mapOf("uz" to "Kunlik maqsad (odatlar soni)", "ru" to "Цель на день (количество)", "en" to "Daily Target Habits"),
        "choose_habits" to mapOf("uz" to "Rivojlantirmoqchi bo'lgan odatlaringizni tanlang", "ru" to "Выберите привычки для старта", "en" to "Select habits to cultivate"),
        "get_started" to mapOf("uz" to "Boshlash", "ru" to "Начать путь", "en" to "Get Started"),
        "habit_completed_toast" to mapOf("uz" to "Ajoyib! Odat bajarildi 🎉", "ru" to "Отлично! Привычка выполнена 🎉", "en" to "Awesome! Habit completed 🎉")
    )

    fun get(key: String, lang: String): String {
        val normalizedLang = when (lang.lowercase()) {
            "ru" -> "ru"
            "en" -> "en"
            else -> "uz"
        }
        return translations[key]?.get(normalizedLang)
            ?: translations[key]?.get("uz")
            ?: key
    }
}
