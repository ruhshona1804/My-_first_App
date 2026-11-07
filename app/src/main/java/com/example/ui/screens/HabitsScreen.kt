package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FrequencyType
import com.example.data.model.HabitCategory
import com.example.data.model.HabitEntity
import com.example.data.model.HabitPriority
import com.example.data.model.HabitTemplates
import com.example.data.model.Localization
import com.example.ui.components.HabitCard
import com.example.ui.theme.NuviaAccent
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaPrimaryDark
import com.example.ui.theme.NuviaPrimaryLight
import com.example.ui.theme.NuviaSecondary
import com.example.viewmodel.NuviaUiState
import com.example.viewmodel.NuviaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    uiState: NuviaUiState,
    viewModel: NuviaViewModel
) {
    val lang = uiState.language
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<String?>("ALL") }

    val categories = remember {
        listOf("ALL") + HabitCategory.values().map { it.name }
    }

    val filteredHabits = uiState.allHabits.filter { habit ->
        val matchesCategory = selectedCategoryFilter == "ALL" || habit.category == selectedCategoryFilter
        val matchesSearch = searchQuery.isBlank() || habit.title.contains(searchQuery, ignoreCase = true) ||
                habit.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("habits_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddHabit() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("fab_add_habit")
                    .padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Habit")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Header & Templates button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("nav_habits", lang),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${uiState.activeHabits.size} ta faol odat",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { viewModel.openTemplatesDialog() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NuviaAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("btn_templates")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = Localization.get("templates", lang),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_habits_input"),
                placeholder = { Text(Localization.get("habit_name", lang)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { catName ->
                    val isSelected = selectedCategoryFilter == catName
                    val label = if (catName == "ALL") {
                        Localization.get("view_all", lang)
                    } else {
                        try {
                            val cat = HabitCategory.valueOf(catName)
                            Localization.get(cat.key, lang)
                        } catch (_: Exception) {
                            catName
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                            .clickable { selectedCategoryFilter = catName }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("category_pill_$catName")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Habit List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredHabits.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Odatlar topilmadi 🌿",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Qidiruv parametrlarini o'zgartiring yoki yangi odat qo'shing",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(filteredHabits) { habit ->
                        val isCompleted = uiState.completedHabitIdsForSelectedDate.contains(habit.id)
                        HabitCard(
                            habit = habit,
                            isCompleted = isCompleted,
                            onToggleCompletion = { viewModel.toggleHabitCompletion(habit.id) },
                            onEdit = { viewModel.openEditHabit(habit) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add / Edit Habit Bottom Sheet / Dialog
    if (uiState.showAddEditHabitDialog) {
        AddEditHabitSheet(
            habit = uiState.habitToEdit,
            lang = lang,
            onDismiss = { viewModel.closeAddEditHabit() },
            onSave = { viewModel.saveHabit(it) },
            onDelete = { it?.let { h -> viewModel.deleteHabit(h.id) } },
            onArchive = { it?.let { h -> viewModel.toggleArchiveHabit(h) } }
        )
    }

    // Pre-made Templates Dialog
    if (uiState.showTemplatesDialog) {
        HabitTemplatesDialog(
            lang = lang,
            onDismiss = { viewModel.closeTemplatesDialog() },
            onSelect = { template ->
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val newHabit = HabitEntity(
                    title = when (lang) {
                        "ru" -> template.titleRu
                        "en" -> template.titleEn
                        else -> template.titleUz
                    },
                    description = when (lang) {
                        "ru" -> template.descRu
                        "en" -> template.descEn
                        else -> template.descUz
                    },
                    category = template.category.name,
                    iconName = template.iconName,
                    colorHex = template.colorHex,
                    startDate = todayStr,
                    timeStart = template.timeStart,
                    timeEnd = template.timeEnd,
                    reminderTime = template.reminderTime,
                    priority = template.priority.name
                )
                viewModel.saveHabit(newHabit)
                viewModel.closeTemplatesDialog()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitSheet(
    habit: HabitEntity?,
    lang: String,
    onDismiss: () -> Unit,
    onSave: (HabitEntity) -> Unit,
    onDelete: (HabitEntity?) -> Unit,
    onArchive: (HabitEntity?) -> Unit
) {
    val isEdit = habit != null
    var title by remember { mutableStateOf(habit?.title ?: "") }
    var description by remember { mutableStateOf(habit?.description ?: "") }
    var selectedCategory by remember { mutableStateOf(habit?.category ?: HabitCategory.HEALTH.name) }
    var selectedColor by remember { mutableStateOf(habit?.colorHex ?: "#A8BFA3") }
    var frequency by remember { mutableStateOf(habit?.frequencyType ?: FrequencyType.DAILY.name) }
    var timeStart by remember { mutableStateOf(habit?.timeStart ?: "08:00") }
    var timeEnd by remember { mutableStateOf(habit?.timeEnd ?: "08:30") }
    var reminderTime by remember { mutableStateOf(habit?.reminderTime ?: "07:55") }
    var targetDays by remember { mutableStateOf(habit?.targetDays ?: 30) }
    var priority by remember { mutableStateOf(habit?.priority ?: HabitPriority.MEDIUM.name) }

    val colorOptions = listOf("#A8BFA3", "#E8D7D2", "#D9C9EB", "#F8E3A1", "#B5D5E6", "#F4A896", "#C5DCA0")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (isEdit) Localization.get("edit_habit", lang) else Localization.get("new_habit", lang),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Habit Name
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(Localization.get("habit_name", lang)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_habit_title"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(Localization.get("habit_desc", lang)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_habit_desc"),
                shape = RoundedCornerShape(14.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Picker
            Text(
                text = Localization.get("habit_category", lang),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(HabitCategory.values()) { cat ->
                    val isSel = selectedCategory == cat.name
                    val catLabel = Localization.get(cat.key, lang)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedCategory = cat.name }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = catLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Color Palette
            Text(
                text = Localization.get("color_label", lang),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                colorOptions.forEach { hex ->
                    val color = Color(android.graphics.Color.parseColor(hex))
                    val isSel = selectedColor.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSel) 3.dp else 1.dp,
                                color = if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = hex }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Frequency
            Text(
                text = Localization.get("habit_frequency", lang),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    FrequencyType.DAILY to Localization.get("freq_daily", lang),
                    FrequencyType.WEEKDAYS to Localization.get("freq_weekdays", lang),
                    FrequencyType.WEEKENDS to Localization.get("freq_weekends", lang)
                ).forEach { (freq, label) ->
                    val isSel = frequency == freq.name
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { frequency = freq.name }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSel) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Times
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = timeStart,
                    onValueChange = { timeStart = it },
                    label = { Text(Localization.get("start_time", lang)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = timeEnd,
                    onValueChange = { timeEnd = it },
                    label = { Text(Localization.get("end_time", lang)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Reminder Time
            OutlinedTextField(
                value = reminderTime,
                onValueChange = { reminderTime = it },
                label = { Text(Localization.get("reminder_time", lang)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        val updated = habit?.copy(
                            title = title,
                            description = description,
                            category = selectedCategory,
                            colorHex = selectedColor,
                            frequencyType = frequency,
                            timeStart = timeStart,
                            timeEnd = timeEnd,
                            reminderTime = reminderTime,
                            targetDays = targetDays,
                            priority = priority
                        ) ?: HabitEntity(
                            title = title,
                            description = description,
                            category = selectedCategory,
                            colorHex = selectedColor,
                            startDate = todayStr,
                            frequencyType = frequency,
                            timeStart = timeStart,
                            timeEnd = timeEnd,
                            reminderTime = reminderTime,
                            targetDays = targetDays,
                            priority = priority
                        )
                        onSave(updated)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_habit"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(Localization.get("save", lang), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (isEdit) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = { onArchive(habit) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(Localization.get("archive", lang))
                    }
                    Button(
                        onClick = { onDelete(habit) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Localization.get("delete", lang))
                    }
                }
            }
        }
    }
}

@Composable
fun HabitTemplatesDialog(
    lang: String,
    onDismiss: () -> Unit,
    onSelect: (com.example.data.model.PredefinedHabitTemplate) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = Localization.get("templates", lang),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.height(380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HabitTemplates.starterHabits) { template ->
                    val title = when (lang) {
                        "ru" -> template.titleRu
                        "en" -> template.titleEn
                        else -> template.titleUz
                    }
                    val desc = when (lang) {
                        "ru" -> template.descRu
                        "en" -> template.descEn
                        else -> template.descUz
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(template) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(android.graphics.Color.parseColor(template.colorHex))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = template.titleUz.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Localization.get("cancel", lang))
            }
        }
    )
}
