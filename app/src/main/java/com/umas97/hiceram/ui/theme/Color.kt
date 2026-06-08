package com.umas97.hiceram.ui.theme

import androidx.compose.ui.graphics.Color

// Palette di colori in stile iOS / Material You minimal.
// Prediligiamo un contrasto elevato (Dark Mode nero assoluto e Light Mode bianco assoluto).

// --- LIGHT MODE ---
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF2F2F7) // Grigio chiarissimo di sfondo per le card
val LightSurfaceVariant = Color(0xFFE5E5EA) // Bordi e divisori
val LightOnBackground = Color(0xFF000000)
val LightOnSurface = Color(0xFF1C1C1E)
val LightSecondary = Color(0xFF8E8E93) // Grigio testo secondario

// --- DARK MODE ---
val DarkBackground = Color(0xFF000000) // Nero assoluto (OLED friendly)
val DarkSurface = Color(0xFF1C1C1E) // Grigio scuro per le card
val DarkSurfaceVariant = Color(0xFF2C2C2E) // Bordi e divisori
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFE5E5EA)
val DarkSecondary = Color(0xFF8E8E93) // Grigio testo secondario

// Array di colori accento tra cui scegliere (Blu, Verde, Rosso, Viola, Arancio, Rosa)
val AccentColors = listOf(
    Color(0xFF0A84FF), // iOS Blue (Default)
    Color(0xFF34C759), // iOS Green
    Color(0xFFFF3B30), // iOS Red
    Color(0xFFAF52DE), // iOS Purple
    Color(0xFFFF9500), // iOS Orange
    Color(0xFFFF2D55)  // iOS Pink
)

// Manteniamo ErrorColor per casi specifici
val ErrorColor = Color(0xFFFF453A)
