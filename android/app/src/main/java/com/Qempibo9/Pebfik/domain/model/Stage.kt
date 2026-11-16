package com.Qempibo9.Pebfik.domain.model

enum class StageKind {
    SECURE_FLIGHTS,
    REACH_MULTIPLIER,
    COLLECT_CRATES,
    SECURE_STREAK
}

data class Stage(
    val index: Int,
    val nameRes: Int,
    val kind: StageKind,
    val target: Int,
    val rewardMiles: Int,
    val collectibleId: Int
)
