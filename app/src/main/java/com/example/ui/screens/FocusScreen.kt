package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Localization
import com.example.ui.components.CircularProgressRing
import com.example.ui.theme.NuviaAccent
import com.example.ui.theme.NuviaGold
import com.example.ui.theme.NuviaPrimary
import com.example.ui.theme.NuviaPrimaryDark
import com.example.viewmodel.NuviaScreen
import com.example.viewmodel.NuviaUiState
import com.example.viewmodel.NuviaViewModel

@Composable
fun FocusScreen(
    uiState: NuviaUiState,
    viewModel: NuviaViewModel
) {
    val lang = uiState.language
    val totalSec = uiState.focusTotalSeconds.coerceAtLeast(1)
    val remainingSec = uiState.focusRemainingSeconds
    val isRunning = uiState.isFocusRunning
    val isBreak = uiState.isFocusBreak

    val minutes = remainingSec / 60
    val seconds = remainingSec % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val progressPercent = (((totalSec - remainingSec).toFloat() / totalSec) * 100).toInt()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("focus_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(NuviaScreen.HOME) },
                    modifier = Modifier.testTag("btn_focus_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = Localization.get("focus_title", lang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isBreak) Localization.get("focus_break", lang) else Localization.get("focus_work", lang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Presets Selector (25/5 min vs 50/10 min)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                val is25 = totalSec == 25 * 60 && !isBreak
                val is50 = totalSec == 50 * 60 && !isBreak
                val is5Break = totalSec == 5 * 60 && isBreak

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (is25) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setFocusPreset(25, isBreak = false) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "25 daqiqa",
                        color = if (is25) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (is50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setFocusPreset(50, isBreak = false) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "50 daqiqa",
                        color = if (is50) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (is5Break) NuviaAccent else MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { viewModel.setFocusPreset(5, isBreak = true) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "5 min tanaffus",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            // Big Timer Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(260.dp)
            ) {
                CircularProgressRing(
                    progressPercent = progressPercent,
                    size = 250.dp,
                    strokeWidth = 16.dp,
                    primaryColor = if (isBreak) NuviaAccent else NuviaPrimaryDark,
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = if (isBreak) NuviaAccent else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.headlineLarge,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isRunning) "Chuqur diqqat rejimi..." else "Tayyormisiz?",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Inspiring Ambient Quote
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "“Bir vaqtning o'zida faqat bir vazifaga butun e'tiborni qaratish — buyuk natijalarning siridir.”",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(14.dp),
                    fontSize = 12.sp
                )
            }

            // Controls: Start / Pause / Reset
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                IconButton(
                    onClick = { viewModel.resetFocusTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("btn_focus_reset")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Play / Pause FAB
                Button(
                    onClick = {
                        if (isRunning) {
                            viewModel.pauseFocusTimer()
                        } else {
                            viewModel.startFocusTimer()
                        }
                    },
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .testTag("btn_focus_toggle"),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBreak) NuviaAccent else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = if (isBreak) Color.Black else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
