package com.Qempibo9.Pebfik.presentation.goals

data class GoalCardUi(
    val goalId: Int,
    val titleRes: Int,
    val artIndex: Int,
    val progress: Int,
    val target: Int,
    val rewardMiles: Int,
    val claimed: Boolean,
    val isMultiplier: Boolean
) {
    val complete: Boolean get() = progress >= target
    val fraction: Float get() = if (target <= 0) 0f else (progress.toFloat() / target).coerceIn(0f, 1f)
}

data class DailyGoalsUiState(
    val loading: Boolean = true,
    val regenerated: Boolean = false,
    val goals: List<GoalCardUi> = emptyList(),
    val resetHours: Int = 0,
    val resetMinutes: Int = 0,
    val miles: Int = 0
)
