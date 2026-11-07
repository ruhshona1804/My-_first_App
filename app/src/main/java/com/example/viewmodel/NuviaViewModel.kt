package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.NuviaDatabase
import com.example.data.model.HabitCompletionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.HabitTemplates
import com.example.data.model.JournalEntryEntity
import com.example.data.model.Localization
import com.example.data.model.MoodType
import com.example.data.model.ScheduleTaskEntity
import com.example.data.model.UserProfileEntity
import com.example.data.repository.HabitRepository
import com.example.receiver.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private data class RepoDataBundle(
    val activeHabits: List<HabitEntity> = emptyList(),
    val allHabits: List<HabitEntity> = emptyList(),
    val completions: List<HabitCompletionEntity> = emptyList(),
    val scheduleTasks: List<ScheduleTaskEntity> = emptyList(),
    val journals: List<JournalEntryEntity> = emptyList()
)

enum class NuviaScreen {
    HOME,
    HABITS,
    SCHEDULE,
    STATISTICS,
    PROFILE,
    JOURNAL,
    FOCUS,
    CALENDAR,
    ONBOARDING
}

data class NuviaUiState(
    val currentScreen: NuviaScreen = NuviaScreen.HOME,
    val selectedDate: String = "",
    val activeHabits: List<HabitEntity> = emptyList(),
    val allHabits: List<HabitEntity> = emptyList(),
    val completedHabitIdsForSelectedDate: Set<Long> = emptySet(),
    val allCompletions: List<HabitCompletionEntity> = emptyList(),
    val currentStreak: Int = 0,
    val todayProgressPercent: Int = 0,
    val scheduleTasks: List<ScheduleTaskEntity> = emptyList(),
    val scheduleConflicts: List<Pair<ScheduleTaskEntity, ScheduleTaskEntity>> = emptyList(),
    val currentJournal: JournalEntryEntity? = null,
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val language: String = "uz",
    val isDarkTheme: Boolean = false,
    // Focus Timer
    val focusTotalSeconds: Int = 25 * 60,
    val focusRemainingSeconds: Int = 25 * 60,
    val isFocusRunning: Boolean = false,
    val isFocusBreak: Boolean = false,
    // UI Dialogs
    val showAddEditHabitDialog: Boolean = false,
    val habitToEdit: HabitEntity? = null,
    val showTemplatesDialog: Boolean = false,
    val showAddTaskDialog: Boolean = false,
    val confettiTrigger: Long = 0L,
    val toastMessage: String? = null
)

class NuviaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _currentScreen = MutableStateFlow(NuviaScreen.HOME)
    val currentScreen: StateFlow<NuviaScreen> = _currentScreen.asStateFlow()

    private val _focusTotalSeconds = MutableStateFlow(25 * 60)
    private val _focusRemainingSeconds = MutableStateFlow(25 * 60)
    private val _isFocusRunning = MutableStateFlow(false)
    private val _isFocusBreak = MutableStateFlow(false)
    private var focusTimerJob: Job? = null

    private val _showAddEditHabit = MutableStateFlow(false)
    private val _habitToEdit = MutableStateFlow<HabitEntity?>(null)
    private val _showTemplates = MutableStateFlow(false)
    private val _showAddTask = MutableStateFlow(false)
    private val _confettiTrigger = MutableStateFlow(0L)
    private val _toastMessage = MutableStateFlow<String?>(null)

    init {
        val database = NuviaDatabase.getDatabase(application)
        repository = HabitRepository(database, application)

        // Seed demo data if database is fresh
        viewModelScope.launch {
            repository.allHabits.collect { habits ->
                if (habits.isEmpty()) {
                    repository.populateDemoData(clearExisting = false)
                }
            }
        }
    }

    private val repoDataFlow = combine(
        repository.activeHabits,
        repository.allHabits,
        repository.allCompletions,
        repository.allScheduleTasks,
        repository.allJournalEntries
    ) { activeHabits, allHabits, completions, scheduleTasks, journals ->
        RepoDataBundle(activeHabits, allHabits, completions, scheduleTasks, journals)
    }

    val uiState: StateFlow<NuviaUiState> = combine(
        _currentScreen,
        _selectedDate,
        repository.userProfile,
        _focusRemainingSeconds,
        repoDataFlow
    ) { screen, date, profile, remainingSec, repoData ->

        val userProf = profile ?: UserProfileEntity()
        val lang = userProf.language

        val completedIdsForDate = repoData.completions
            .filter { it.date == date }
            .map { it.habitId }
            .toSet()

        val tasksForDate = repoData.scheduleTasks.filter { it.date == date }
        val conflicts = repository.findScheduleConflicts(tasksForDate)

        val journalForDate = repoData.journals.find { it.date == date }

        val activeCount = repoData.activeHabits.size
        val completedCount = repoData.activeHabits.count { completedIdsForDate.contains(it.id) }
        val progressPercent = if (activeCount > 0) ((completedCount.toFloat() / activeCount) * 100).toInt() else 0

        // Streak
        val streak = repository.calculateCurrentStreak(repoData.completions)

        // Screen decision for first-time launch
        val effectiveScreen = if (!userProf.hasCompletedOnboarding && screen == NuviaScreen.HOME) {
            NuviaScreen.ONBOARDING
        } else {
            screen
        }

        NuviaUiState(
            currentScreen = effectiveScreen,
            selectedDate = date,
            activeHabits = repoData.activeHabits,
            allHabits = repoData.allHabits,
            completedHabitIdsForSelectedDate = completedIdsForDate,
            allCompletions = repoData.completions,
            currentStreak = streak,
            todayProgressPercent = progressPercent,
            scheduleTasks = tasksForDate,
            scheduleConflicts = conflicts,
            currentJournal = journalForDate,
            userProfile = userProf,
            language = lang,
            isDarkTheme = userProf.isDarkMode ?: false,
            focusTotalSeconds = _focusTotalSeconds.value,
            focusRemainingSeconds = remainingSec,
            isFocusRunning = _isFocusRunning.value,
            isFocusBreak = _isFocusBreak.value,
            showAddEditHabitDialog = _showAddEditHabit.value,
            habitToEdit = _habitToEdit.value,
            showTemplatesDialog = _showTemplates.value,
            showAddTaskDialog = _showAddTask.value,
            confettiTrigger = _confettiTrigger.value,
            toastMessage = _toastMessage.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NuviaUiState()
    )

    fun navigateTo(screen: NuviaScreen) {
        _currentScreen.value = screen
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val isCompleted = repository.toggleHabitCompletion(habitId, date)
            if (isCompleted) {
                _toastMessage.value = Localization.get("habit_completed_toast", uiState.value.language)
                _confettiTrigger.value = System.currentTimeMillis()
            }
        }
    }

    fun openAddHabit() {
        _habitToEdit.value = null
        _showAddEditHabit.value = true
    }

    fun openEditHabit(habit: HabitEntity) {
        _habitToEdit.value = habit
        _showAddEditHabit.value = true
    }

    fun closeAddEditHabit() {
        _showAddEditHabit.value = false
        _habitToEdit.value = null
    }

    fun saveHabit(habit: HabitEntity) {
        viewModelScope.launch {
            if (habit.id == 0L) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }
            closeAddEditHabit()
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            closeAddEditHabit()
        }
    }

    fun toggleArchiveHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.updateHabit(habit.copy(isArchived = !habit.isArchived))
        }
    }

    fun openTemplatesDialog() {
        _showTemplates.value = true
    }

    fun closeTemplatesDialog() {
        _showTemplates.value = false
    }

    fun openAddTaskDialog() {
        _showAddTask.value = true
    }

    fun closeAddTaskDialog() {
        _showAddTask.value = false
    }

    fun saveScheduleTask(task: ScheduleTaskEntity) {
        viewModelScope.launch {
            if (task.id == 0L) {
                repository.insertScheduleTask(task)
            } else {
                repository.updateScheduleTask(task)
            }
            closeAddTaskDialog()
        }
    }

    fun toggleScheduleTask(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleScheduleTaskCompletion(id, completed)
        }
    }

    fun deleteScheduleTask(id: Long) {
        viewModelScope.launch {
            repository.deleteScheduleTask(id)
        }
    }

    fun saveJournal(
        date: String,
        mood: String,
        achievements: String,
        challenges: String,
        gratitude: String,
        tomorrowGoals: String
    ) {
        viewModelScope.launch {
            repository.saveJournalEntry(
                JournalEntryEntity(
                    date = date,
                    mood = mood,
                    achievements = achievements,
                    challenges = challenges,
                    gratitude = gratitude,
                    tomorrowGoals = tomorrowGoals
                )
            )
            _toastMessage.value = "Kundalik saqlandi ✨"
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val prof = uiState.value.userProfile
            repository.updateProfile(prof.copy(language = lang))
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            val prof = uiState.value.userProfile
            val newDark = !(prof.isDarkMode ?: false)
            repository.updateProfile(prof.copy(isDarkMode = newDark))
        }
    }

    fun updateProfile(name: String, dailyGoal: Int) {
        viewModelScope.launch {
            val prof = uiState.value.userProfile
            repository.updateProfile(prof.copy(name = name, dailyGoalCount = dailyGoal))
        }
    }

    fun completeOnboarding(name: String, selectedHabitTitles: List<String>, dailyGoal: Int) {
        viewModelScope.launch {
            val prof = uiState.value.userProfile
            repository.updateProfile(
                prof.copy(
                    name = name.ifBlank { "Ruxshona" },
                    dailyGoalCount = dailyGoal,
                    hasCompletedOnboarding = true
                )
            )
            // Add any chosen habits from starter templates
            val todayStr = dateFormat.format(Date())
            for (title in selectedHabitTitles) {
                val template = HabitTemplates.starterHabits.find { it.titleUz == title || it.titleEn == title || it.titleRu == title }
                if (template != null) {
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
                    repository.insertHabit(habit)
                }
            }
            _currentScreen.value = NuviaScreen.HOME
        }
    }

    fun sendTestNotification(context: Context) {
        val lang = uiState.value.language
        val name = uiState.value.userProfile.name
        val title = "NUVIA Eslatma"
        val msg = "$name, kitob o'qish vaqti keldi! Kunlik maqsadingiz sari yana bir qadam 🌿"
        NotificationHelper.sendImmediateNotification(context, 9999, title, msg)
        _toastMessage.value = "Test bildirishnoma yuborildi 🔔"
    }

    fun reloadDemoData() {
        viewModelScope.launch {
            repository.populateDemoData(clearExisting = true)
            _toastMessage.value = "Demo ma'lumotlar qayta yuklandi 🔄"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Focus Timer functions
    fun setFocusPreset(minutes: Int, isBreak: Boolean = false) {
        focusTimerJob?.cancel()
        _isFocusRunning.value = false
        _isFocusBreak.value = isBreak
        _focusTotalSeconds.value = minutes * 60
        _focusRemainingSeconds.value = minutes * 60
    }

    fun startFocusTimer() {
        if (_isFocusRunning.value) return
        _isFocusRunning.value = true
        focusTimerJob = viewModelScope.launch {
            while (_focusRemainingSeconds.value > 0 && _isFocusRunning.value) {
                delay(1000L)
                _focusRemainingSeconds.value -= 1
            }
            if (_focusRemainingSeconds.value <= 0) {
                _isFocusRunning.value = false
                repository.addXp(30)
                _confettiTrigger.value = System.currentTimeMillis()
                _toastMessage.value = "Fokus vaqti tugadi! Ajoyib natija 🎉"
            }
        }
    }

    fun pauseFocusTimer() {
        _isFocusRunning.value = false
        focusTimerJob?.cancel()
    }

    fun resetFocusTimer() {
        pauseFocusTimer()
        _focusRemainingSeconds.value = _focusTotalSeconds.value
    }
}
