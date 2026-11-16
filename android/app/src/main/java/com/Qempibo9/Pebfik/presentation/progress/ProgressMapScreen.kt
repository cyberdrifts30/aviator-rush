package com.Qempibo9.Pebfik.presentation.progress

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceLocked
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextMuted
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.EmptyState
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.SectionHeader
import com.Qempibo9.Pebfik.core.util.groupNumber
import com.Qempibo9.Pebfik.core.util.hundredthsToDecimal
import com.Qempibo9.Pebfik.domain.model.StageKind
import com.Qempibo9.Pebfik.presentation.common.stageRequirementRes

@Composable
fun ProgressMapScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ProgressMapViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProgressMapContent(
        state = state,
        onNodeSelected = viewModel::onNodeSelected,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun ProgressMapContent(
    state: ProgressMapUiState,
    onNodeSelected: (Int) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.5f,
        overlay = listOf(
            BgBase.copy(alpha = 0.93f),
            SurfaceLow.copy(alpha = 0.72f),
            BgBase.copy(alpha = 0.96f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionHeader(
                titleRes = R.string.hub_progress_map,
                trailing = stringResource(
                    R.string.progress_stage_count,
                    state.stagesComplete,
                    state.totalStages
                )
            )
            NodeGrid(
                state = state,
                onNodeSelected = onNodeSelected,
                modifier = Modifier.weight(1f)
            )
            if (state.trackComplete) {
                GlassPanel(modifier = Modifier.fillMaxWidth()) {
                    EmptyState(
                        artRes = R.drawable.sprite_4,
                        titleRes = R.string.progress_track_complete,
                        messageRes = R.string.progress_track_complete_body,
                        artSize = 72
                    )
                }
            } else {
                DetailCard(node = state.selected)
            }
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }
}

@Composable
private fun NodeGrid(
    state: ProgressMapUiState,
    onNodeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 3
            val rows = 4
            val cellW = size.width / columns
            val cellH = size.height / rows
            val points = (0 until rows).flatMap { row ->
                val order = if (row % 2 == 0) (0 until columns) else (columns - 1 downTo 0)
                order.map { column ->
                    Offset(cellW * (column + 0.5f), cellH * (row + 0.5f))
                }
            }
            val effect = PathEffect.dashPathEffect(floatArrayOf(6f, 10f), 0f)
            points.zipWithNext { from, to ->
                drawLine(
                    color = Secondary.copy(alpha = 0.4f),
                    start = from,
                    end = to,
                    strokeWidth = 2f,
                    pathEffect = effect
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            (0 until 4).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    (0 until 3).forEach { column ->
                        val index = row * 3 + column
                        val node = state.nodes.getOrNull(index)
                        if (node != null) {
                            StageNode(
                                node = node,
                                selected = index == state.selectedIndex,
                                reducedMotion = state.reducedMotion,
                                onClick = { onNodeSelected(index) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StageNode(
    node: StageNodeUi,
    selected: Boolean,
    reducedMotion: Boolean,
    onClick: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = MAP_TRANSITION)
    val pulse by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = MAP_PULSE
    )
    val ringAlpha = when {
        node.status != NodeStatus.CURRENT -> 1f
        reducedMotion -> 0.9f
        else -> pulse
    }
    val fill = when (node.status) {
        NodeStatus.COMPLETED -> Primary
        NodeStatus.CURRENT -> SurfaceLow
        NodeStatus.LOCKED -> SurfaceLocked.copy(alpha = 0.7f)
    }
    val borderColor = when (node.status) {
        NodeStatus.COMPLETED -> Accent
        NodeStatus.CURRENT -> Accent.copy(alpha = ringAlpha)
        NodeStatus.LOCKED -> Secondary.copy(alpha = 0.12f)
    }
    val stateText = stringResource(
        when (node.status) {
            NodeStatus.COMPLETED -> R.string.state_stage_completed
            NodeStatus.CURRENT -> R.string.state_stage_current
            NodeStatus.LOCKED -> R.string.state_stage_locked
        }
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(76.dp)
            .background(fill, CircleShape)
            .border(if (selected) 3.dp else 2.dp, borderColor, CircleShape)
            .clickable { onClick() }
            .semantics { stateDescription = stateText }
    ) {
        when (node.status) {
            NodeStatus.COMPLETED -> Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(22.dp)
            )
            NodeStatus.CURRENT -> Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(34.dp)
            )
            NodeStatus.LOCKED -> Icon(
                painter = painterResource(R.drawable.ic_lock),
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = (node.index + 1).toString(),
            style = MaterialTheme.typography.labelSmall,
            color = if (node.status == NodeStatus.LOCKED) TextMuted else TextPrimary,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun DetailCard(node: StageNodeUi?) {
    if (node == null) return
    val targetText = if (node.kind == StageKind.REACH_MULTIPLIER) {
        stringResource(R.string.fmt_multiplier, hundredthsToDecimal(node.target))
    } else {
        node.target.toString()
    }
    val progressText = if (node.kind == StageKind.REACH_MULTIPLIER) {
        stringResource(R.string.fmt_multiplier, hundredthsToDecimal(node.progress))
    } else {
        node.progress.toString()
    }
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 20.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(node.nameRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = stringResource(node.rewardLabelRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(
                    R.string.progress_requirement,
                    stringResource(stageRequirementRes(node.kind)),
                    progressText,
                    targetText
                ),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(10.dp))
            ProgressTrack(
                progress = if (node.target <= 0) 0f else node.progress.toFloat() / node.target,
                height = 6.dp,
                fillColors = listOf(Secondary, Accent)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.progress_reward, groupNumber(node.rewardMiles)),
                style = MaterialTheme.typography.labelSmall,
                color = Secondary,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private const val MAP_TRANSITION = "map-transition"
private const val MAP_PULSE = "map-pulse"
