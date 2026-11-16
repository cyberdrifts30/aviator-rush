package com.Qempibo9.Pebfik.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import com.Qempibo9.Pebfik.domain.usecase.RefreshDailyGoalsUseCase
import com.Qempibo9.Pebfik.domain.usecase.ResolveRankUseCase
import com.Qempibo9.Pebfik.presentation.common.rankNameRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    private val catalog: GameCatalog,
    private val resolveRank: ResolveRankUseCase,
    refreshDailyGoals: RefreshDailyGoalsUseCase
) : ViewModel() {

    init {
        val today = progressRepository.todayEpochDay()
        progressRepository.update { refreshDailyGoals(it, today) }
    }

    val state: StateFlow<MenuUiState> = combine(
        progressRepository.state,
        settingsRepository.settings
    ) { progress, settings -> map(progress, settings) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(progressRepository.state.value, settingsRepository.settings.value)
        )

    private fun map(progress: ProgressState, settings: AppSettings): MenuUiState {
        val goalsComplete = progress.goals.entries.count { entry ->
            val goal = catalog.goalPool.firstOrNull { it.id == entry.goalId }
            goal != null && entry.progress >= goal.target
        }
        val goalsPending = progress.goals.entries.any { entry ->
            val goal = catalog.goalPool.firstOrNull { it.id == entry.goalId }
            goal != null && entry.progress >= goal.target && !entry.claimed
        }
        val rewardsReady = catalog.milestones.count {
            progress.miles >= it.requiredMiles && !progress.claimedMilestones.contains(it.index)
        }
        val equipped = catalog.collectibles.firstOrNull { it.id == progress.equippedLivery }
        return MenuUiState(
            miles = progress.miles,
            rankNameRes = rankNameRes(resolveRank(progress.miles)),
            rankProgress = resolveRank.progressToNext(progress.miles),
            streak = progress.streak,
            stageNumber = (progress.stageIndex + 1).coerceAtMost(GameConfig.TOTAL_STAGES),
            stagesComplete = progress.stageIndex,
            flightNumber = progress.flights + 1,
            firstFlight = progress.flights == 0,
            goalsComplete = goalsComplete,
            goalsPending = goalsPending,
            collectionUnlocked = progress.unlocked.size,
            rewardsReady = rewardsReady,
            equippedArtIndex = equipped?.artIndex ?: 1,
            reducedMotion = settings.reducedMotion,
            onboardingSeen = progress.onboardingSeen
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
