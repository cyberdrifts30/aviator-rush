package com.Qempibo9.Pebfik.presentation.progress

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.StageKind

enum class NodeStatus {
    COMPLETED,
    CURRENT,
    LOCKED
}

data class StageNodeUi(
    val index: Int,
    val nameRes: Int,
    val kind: StageKind,
    val target: Int,
    val progress: Int,
    val rewardMiles: Int,
    val rewardLabelRes: Int,
    val status: NodeStatus
)

data class ProgressMapUiState(
    val nodes: List<StageNodeUi> = emptyList(),
    val selectedIndex: Int = 0,
    val stagesComplete: Int = 0,
    val totalStages: Int = GameConfig.TOTAL_STAGES,
    val trackComplete: Boolean = false,
    val reducedMotion: Boolean = false
) {
    val selected: StageNodeUi? get() = nodes.getOrNull(selectedIndex)
}
