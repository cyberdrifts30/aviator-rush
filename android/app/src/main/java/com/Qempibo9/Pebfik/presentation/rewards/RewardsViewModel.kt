package com.Qempibo9.Pebfik.presentation.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.usecase.ClaimMilestoneUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RewardsViewModel(
    private val progressRepository: ProgressRepository,
    private val catalog: GameCatalog,
    private val claimMilestone: ClaimMilestoneUseCase
) : ViewModel() {

    private val ready = MutableStateFlow(false)

    init {
        viewModelScope.launch { ready.value = true }
    }

    val state: StateFlow<RewardsUiState> = combine(
        progressRepository.state,
        ready
    ) { progress, isReady -> map(progress, isReady) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(progressRepository.state.value, false)
        )

    fun onClaim(index: Int) {
        progressRepository.update { claimMilestone(it, index) }
    }

    private fun map(progress: ProgressState, isReady: Boolean): RewardsUiState {
        val rows = catalog.milestones.map { milestone ->
            val claimed = progress.claimedMilestones.contains(milestone.index)
            val reached = progress.miles >= milestone.requiredMiles
            MilestoneRowUi(
                index = milestone.index,
                requiredMiles = milestone.requiredMiles,
                contentsRes = milestone.contentsRes,
                bonusMiles = milestone.bonusMiles,
                fraction = if (milestone.requiredMiles <= 0) {
                    1f
                } else {
                    (progress.miles.toFloat() / milestone.requiredMiles).coerceIn(0f, 1f)
                },
                remaining = (milestone.requiredMiles - progress.miles).coerceAtLeast(0),
                claimed = claimed,
                claimable = reached && !claimed
            )
        }
        return RewardsUiState(
            loading = !isReady,
            rows = rows,
            totalMiles = progress.miles
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
