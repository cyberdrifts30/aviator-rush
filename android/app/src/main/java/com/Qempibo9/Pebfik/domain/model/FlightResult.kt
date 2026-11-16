package com.Qempibo9.Pebfik.domain.model

data class FlightResult(
    val secured: Boolean,
    val multiplier: Float,
    val altitudeM: Int,
    val crates: Int,
    val boosts: Int,
    val miles: Int,
    val streakAfter: Int,
    val stageCompleted: Boolean,
    val unlockedCollectible: Int,
    val ceilingReached: Boolean
) {
    companion object {
        const val NO_COLLECTIBLE = -1
    }
}
