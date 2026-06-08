package com.umas97.hiceram.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hic_eram_prefs", Context.MODE_PRIVATE)

    // Indice del colore d'accento (0..5)
    private val _colorIndex = MutableStateFlow(prefs.getInt(KEY_COLOR_INDEX, 0))
    val colorIndex: StateFlow<Int> = _colorIndex.asStateFlow()

    // Modalità tema (0=System, 1=Light, 2=Dark)
    private val _themeMode = MutableStateFlow(
        ThemeMode.values()[prefs.getInt(KEY_THEME_MODE, ThemeMode.SYSTEM.ordinal)]
    )
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setColorIndex(index: Int) {
        prefs.edit().putInt(KEY_COLOR_INDEX, index).apply()
        _colorIndex.value = index
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode.ordinal).apply()
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_COLOR_INDEX = "color_index"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
