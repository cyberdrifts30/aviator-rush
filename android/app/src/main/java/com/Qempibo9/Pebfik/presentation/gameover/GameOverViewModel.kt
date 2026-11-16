package com.Qempibo9.Pebfik.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.min

class GameOverViewModel(
    progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    private val catalog: GameCatalog
) : ViewModel() {

    val state: StateFlow<GameOverUiState> = combine(
        progressRepository.state,
        settingsRepository.settings
    ) { progress, settings -> map(progress, settings) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(progressRepository.state.value, settingsRepository.settings.value)
        )

    private fun map(progress: ProgressState, settings: AppSettings): GameOverUiState {
        val flight = progress.lastFlight
        val stage = catalog.stages.getOrNull(progress.stageIndex) ?: catalog.stages.last()
        val unlockedName = flight?.unlockedCollectible
            ?.takeIf { it >= 0 }
            ?.let { id -> catalog.collectibles.firstOrNull { it.id == id }?.nameRes }
            ?: 0
        return GameOverUiState(
            secured = flight?.secured ?: false,
            ceilingReached = flight?.ceilingReached ?: false,
            miles = flight?.miles ?: 0,
            peakMultiplier = flight?.multiplier ?: 1f,
            altitudeM = flight?.altitudeM ?: 0,
            crates = flight?.crates ?: 0,
            streak = progress.streak,
            stageNumber = min(progress.stageIndex + 1, GameConfig.TOTAL_STAGES),
            stageKind = stage.kind,
            stageProgress = progress.stageProgress,
            stageTarget = stage.target,
            stageCompleted = flight?.stageCompleted ?: false,
            unlockedItemNameRes = unlockedName,
            altitudeUnit = settings.altitudeUnit,
            reducedMotion = settings.reducedMotion,
            totalMiles = progress.miles
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
