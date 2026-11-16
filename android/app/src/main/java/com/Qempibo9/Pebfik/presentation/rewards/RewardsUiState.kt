package com.Qempibo9.Pebfik.presentation.rewards

data class MilestoneRowUi(
    val index: Int,
    val requiredMiles: Int,
    val contentsRes: Int,
    val bonusMiles: Int,
    val fraction: Float,
    val remaining: Int,
    val claimed: Boolean,
    val claimable: Boolean
)

data class RewardsUiState(
    val loading: Boolean = true,
    val rows: List<MilestoneRowUi> = emptyList(),
    val totalMiles: Int = 0
)
