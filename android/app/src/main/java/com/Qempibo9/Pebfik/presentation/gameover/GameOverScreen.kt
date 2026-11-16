package com.Qempibo9.Pebfik.presentation.gameover

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.AccentDeep
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.Danger
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.Success
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.PrimaryButton
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.StatCardData
import com.Qempibo9.Pebfik.core.ui.StatRow
import com.Qempibo9.Pebfik.core.util.decimal2
import com.Qempibo9.Pebfik.core.util.groupNumber
import com.Qempibo9.Pebfik.core.util.hundredthsToDecimal
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.StageKind
import com.Qempibo9.Pebfik.presentation.common.spriteRes
import com.Qempibo9.Pebfik.presentation.common.stageRequirementRes

@Composable
fun GameOverScreen(
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: GameOverViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    GameOverContent(
        state = state,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overlay = if (state.secured) {
        listOf(
            Color(0xFF0C2018).copy(alpha = 0.92f),
            BgBase.copy(alpha = 0.9f),
            BgBase.copy(alpha = 0.96f)
        )
    } else {
        listOf(
            Color(0xFF20101A).copy(alpha = 0.92f),
            BgBase.copy(alpha = 0.9f),
            BgBase.copy(alpha = 0.96f)
        )
    }
    val titleColor = if (state.secured) Success else Danger
    val heroArt = if (state.secured) spriteRes(4) else spriteRes(5)

    val cards = buildList {
        if (state.miles > 0) {
            add(
                StatCardData(
                    labelRes = R.string.stat_miles,
                    value = stringResource(R.string.fmt_plus, groupNumber(state.miles)),
                    accent = Accent
                )
            )
        }
        add(
            StatCardData(
                labelRes = R.string.stat_peak,
                value = if (state.altitudeUnit == AltitudeUnit.METRES) {
                    stringResource(R.string.fmt_altitude, groupNumber(state.altitudeM))
                } else {
                    stringResource(R.string.fmt_multiplier, decimal2(state.peakMultiplier))
                },
                accent = Secondary
            )
        )
        if (state.crates > 0) {
            add(
                StatCardData(
                    labelRes = R.string.stat_crates,
                    value = state.crates.toString(),
                    accent = Success
                )
            )
        }
    }

    ScreenBackground(imageAlpha = 0.45f, overlay = overlay, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(
                    if (state.secured) R.string.result_secured else R.string.result_stalled
                ),
                style = MaterialTheme.typography.displaySmall,
                color = titleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(modifier = Modifier.height(14.dp))
            Spacer(
                modifier = Modifier
                    .width(110.dp)
                    .height(2.dp)
                    .background(titleColor.copy(alpha = 0.7f), RoundedCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.height(10.dp))
            if (state.ceilingReached) {
                Text(
                    text = stringResource(R.string.result_ceiling),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            ResultHero(
                artRes = heroArt,
                secured = state.secured,
                reducedMotion = state.reducedMotion,
                modifier = Modifier.weight(1f)
            )

            if (cards.size >= 2) {
                StatRow(cards = cards)
            } else {
                Text(
                    text = stringResource(R.string.result_no_miles),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            StageStrip(state = state)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryButton(
                textRes = R.string.fly_again,
                onClick = onPlayAgain,
                height = 64.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }
}

@Composable
private fun ResultHero(
    artRes: Int,
    secured: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = RESULT_TRANSITION)
    val shimmer by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = RESULT_SHIMMER
    )
    val alphaValue = when {
        reducedMotion -> if (secured) 1f else 0.7f
        secured -> shimmer
        else -> 0.7f
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(artRes),
            contentDescription = stringResource(
                if (secured) R.string.result_secured else R.string.result_stalled
            ),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(120.dp).alpha(alphaValue)
        )
    }
}

@Composable
private fun StageStrip(state: GameOverUiState) {
    val targetText = if (state.stageKind == StageKind.REACH_MULTIPLIER) {
        stringResource(R.string.fmt_multiplier, hundredthsToDecimal(state.stageTarget))
    } else {
        state.stageTarget.toString()
    }
    val progressText = if (state.stageKind == StageKind.REACH_MULTIPLIER) {
        stringResource(R.string.fmt_multiplier, hundredthsToDecimal(state.stageProgress))
    } else {
        state.stageProgress.toString()
    }
    GlassPanel(modifier = Modifier.fillMaxWidth(), cornerRadius = 18.dp) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            if (state.stageCompleted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.result_stage_complete, state.stageNumber),
                        style = MaterialTheme.typography.titleMedium,
                        color = Accent
                    )
                }
                if (state.unlockedItemNameRes != 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.result_unlocked,
                            stringResource(state.unlockedItemNameRes)
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentDeep
                    )
                }
            } else {
                Text(
                    text = stringResource(
                        R.string.result_stage_progress,
                        state.stageNumber,
                        stringResource(stageRequirementRes(state.stageKind)),
                        progressText,
                        targetText
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProgressTrack(
                    progress = if (state.stageTarget <= 0) {
                        0f
                    } else {
                        state.stageProgress.toFloat() / state.stageTarget
                    },
                    height = 6.dp,
                    fillColors = listOf(Secondary, Accent)
                )
            }
        }
    }
}

private const val RESULT_TRANSITION = "result-transition"
private const val RESULT_SHIMMER = "result-shimmer"
