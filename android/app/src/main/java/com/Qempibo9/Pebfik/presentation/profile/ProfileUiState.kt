package com.Qempibo9.Pebfik.presentation.profile

data class RecentFlightUi(
    val secured: Boolean,
    val multiplier: Float
)

data class ProfileUiState(
    val miles: Int = 0,
    val rankNameRes: Int = 0,
    val nextRankNameRes: Int = 0,
    val milesToNext: Int = 0,
    val rankProgress: Float = 0f,
    val streak: Int = 0,
    val equippedArtIndex: Int = 1,
    val equippedNameRes: Int = 0,
    val flights: Int = 0,
    val secured: Int = 0,
    val bestMultiplier: Float = 0f,
    val bestStreak: Int = 0,
    val crates: Int = 0,
    val stagesComplete: Int = 0,
    val recent: List<RecentFlightUi> = emptyList()
)
