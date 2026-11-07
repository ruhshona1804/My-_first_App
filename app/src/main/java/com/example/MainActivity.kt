package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.NuviaBottomBar
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.FocusScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.JournalScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.NuviaScreen
import com.example.viewmodel.NuviaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NuviaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current
            val snackbarHostState = remember { SnackbarHostState() }

            // Notification permission request for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { _ -> }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            // Toast / Snackbar listener
            LaunchedEffect(uiState.toastMessage) {
                uiState.toastMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            // Back handler navigation: navigate back to Home if not on Home
            BackHandler(enabled = uiState.currentScreen != NuviaScreen.HOME && uiState.currentScreen != NuviaScreen.ONBOARDING) {
                viewModel.navigateTo(NuviaScreen.HOME)
            }

            MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
                if (uiState.currentScreen == NuviaScreen.ONBOARDING) {
                    OnboardingScreen(uiState = uiState, viewModel = viewModel)
                } else {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NuviaBottomBar(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { viewModel.navigateTo(it) },
                                lang = uiState.language
                            )
                        },
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentScreen,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "screen_transition"
                            ) { screen ->
                                when (screen) {
                                    NuviaScreen.HOME -> HomeScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.HABITS -> HabitsScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.SCHEDULE -> ScheduleScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.STATISTICS -> StatisticsScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.PROFILE -> ProfileScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.JOURNAL -> JournalScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.FOCUS -> FocusScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.CALENDAR -> CalendarScreen(uiState = uiState, viewModel = viewModel)
                                    NuviaScreen.ONBOARDING -> OnboardingScreen(uiState = uiState, viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
