package com.Qempibo9.Pebfik.presentation.gameover

import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.StageKind

data class GameOverUiState(
    val secured: Boolean = false,
    val ceilingReached: Boolean = false,
    val miles: Int = 0,
    val peakMultiplier: Float = 1f,
    val altitudeM: Int = 0,
    val crates: Int = 0,
    val streak: Int = 0,
    val stageNumber: Int = 1,
    val stageKind: StageKind = StageKind.SECURE_FLIGHTS,
    val stageProgress: Int = 0,
    val stageTarget: Int = 3,
    val stageCompleted: Boolean = false,
    val unlockedItemNameRes: Int = 0,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.MULTIPLIER,
    val reducedMotion: Boolean = false,
    val totalMiles: Int = 0
)
