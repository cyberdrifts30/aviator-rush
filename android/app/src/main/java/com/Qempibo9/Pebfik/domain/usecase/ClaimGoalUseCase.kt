package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog

class ClaimGoalUseCase(private val catalog: GameCatalog) {

    operator fun invoke(state: ProgressState, goalId: Int): ProgressState {
        val goal = catalog.goalPool.firstOrNull { it.id == goalId } ?: return state
        val entry = state.goals.entries.firstOrNull { it.goalId == goalId } ?: return state
        if (entry.claimed || entry.progress < goal.target) return state
        val entries = state.goals.entries.map {
            if (it.goalId == goalId) it.copy(claimed = true) else it
        }
        return state.copy(
            miles = state.miles + goal.rewardMiles,
            goals = state.goals.copy(entries = entries)
        )
    }
}
