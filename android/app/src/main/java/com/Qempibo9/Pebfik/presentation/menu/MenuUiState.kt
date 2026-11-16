package com.Qempibo9.Pebfik.presentation.menu

import com.Qempibo9.Pebfik.core.config.GameConfig

data class MenuUiState(
    val miles: Int = 0,
    val rankNameRes: Int = 0,
    val rankProgress: Float = 0f,
    val streak: Int = 0,
    val stageNumber: Int = 1,
    val totalStages: Int = GameConfig.TOTAL_STAGES,
    val stagesComplete: Int = 0,
    val flightNumber: Int = 1,
    val firstFlight: Boolean = true,
    val goalsComplete: Int = 0,
    val goalsTotal: Int = GameConfig.DAILY_GOAL_COUNT,
    val goalsPending: Boolean = false,
    val collectionUnlocked: Int = 1,
    val collectionTotal: Int = GameConfig.TOTAL_COLLECTIBLES,
    val rewardsReady: Int = 0,
    val equippedArtIndex: Int = 1,
    val reducedMotion: Boolean = false,
    val onboardingSeen: Boolean = true
)
