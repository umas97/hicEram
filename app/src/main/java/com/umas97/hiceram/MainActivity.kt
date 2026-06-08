package com.umas97.hiceram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.umas97.hiceram.data.AppPreferences
import com.umas97.hiceram.data.ThemeMode
import com.umas97.hiceram.ui.screens.AddScreen
import com.umas97.hiceram.ui.screens.DetailScreen
import com.umas97.hiceram.ui.screens.FeedScreen
import com.umas97.hiceram.ui.screens.SettingsScreen
import com.umas97.hiceram.ui.theme.AccentColors
import com.umas97.hiceram.ui.theme.HicEramTheme
import com.umas97.hiceram.ui.viewmodel.MomentViewModel
import com.umas97.hiceram.ui.viewmodel.MomentViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val appPreferences = remember { AppPreferences(this) }
            val colorIndex by appPreferences.colorIndex.collectAsState()
            val themeMode by appPreferences.themeMode.collectAsState()
            
            val isDarkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            HicEramTheme(
                darkTheme = isDarkTheme,
                accentColor = AccentColors[colorIndex]
            ) {
                val app = application as HicEramApplication
                val viewModel: MomentViewModel = viewModel(
                    factory = MomentViewModelFactory(app.repository)
                )

                // Stato di navigazione
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Feed) }

                // Gestisce il tasto "Indietro" fisico/gestuale del sistema operativo
                if (currentScreen != Screen.Feed) {
                    BackHandler {
                        currentScreen = Screen.Feed
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (val screen = currentScreen) {
                        is Screen.Feed -> {
                            FeedScreen(
                                viewModel = viewModel,
                                onNavigateToAdd = { currentScreen = Screen.Add },
                                onNavigateToDetail = { moment -> currentScreen = Screen.Detail(moment) },
                                onNavigateToSettings = { currentScreen = Screen.Settings }
                            )
                        }
                        is Screen.Add -> {
                            AddScreen(
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = Screen.Feed }
                            )
                        }
                        is Screen.Detail -> {
                            DetailScreen(
                                moment = screen.moment,
                                viewModel = viewModel,
                                onNavigateBack = { currentScreen = Screen.Feed }
                            )
                        }
                        is Screen.Settings -> {
                            SettingsScreen(
                                currentColorIndex = colorIndex,
                                currentThemeMode = themeMode,
                                onColorSelected = { appPreferences.setColorIndex(it) },
                                onThemeModeSelected = { appPreferences.setThemeMode(it) },
                                onNavigateBack = { currentScreen = Screen.Feed }
                            )
                        }
                    }
                }
            }
        }
    }
}
