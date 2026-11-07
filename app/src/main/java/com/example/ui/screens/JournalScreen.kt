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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Localization
import com.example.data.model.MoodType
import com.example.ui.components.DateSelectorStrip
import com.example.ui.theme.NuviaAccent
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaSecondary
import com.example.viewmodel.NuviaScreen
import com.example.viewmodel.NuviaUiState
import com.example.viewmodel.NuviaViewModel

@Composable
fun JournalScreen(
    uiState: NuviaUiState,
    viewModel: NuviaViewModel
) {
    val lang = uiState.language
    val currentJournal = uiState.currentJournal

    var selectedMood by remember(currentJournal) {
        mutableStateOf(currentJournal?.mood ?: MoodType.GOOD.name)
    }
    var achievements by remember(currentJournal) {
        mutableStateOf(currentJournal?.achievements ?: "")
    }
    var challenges by remember(currentJournal) {
        mutableStateOf(currentJournal?.challenges ?: "")
    }
    var gratitude by remember(currentJournal) {
        mutableStateOf(currentJournal?.gratitude ?: "")
    }
    var tomorrowGoals by remember(currentJournal) {
        mutableStateOf(currentJournal?.tomorrowGoals ?: "")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("journal_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with Back button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(NuviaScreen.HOME) },
                        modifier = Modifier.testTag("btn_back_home")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = Localization.get("journal_title", lang),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fikrlar, his-tuyg'ular va minnatdorchilik",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Date picker
            item {
                DateSelectorStrip(
                    selectedDate = uiState.selectedDate,
                    onSelectDate = { viewModel.setSelectedDate(it) }
                )
            }

            // Mood Tracker Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("card_mood_picker"),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mood, contentDescription = null, tint = NuviaGold, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("mood_prompt", lang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MoodType.values().forEach { mood ->
                                val isSelected = selectedMood == mood.name
                                val label = Localization.get(mood.labelKey, lang)
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSelected) NuviaGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        )
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) NuviaGold else androidx.compose.ui.graphics.Color.Transparent,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedMood = mood.name }
                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                        .testTag("mood_${mood.name.lowercase()}")
                                ) {
                                    Text(text = mood.emoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Wins / Achievements input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NuviaPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("achievements_field", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = achievements,
                            onValueChange = { achievements = it },
                            placeholder = { Text("Bugun qaysi maqsadlarga erishdingiz?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_journal_wins"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // Challenges & Lessons
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = NuviaSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("challenges_field", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = challenges,
                            onValueChange = { challenges = it },
                            placeholder = { Text("Qanday qiyinchiliklar bo'ldi va nimalarni o'rgandingiz?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_journal_challenges"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // Gratitude Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = androidx.compose.ui.graphics.Color(0xFFE57373), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("gratitude_field", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = gratitude,
                            onValueChange = { gratitude = it },
                            placeholder = { Text("Bugungi kun uchun 3 ta minnatdorchilik...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_journal_gratitude"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // Tomorrow Goals
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = NuviaAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("tomorrow_goals_field", lang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tomorrowGoals,
                            onValueChange = { tomorrowGoals = it },
                            placeholder = { Text("Ertaga bajarilishi shart bo'lgan asosiy ishlar...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_journal_tomorrow"),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            }

            // Save Journal Button
            item {
                Button(
                    onClick = {
                        viewModel.saveJournal(
                            date = uiState.selectedDate,
                            mood = selectedMood,
                            achievements = achievements,
                            challenges = challenges,
                            gratitude = gratitude,
                            tomorrowGoals = tomorrowGoals
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_journal"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("save", lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
