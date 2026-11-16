package com.Qempibo9.Pebfik.presentation.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.domain.model.GoalKind
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.usecase.ClaimGoalUseCase
import com.Qempibo9.Pebfik.domain.usecase.RefreshDailyGoalsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DailyGoalsViewModel(
    private val progressRepository: ProgressRepository,
    private val catalog: GameCatalog,
    refreshDailyGoals: RefreshDailyGoalsUseCase,
    private val claimGoal: ClaimGoalUseCase
) : ViewModel() {

    private val ready = MutableStateFlow(false)
    private val regenerated = MutableStateFlow(false)
    private val resetMinutes = MutableStateFlow(progressRepository.minutesUntilReset())

    init {
        val today = progressRepository.todayEpochDay()
        val before = progressRepository.state.value.goals
        progressRepository.update { refreshDailyGoals(it, today) }
        regenerated.value = before != progressRepository.state.value.goals && before.entries.isNotEmpty()
        viewModelScope.launch {
            resetMinutes.value = progressRepository.minutesUntilReset()
            ready.value = true
        }
    }

    val state: StateFlow<DailyGoalsUiState> = combine(
        progressRepository.state,
        ready,
        regenerated,
        resetMinutes
    ) { progress, isReady, wasRegenerated, minutes ->
        map(progress, isReady, wasRegenerated, minutes)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        map(progressRepository.state.value, false, false, resetMinutes.value)
    )

    fun onClaim(goalId: Int) {
        progressRepository.update { claimGoal(it, goalId) }
    }

    private fun map(
        progress: ProgressState,
        isReady: Boolean,
        wasRegenerated: Boolean,
        minutes: Int
    ): DailyGoalsUiState {
        val cards = progress.goals.entries.mapNotNull { entry ->
            val goal = catalog.goalPool.firstOrNull { it.id == entry.goalId } ?: return@mapNotNull null
            GoalCardUi(
                goalId = goal.id,
                titleRes = goal.titleRes,
                artIndex = goal.artIndex,
                progress = entry.progress,
                target = goal.target,
                rewardMiles = goal.rewardMiles,
                claimed = entry.claimed,
                isMultiplier = goal.kind == GoalKind.REACH_MULTIPLIER
            )
        }
        return DailyGoalsUiState(
            loading = !isReady,
            regenerated = wasRegenerated,
            goals = cards,
            resetHours = minutes / 60,
            resetMinutes = minutes % 60,
            miles = progress.miles
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
