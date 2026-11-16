package com.Qempibo9.Pebfik.presentation.settings

import com.Qempibo9.Pebfik.domain.model.AltitudeUnit

data class SettingsUiState(
    val reducedMotion: Boolean = false,
    val autoSecureIndex: Int = 0,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.MULTIPLIER,
    val confirmReset: Boolean = false
)
