package com.Qempibo9.Pebfik.presentation.game

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.StageKind

enum class FlightPhase {
    IDLE,
    FLYING,
    SECURING,
    STALLING,
    PAUSED
}

data class CrateUi(
    val id: Int,
    val horizontalFraction: Float,
    val verticalFraction: Float
)

data class GameUiState(
    val phase: FlightPhase = FlightPhase.IDLE,
    val multiplier: Float = 1f,
    val altitudeM: Int = 0,
    val fuel: Float = GameConfig.FUEL_FULL,
    val boosting: Boolean = false,
    val crates: Int = 0,
    val streak: Int = 0,
    val miles: Int = 0,
    val trail: List<Float> = emptyList(),
    val activeCrate: CrateUi? = null,
    val autoSecureIndex: Int = 0,
    val flightNumber: Int = 1,
    val stageNumber: Int = 1,
    val stageKind: StageKind = StageKind.SECURE_FLIGHTS,
    val stageTarget: Int = 3,
    val stageProgress: Int = 0,
    val showHint: Boolean = true,
    val reducedMotion: Boolean = false,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.MULTIPLIER,
    val cloudOffsetA: Float = 0f,
    val cloudOffsetB: Float = 0.5f,
    val seed: Int = 0
) {
    val isFlying: Boolean get() = phase == FlightPhase.FLYING
    val boostEnabled: Boolean get() = phase == FlightPhase.FLYING && fuel > 0f
    val controlsLocked: Boolean get() = phase == FlightPhase.SECURING || phase == FlightPhase.STALLING
}
