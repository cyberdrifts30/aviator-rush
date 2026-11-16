package com.Qempibo9.Pebfik.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.AccentDeep
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.CloudTint
import com.Qempibo9.Pebfik.core.theme.Danger
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.core.theme.PrimaryBright
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextOnAccent
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.ChipRow
import com.Qempibo9.Pebfik.core.ui.MilesBadge
import com.Qempibo9.Pebfik.core.ui.PrimaryButton
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.util.decimal2
import com.Qempibo9.Pebfik.core.util.groupNumber
import com.Qempibo9.Pebfik.core.util.hundredthsToDecimal
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.StageKind
import com.Qempibo9.Pebfik.presentation.common.stageRequirementRes
import com.Qempibo9.Pebfik.presentation.dialog.PauseDialog
import kotlin.random.Random

@Composable
fun GameScreen(
    onGameOver: (Boolean) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is GameEvent.Finished -> onGameOver(event.secured)
                GameEvent.Abandoned -> onMenu()
            }
        }
    }

    DisposableEffect(viewModel) {
        onDispose { viewModel.pauseFlight() }
    }

    GameContent(
        state = state,
        onPrimaryAction = viewModel::onPrimaryAction,
        onBoost = viewModel::onBoostToggled,
        onAutoSecure = viewModel::onAutoSecureSelected,
        onCrateTap = viewModel::onCrateTapped,
        onPause = viewModel::pauseFlight,
        onResume = viewModel::resumeFlight,
        onAbandon = viewModel::abandonFlight,
        modifier = modifier
    )
}

