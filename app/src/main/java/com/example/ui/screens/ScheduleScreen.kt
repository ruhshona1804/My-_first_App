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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.HabitCategory
import com.example.data.model.Localization
import com.example.data.model.ScheduleTaskEntity
import com.example.ui.components.DateSelectorStrip
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaPrimaryDark
import com.example.ui.theme.NuviaSecondary
import com.example.viewmodel.NuviaUiState
import com.example.viewmodel.NuviaViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ScheduleScreen(
    uiState: NuviaUiState,
    viewModel: NuviaViewModel
) {
    val lang = uiState.language
    val tasks = uiState.scheduleTasks
    val conflicts = uiState.scheduleConflicts

    var editingTask by remember { mutableStateOf<ScheduleTaskEntity?>(null) }

    val currentTimeStr = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("schedule_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddTaskDialog() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("fab_add_task")
                    .padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Localization.get("schedule_title", lang),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${tasks.size} ta rejalashtirilgan vazifa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Current Time Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NuviaPrimary.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NuviaPrimaryDark)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${Localization.get("now_indicator", lang)}: $currentTimeStr",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Date picker horizontal selector
            item {
                DateSelectorStrip(
                    selectedDate = uiState.selectedDate,
                    onSelectDate = { viewModel.setSelectedDate(it) }
                )
            }

            // Time Conflict Warning Banner
            if (conflicts.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("conflict_warning_banner"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD)),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Conflict",
                                tint = Color(0xFF856404),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = Localization.get("conflict_alert", lang),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF856404)
                                )
                                Text(
                                    text = "${conflicts.first().first.title} ↔ ${conflicts.first().second.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF856404).copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Items
            if (tasks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
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
                                text = "Kunlik jadval bo'sh 🗓️",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Vaqtingizni unumli taqsimlash uchun birinchi vazifangizni rejalashtiring",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { viewModel.openAddTaskDialog() },
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text(Localization.get("add_task", lang))
                            }
                        }
                    }
                }
            } else {
                items(tasks) { task ->
                    val parsedColor = try {
                        Color(android.graphics.Color.parseColor(task.colorHex))
                    } catch (_: Exception) {
                        NuviaSecondary
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("timeline_task_${task.id}"),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Timeline Column: Time Label & Vertical line
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(55.dp)
                        ) {
                            Text(
                                text = task.startTime,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(45.dp)
                                    .background(parsedColor.copy(alpha = 0.6f))
                            )
                            Text(
                                text = task.endTime,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Task Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(18.dp))
                                .clickable { editingTask = task },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Completion toggle circle
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (task.isCompleted) NuviaPrimary else parsedColor.copy(alpha = 0.25f))
                                        .border(
                                            width = 1.5.dp,
                                            color = if (task.isCompleted) NuviaPrimary else parsedColor,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.toggleScheduleTask(task.id, task.isCompleted) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (task.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Completed",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = task.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (task.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = task.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteScheduleTask(task.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Add / Edit Task Dialog
    if (uiState.showAddTaskDialog || editingTask != null) {
        val currentTask = editingTask
        var title by remember { mutableStateOf(currentTask?.title ?: "") }
        var description by remember { mutableStateOf(currentTask?.description ?: "") }
        var startTime by remember { mutableStateOf(currentTask?.startTime ?: "09:00") }
        var endTime by remember { mutableStateOf(currentTask?.endTime ?: "10:00") }
        var selectedColor by remember { mutableStateOf(currentTask?.colorHex ?: "#E8D7D2") }

        AlertDialog(
            onDismissRequest = {
                viewModel.closeAddTaskDialog()
                editingTask = null
            },
            title = {
                Text(
                    text = if (currentTask != null) "Vazifani tahrirlash" else Localization.get("add_task", lang),
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(Localization.get("task_name", lang)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(Localization.get("habit_desc", lang)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text(Localization.get("start_time", lang)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text(Localization.get("end_time", lang)) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val task = currentTask?.copy(
                                title = title,
                                description = description,
                                startTime = startTime,
                                endTime = endTime,
                                colorHex = selectedColor
                            ) ?: ScheduleTaskEntity(
                                title = title,
                                description = description,
                                date = uiState.selectedDate,
                                startTime = startTime,
                                endTime = endTime,
                                colorHex = selectedColor
                            )
                            viewModel.saveScheduleTask(task)
                            editingTask = null
                        }
                    }
                ) {
                    Text(Localization.get("save", lang))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.closeAddTaskDialog()
                        editingTask = null
                    }
                ) {
                    Text(Localization.get("cancel", lang))
                }
            }
        )
    }
}
