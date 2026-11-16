package com.Qempibo9.Pebfik.domain.model

enum class GoalKind {
    SECURE_FLIGHTS,
    REACH_MULTIPLIER,
    COLLECT_CRATES,
    BOOST_TIMES,
    SECURE_STREAK,
    COMPLETE_STAGE
}

data class DailyGoal(
    val id: Int,
    val titleRes: Int,
    val kind: GoalKind,
    val target: Int,
    val rewardMiles: Int,
    val artIndex: Int
)

data class GoalProgress(
    val goalId: Int,
    val progress: Int,
    val claimed: Boolean
)

data class DailyGoalsState(
    val epochDay: Long,
    val entries: List<GoalProgress>
) {
    companion object {
        val EMPTY = DailyGoalsState(-1L, emptyList())
    }
}