@Composable
private fun GameContent(
    state: GameUiState,
    onPrimaryAction: () -> Unit,
    onBoost: () -> Unit,
    onAutoSecure: (Int) -> Unit,
    onCrateTap: (Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onAbandon: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.9f,
        overlay = listOf(
            Color(0xFF0A1120).copy(alpha = 0.55f),
            Color.Transparent,
            BgBase.copy(alpha = 0.85f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            GameHeader(state = state, onPause = onPause)
            SkyArea(
                state = state,
                onCrateTap = onCrateTap,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            StatStrip(state = state)
            ControlsPanel(
                state = state,
                onPrimaryAction = onPrimaryAction,
                onBoost = onBoost,
                onAutoSecure = onAutoSecure
            )
        }
    }
    if (state.phase == FlightPhase.PAUSED) {
        PauseDialog(onResume = onResume, onMenu = onAbandon)
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    val targetText = if (state.stageKind == StageKind.REACH_MULTIPLIER) {
        stringResource(R.string.fmt_multiplier, hundredthsToDecimal(state.stageTarget))
    } else {
        state.stageTarget.toString()
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .background(SurfaceHigh.copy(alpha = 0.8f), CircleShape)
                .border(1.dp, Secondary.copy(alpha = 0.24f), CircleShape)
                .clickable(enabled = state.isFlying) { onPause() }
                .alpha(if (state.isFlying) 1f else 0.5f)
        ) {
            Image(
                painter = painterResource(R.drawable.icon_pause),
                contentDescription = stringResource(R.string.action_pause),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.game_flight_number, state.flightNumber),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                text = stringResource(
                    R.string.game_stage_target,
                    state.stageNumber,
                    stringResource(stageRequirementRes(state.stageKind)),
                    targetText
                ),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1
            )
        }
        MilesBadge(miles = state.miles)
    }
}

@Composable
private fun SkyArea(
    state: GameUiState,
    onCrateTap: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = GAME_TRANSITION)
    val scroll by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = GAME_SCROLL
    )
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = GAME_PULSE
    )
    val activeScroll = if (state.reducedMotion) 0.3f else scroll
    val activePulse = if (state.reducedMotion || !state.isFlying) 1f else pulse

    val planeAngle by animateFloatAsState(
        targetValue = when (state.phase) {
            FlightPhase.STALLING -> 34f
            FlightPhase.FLYING -> if (state.boosting) -16f else -4f
            else -> -4f
        },
        animationSpec = tween(durationMillis = 320),
        label = GAME_PLANE
    )
    val planeDrop by animateFloatAsState(
        targetValue = if (state.phase == FlightPhase.STALLING) 120f else 0f,
        animationSpec = tween(durationMillis = 520),
        label = GAME_DROP
    )

    val stars = remember {
        val random = Random(SKY_SEED)
        List(STAR_COUNT) { Triple(random.nextFloat(), random.nextFloat(), 0.8f + random.nextFloat() * 1.6f) }
    }

    val multiplierColor = when {
        state.multiplier >= 8f -> Accent
        state.multiplier >= 3f -> Secondary
        else -> TextPrimary
    }
    val multiplierText = stringResource(R.string.fmt_multiplier, decimal2(state.multiplier))
    val altitudeText = if (state.altitudeUnit == AltitudeUnit.METRES) {
        stringResource(R.string.fmt_altitude, groupNumber(state.altitudeM))
    } else {
        multiplierText
    }

    BoxWithConstraints(modifier = modifier) {
        val areaWidth = maxWidth
        val areaHeight = maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                val y = ((star.second + activeScroll * 0.25f) % 1f) * size.height
                drawCircle(
                    color = Color.White.copy(alpha = 0.3f),
                    radius = star.third,
                    center = Offset(size.width * star.first, y)
                )
            }
            val cloudY = ((state.cloudOffsetA + activeScroll * 0.6f) % 1f) * size.height
            val cloudY2 = ((state.cloudOffsetB + activeScroll * 0.42f) % 1f) * size.height
            drawCircle(
                color = CloudTint.copy(alpha = 0.26f),
                radius = size.width * 0.34f,
                center = Offset(size.width * 0.22f, cloudY)
            )
            drawCircle(
                color = CloudTint.copy(alpha = 0.2f),
                radius = size.width * 0.28f,
                center = Offset(size.width * 0.82f, cloudY2)
            )

            if (state.trail.size > 1) {
                val maxValue = state.trail.max().coerceAtLeast(0.001f)
                val path = Path()
                val fill = Path()
                state.trail.forEachIndexed { index, value ->
                    val x = size.width * (0.12f + 0.76f * index / (state.trail.size - 1).toFloat())
                    val y = size.height * (0.88f - 0.72f * (value / maxValue))
                    if (index == 0) {
                        path.moveTo(x, y)
                        fill.moveTo(x, size.height * 0.88f)
                        fill.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fill.lineTo(x, y)
                    }
                }
                fill.lineTo(size.width * 0.88f, size.height * 0.88f)
                fill.close()
                drawPath(
                    path = fill,
                    brush = Brush.verticalGradient(
                        listOf(Secondary.copy(alpha = 0.14f), Color.Transparent)
                    )
                )
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(listOf(Primary, Secondary)),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            val ribbonHeight = size.height * 0.7f
            val ribbonTop = size.height * 0.15f
            drawRoundRect(
                color = SurfaceHigh,
                topLeft = Offset(14f, ribbonTop),
                size = androidx.compose.ui.geometry.Size(10f, ribbonHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
            )
            val filled = ((state.multiplier - 1f) / 10f).coerceIn(0f, 1f) * ribbonHeight
            if (filled > 0f) {
                drawRoundRect(
                    color = Secondary,
                    topLeft = Offset(14f, ribbonTop + ribbonHeight - filled),
                    size = androidx.compose.ui.geometry.Size(10f, filled),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
                )
            }
        }

        val planeFraction = if (state.trail.size > 1) {
            val maxValue = state.trail.max().coerceAtLeast(0.001f)
            0.88f - 0.72f * (state.trail.last() / maxValue)
        } else {
            0.86f
        }

        Image(
            painter = painterResource(R.drawable.sprite_1),
            contentDescription = stringResource(R.string.game_plane_description),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(96.dp)
                .offset(
                    x = areaWidth * 0.74f,
                    y = areaHeight * planeFraction - 48.dp + planeDrop.dp
                )
                .rotate(planeAngle)
        )

        val crate = state.activeCrate
        if (crate != null) {
            Image(
                painter = painterResource(R.drawable.sprite_2),
                contentDescription = stringResource(R.string.game_crate_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(52.dp)
                    .offset(
                        x = areaWidth * crate.horizontalFraction,
                        y = areaHeight * crate.verticalFraction
                    )
                    .clickable { onCrateTap(crate.id) }
            )
        }

        if (state.phase == FlightPhase.STALLING) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = stringResource(R.string.game_stall_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(150.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Danger.copy(alpha = 0.18f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp)
        ) {
            Text(
                text = multiplierText,
                style = MaterialTheme.typography.displayLarge,
                color = multiplierColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(activePulse)
                    .alpha(if (state.phase == FlightPhase.IDLE) 0.6f else 1f)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = multiplierText
                    }
            )
            Text(
                text = altitudeText,
                style = MaterialTheme.typography.labelSmall,
                color = Secondary,
                textAlign = TextAlign.Center
            )
        }

        if (state.showHint) {
            Text(
                text = stringResource(R.string.game_hint),
                style = MaterialTheme.typography.labelSmall,
                color = Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatStrip(state: GameUiState) {
    val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(SurfaceLow.copy(alpha = 0.86f), shape)
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = stringResource(R.string.stat_fuel),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.stat_fuel),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            ProgressTrack(
                progress = state.fuel / GameConfig.FUEL_FULL,
                height = 6.dp,
                fillColors = listOf(Accent, AccentDeep)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        StatCell(
            labelRes = R.string.stat_streak,
            value = stringResource(R.string.fmt_streak, state.streak),
            dim = state.streak == 0
        )
        Spacer(modifier = Modifier.width(16.dp))
        StatCell(
            labelRes = R.string.stat_crates,
            value = state.crates.toString(),
            dim = state.crates == 0
        )
    }
}

@Composable
private fun StatCell(labelRes: Int, value: String, dim: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary.copy(alpha = if (dim) 0.55f else 1f)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = if (dim) TextSecondary else TextPrimary
        )
    }
}

@Composable
private fun ControlsPanel(
    state: GameUiState,
    onPrimaryAction: () -> Unit,
    onBoost: () -> Unit,
    onAutoSecure: (Int) -> Unit
) {
    val autoLabels = listOf(
        stringResource(R.string.auto_off),
        stringResource(R.string.fmt_auto, AUTO_LOW),
        stringResource(R.string.fmt_auto, AUTO_MID),
        stringResource(R.string.fmt_auto, AUTO_HIGH)
    )
    val boostState = stringResource(
        if (state.boosting) R.string.state_boost_on else R.string.state_boost_off
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(BgBase.copy(alpha = 0.94f))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(
            text = stringResource(R.string.auto_secure_label),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        ChipRow(
            labels = autoLabels,
            selectedIndex = state.autoSecureIndex,
            onSelect = onAutoSecure,
            enabled = state.phase == FlightPhase.IDLE
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                textRes = if (state.isFlying) R.string.action_secure else R.string.action_go,
                onClick = onPrimaryAction,
                enabled = !state.controlsLocked,
                height = 64.dp,
                gradient = if (state.isFlying) {
                    listOf(Accent, AccentDeep)
                } else {
                    listOf(Primary, PrimaryBright)
                },
                contentColor = if (state.isFlying) TextOnAccent else TextPrimary,
                caption = if (state.isFlying) {
                    stringResource(R.string.fmt_multiplier, decimal2(state.multiplier))
                } else {
                    null
                },
                modifier = Modifier.weight(1f)
            )
            SecondaryButton(
                textRes = R.string.action_boost,
                onClick = onBoost,
                enabled = state.boostEnabled,
                height = 64.dp,
                accent = Accent,
                leadingIcon = R.drawable.sprite_3,
                modifier = Modifier
                    .width(132.dp)
                    .semantics { stateDescription = boostState }
            )
        }
    }
}

private const val GAME_TRANSITION = "game-transition"
private const val GAME_SCROLL = "game-scroll"
private const val GAME_PULSE = "game-pulse"
private const val GAME_PLANE = "game-plane"
private const val GAME_DROP = "game-drop"
private const val STAR_COUNT = 60
private const val SKY_SEED = 77131
private const val AUTO_LOW = "2"
private const val AUTO_MID = "5"
private const val AUTO_HIGH = "10"
