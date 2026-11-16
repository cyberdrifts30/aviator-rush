package com.Qempibo9.Pebfik.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProgressMapViewModel(
    private val progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    private val catalog: GameCatalog
) : ViewModel() {

    private val selection = MutableStateFlow(progressRepository.state.value.stageIndex)

    val state: StateFlow<ProgressMapUiState> = combine(
        progressRepository.state,
        settingsRepository.settings,
        selection
    ) { progress, settings, selected -> map(progress, settings, selected) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(
                progressRepository.state.value,
                settingsRepository.settings.value,
                selection.value
            )
        )

    fun onNodeSelected(index: Int) {
        if (index < 0 || index >= catalog.stages.size) return
        selection.value = index
    }

    private fun map(
        progress: ProgressState,
        settings: AppSettings,
        selected: Int
    ): ProgressMapUiState {
        val nodes = catalog.stages.map { stage ->
            val status = when {
                stage.index < progress.stageIndex -> NodeStatus.COMPLETED
                stage.index == progress.stageIndex -> NodeStatus.CURRENT
                else -> NodeStatus.LOCKED
            }
            val item = catalog.collectibles.firstOrNull { it.id == stage.collectibleId }
            val rewardLabel = when (item?.kind) {
                ItemKind.LIVERY -> R.string.reward_livery
                ItemKind.BADGE -> R.string.reward_badge
                else -> R.string.reward_miles
            }
            StageNodeUi(
                index = stage.index,
                nameRes = stage.nameRes,
                kind = stage.kind,
                target = stage.target,
                progress = if (stage.index < progress.stageIndex) {
                    stage.target
                } else if (stage.index == progress.stageIndex) {
                    progress.stageProgress
                } else {
                    0
                },
                rewardMiles = stage.rewardMiles,
                rewardLabelRes = rewardLabel,
                status = status
            )
        }
        return ProgressMapUiState(
            nodes = nodes,
            selectedIndex = selected.coerceIn(0, nodes.lastIndex.coerceAtLeast(0)),
            stagesComplete = progress.stageIndex,
            trackComplete = progress.stageIndex >= GameConfig.TOTAL_STAGES,
            reducedMotion = settings.reducedMotion
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
