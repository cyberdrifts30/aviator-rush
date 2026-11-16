package com.Qempibo9.Pebfik.domain.model

data class FlightOutcome(
    val secured: Boolean,
    val multiplier: Float,
    val crates: Int,
    val boosts: Int,
    val ceilingReached: Boolean
)
