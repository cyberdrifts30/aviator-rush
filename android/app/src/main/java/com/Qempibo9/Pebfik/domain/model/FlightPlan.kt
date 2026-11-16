package com.Qempibo9.Pebfik.domain.model

data class CrateDrop(
    val id: Int,
    val triggerMultiplier: Float,
    val horizontalFraction: Float,
    val verticalFraction: Float
)

data class FlightPlan(
    val seed: Int,
    val stallMultiplier: Float,
    val crates: List<CrateDrop>,
    val cloudOffsetA: Float,
    val cloudOffsetB: Float
)
