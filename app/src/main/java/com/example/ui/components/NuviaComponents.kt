package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.HabitEntity
import com.example.data.model.Localization
import com.example.ui.theme.NuviaAccent
import com.example.ui.theme.NuviaBackground
import com.example.ui.theme.NuviaCard
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaPrimaryDark
import com.example.ui.theme.NuviaPrimaryLight
import com.example.ui.theme.NuviaSecondary
import com.example.ui.theme.NuviaTextMuted
import com.example.ui.theme.NuviaTextPrimary
import com.example.ui.theme.NuviaTextSecondary
import com.example.viewmodel.NuviaScreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun NuviaBottomBar(
    currentScreen: NuviaScreen,
    onNavigate: (NuviaScreen) -> Unit,
    lang: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val navItems = listOf(
                Triple(NuviaScreen.HOME, Localization.get("nav_home", lang), Icons.Filled.Home to Icons.Outlined.Home),
                Triple(NuviaScreen.HABITS, Localization.get("nav_habits", lang), Icons.Filled.TrackChanges to Icons.Outlined.TrackChanges),
                Triple(NuviaScreen.SCHEDULE, Localization.get("nav_schedule", lang), Icons.Filled.Schedule to Icons.Outlined.Schedule),
                Triple(NuviaScreen.STATISTICS, Localization.get("nav_statistics", lang), Icons.Filled.QueryStats to Icons.Outlined.QueryStats),
                Triple(NuviaScreen.PROFILE, Localization.get("nav_profile", lang), Icons.Filled.Person to Icons.Outlined.Person)
            )

            navItems.forEach { (screen, title, iconPair) ->
                val isSelected = currentScreen == screen
                val activeIcon = if (isSelected) iconPair.first else iconPair.second

                Column(
                    modifier = Modifier
                        .testTag("nav_item_${screen.name.lowercase()}")
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigate(screen) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) NuviaPrimary.copy(alpha = 0.35f) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activeIcon,
                            contentDescription = title,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CircularProgressRing(
    progressPercent: Int,
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    strokeWidth: Dp = 12.dp,
    primaryColor: Color = NuviaPrimaryDark,
    backgroundColor: Color = NuviaPrimaryLight.copy(alpha = 0.5f),
    centerLabel: String = ""
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercent / 100f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress_animation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            // Background Track
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, NuviaAccent, primaryColor)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$progressPercent%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (centerLabel.isNotBlank()) {
                Text(
                    text = centerLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun HabitCard(
    habit: HabitEntity,
    isCompleted: Boolean,
    onToggleCompletion: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            scale.animateTo(1.15f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy))
        }
    }

    val parsedColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (_: Exception) {
        NuviaPrimary
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) parsedColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 1.dp else 3.dp),
        border = if (isCompleted) androidx.compose.foundation.BorderStroke(1.dp, parsedColor.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Interactive Checkbox Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale.value)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) parsedColor else parsedColor.copy(alpha = 0.2f)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isCompleted) parsedColor else parsedColor.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .clickable { onToggleCompletion() }
                    .testTag("habit_toggle_${habit.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(parsedColor.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Habit Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onEdit() }
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f) else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (habit.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time pill
                    if (habit.timeStart.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${habit.timeStart} - ${habit.timeEnd}",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Reminder indicator
                    if (habit.reminderTime.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NuviaAccent.copy(alpha = 0.3f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = NuviaTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = habit.reminderTime,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = NuviaTextPrimary
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.testTag("habit_options_${habit.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Options",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DateSelectorStrip(
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val dayNameFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val dayNumberFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }

    val daysList = remember {
        val list = mutableListOf<Triple<String, String, String>>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -3)
        for (i in 0..7) {
            val dStr = dateFormat.format(cal.time)
            val dName = dayNameFormat.format(cal.time).replaceFirstChar { it.uppercase() }
            val dNum = dayNumberFormat.format(cal.time)
            list.add(Triple(dStr, dName, dNum))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(daysList) { (dStr, dName, dNum) ->
            val isSelected = dStr == selectedDate
            val isToday = dStr == dateFormat.format(Date())

            Card(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onSelectDate(dStr) }
                    .testTag("date_chip_$dStr"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dNum,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (isToday) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }
    }
}
