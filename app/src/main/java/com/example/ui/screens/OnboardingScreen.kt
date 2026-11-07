package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HabitTemplates
import com.example.data.model.Localization
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaPrimaryDark
import com.example.viewmodel.NuviaUiState
import com.example.viewmodel.NuviaViewModel

@Composable
fun OnboardingScreen(
    uiState: NuviaUiState,
    viewModel: NuviaViewModel
) {
    val lang = uiState.language
    var name by remember { mutableStateOf("Ruxshona") }
    var dailyGoal by remember { mutableStateOf("5") }

    val selectedHabitTitles = remember {
        mutableStateListOf(
            "Kitob o'qish",
            "Sport bilan shug'ullanish",
            "2 litr suv ichish",
            "Ingliz tili o'rganish"
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("onboarding_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                // Aesthetic Logo and Header
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(NuviaPrimary.copy(alpha = 0.35f))
                        .border(2.dp, NuviaPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nuvia_logo),
                        contentDescription = "NUVIA Logo",
                        modifier = Modifier.size(76.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = Localization.get("onboarding_welcome", lang),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Localization.get("onboarding_subtitle", lang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            // Name Input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Localization.get("your_name", lang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Ismingizni kiriting") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_onboarding_name"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = Localization.get("daily_goal_label", lang),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dailyGoal,
                            onValueChange = { dailyGoal = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_onboarding_goal"),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                    }
                }
            }

            // Habit Selection
            item {
                Text(
                    text = Localization.get("choose_habits", lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(HabitTemplates.starterHabits) { template ->
                val title = when (lang) {
                    "ru" -> template.titleRu
                    "en" -> template.titleEn
                    else -> template.titleUz
                }
                val isSelected = selectedHabitTitles.contains(template.titleUz)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            if (isSelected) {
                                selectedHabitTitles.remove(template.titleUz)
                            } else {
                                selectedHabitTitles.add(template.titleUz)
                            }
                        }
                        .testTag("onboarding_habit_${template.titleUz}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) NuviaPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(if (isSelected) 2.dp else 1.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, NuviaPrimary) else null
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NuviaPrimary else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(
                                text = "${template.timeStart} • ${template.reminderTime}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Get Started Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        val goal = dailyGoal.toIntOrNull() ?: 5
                        viewModel.completeOnboarding(name, selectedHabitTitles.toList(), goal)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_get_started"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = Localization.get("get_started", lang),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
