package com.Qempibo9.Pebfik.domain.model

data class ProgressState(
    val miles: Int = 0,
    val fuel: Float = 100f,
    val stageIndex: Int = 0,
    val stageProgress: Int = 0,
    val streak: Int = 0,
    val bestStreak: Int = 0,
    val bestMultiplier: Float = 0f,
    val flights: Int = 0,
    val secured: Int = 0,
    val crates: Int = 0,
    val boosts: Int = 0,
    val unlocked: Set<Int> = setOf(0),
    val equippedLivery: Int = 0,
    val claimedMilestones: Set<Int> = emptySet(),
    val recentFlights: List<FlightResult> = emptyList(),
    val goals: DailyGoalsState = DailyGoalsState.EMPTY,
    val onboardingSeen: Boolean = false,
    val lastFlight: FlightResult? = null
)
