package com.Qempibo9.Pebfik.domain.model

enum class AltitudeUnit {
    MULTIPLIER,
    METRES
}

data class AppSettings(
    val reducedMotion: Boolean = false,
    val autoSecureIndex: Int = 0,
    val altitudeUnit: AltitudeUnit = AltitudeUnit.MULTIPLIER
)
