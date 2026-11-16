package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog

class ClaimMilestoneUseCase(private val catalog: GameCatalog) {

    operator fun invoke(state: ProgressState, index: Int): ProgressState {
        val milestone = catalog.milestones.firstOrNull { it.index == index } ?: return state
        if (state.claimedMilestones.contains(index)) return state
        if (state.miles < milestone.requiredMiles) return state
        val unlocked = if (milestone.collectibleId >= 0) {
            state.unlocked + milestone.collectibleId
        } else {
            state.unlocked
        }
        return state.copy(
            miles = state.miles + milestone.bonusMiles,
            unlocked = unlocked,
            claimedMilestones = state.claimedMilestones + index
        )
    }
}
