package com.Qempibo9.Pebfik.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Onboarding : Screen
    data object Menu : Screen
    data object Game : Screen
    data class GameOver(val secured: Boolean) : Screen
    data object ProgressMap : Screen
    data object DailyGoals : Screen
    data object Collection : Screen
    data object Rewards : Screen
    data object Profile : Screen
    data object Settings : Screen
}
