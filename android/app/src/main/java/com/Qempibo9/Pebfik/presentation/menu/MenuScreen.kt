package com.Qempibo9.Pebfik.presentation.menu

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.PrimaryBright
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.HubTile
import com.Qempibo9.Pebfik.core.ui.MilesBadge
import com.Qempibo9.Pebfik.core.ui.PrimaryButton
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.presentation.common.spriteRes

@Composable
fun MenuScreen(
    onPlay: () -> Unit,
    onProgressMap: () -> Unit,
    onDailyGoals: () -> Unit,
    onCollection: () -> Unit,
    onRewards: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MenuViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    MenuContent(
        state = state,
        onPlay = onPlay,
        onProgressMap = onProgressMap,
        onDailyGoals = onDailyGoals,
        onCollection = onCollection,
        onRewards = onRewards,
        onProfile = onProfile,
        onSettings = onSettings,
        onHowToPlay = onHowToPlay,
        modifier = modifier
    )
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    onPlay: () -> Unit,
    onProgressMap: () -> Unit,
    onDailyGoals: () -> Unit,
    onCollection: () -> Unit,
    onRewards: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    onHowToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.85f,
        overlay = listOf(
            BgBase.copy(alpha = 0.92f),
            SurfaceLow.copy(alpha = 0.7f),
            BgBase.copy(alpha = 0.96f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuHeader(miles = state.miles)
            HeroPanel(state = state)
            PlayTile(state = state, onPlay = onPlay)
            BentoGrid(
                state = state,
                onProgressMap = onProgressMap,
                onDailyGoals = onDailyGoals,
                onCollection = onCollection,
                onRewards = onRewards,
                onProfile = onProfile,
                onSettings = onSettings,
                modifier = Modifier.weight(1f)
            )
            HubTile(
                labelRes = R.string.hub_how_to_play,
                onClick = onHowToPlay,
                iconRes = R.drawable.ic_help,
                pending = !state.onboardingSeen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Composable
private fun MenuHeader(miles: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = stringResource(R.string.app_title),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.app_title),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.weight(1f))
        MilesBadge(miles = miles)
    }
}

@Composable
private fun HeroPanel(state: MenuUiState) {
    val transition = rememberInfiniteTransition(label = MENU_TRANSITION)
    val float by transition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = MENU_FLOAT
    )
    val offsetY = if (state.reducedMotion) 0f else float

    GlassPanel(modifier = Modifier.fillMaxWidth().height(104.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            Image(
                painter = painterResource(spriteRes(state.equippedArtIndex)),
                contentDescription = stringResource(R.string.menu_hero_description),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(84.dp).offset(y = offsetY.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(state.rankNameRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.menu_streak_stage,
                        state.streak,
                        state.stageNumber,
                        state.totalStages
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary
                )
                Spacer(modifier = Modifier.height(10.dp))
                ProgressTrack(
                    progress = state.rankProgress,
                    height = 4.dp,
                    fillColors = listOf(Accent, PrimaryBright)
                )
            }
        }
    }
}

@Composable
private fun PlayTile(state: MenuUiState, onPlay: () -> Unit) {
    PrimaryButton(
        textRes = R.string.play,
        onClick = onPlay,
        height = 112.dp,
        cornerRadius = 24.dp,
        gradient = listOf(Primary, PrimaryBright),
        leadingIcon = spriteRes(state.equippedArtIndex),
        caption = null,
        captionRes = if (state.firstFlight) R.string.menu_first_flight else null,
        textStyle = MaterialTheme.typography.displaySmall
    )
}

@Composable
private fun BentoGrid(
    state: MenuUiState,
    onProgressMap: () -> Unit,
    onDailyGoals: () -> Unit,
    onCollection: () -> Unit,
    onRewards: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rewardsLabel = stringResource(R.string.state_rewards_ready, state.rewardsReady)
    val goalsLabel = stringResource(
        R.string.state_goals_ready,
        state.goalsComplete,
        state.goalsTotal
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            HubTile(
                labelRes = R.string.hub_progress_map,
                onClick = onProgressMap,
                iconRes = R.drawable.ic_flag,
                value = stringResource(
                    R.string.fmt_fraction,
                    state.stagesComplete,
                    state.totalStages
                ),
                modifier = Modifier.weight(1f).fillMaxSize()
            )
            HubTile(
                labelRes = R.string.hub_daily_goals,
                onClick = onDailyGoals,
                iconRes = R.drawable.ic_target,
                value = stringResource(
                    R.string.fmt_fraction,
                    state.goalsComplete,
                    state.goalsTotal
                ),
                pending = state.goalsPending,
                stateLabel = goalsLabel,
                modifier = Modifier.weight(1f).fillMaxSize()
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            HubTile(
                labelRes = R.string.hub_collection,
                onClick = onCollection,
                spriteRes = R.drawable.sprite_2,
                value = stringResource(
                    R.string.fmt_fraction,
                    state.collectionUnlocked,
                    state.collectionTotal
                ),
                modifier = Modifier.weight(1f).fillMaxSize()
            )
            HubTile(
                labelRes = R.string.hub_rewards,
                onClick = onRewards,
                spriteRes = R.drawable.sprite_4,
                value = if (state.rewardsReady > 0) {
                    stringResource(R.string.fmt_ready, state.rewardsReady)
                } else {
                    null
                },
                pending = state.rewardsReady > 0,
                stateLabel = rewardsLabel,
                modifier = Modifier.weight(1f).fillMaxSize()
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            HubTile(
                labelRes = R.string.hub_profile,
                onClick = onProfile,
                iconRes = R.drawable.ic_badge,
                value = stringResource(state.rankNameRes),
                modifier = Modifier.weight(1f).fillMaxSize()
            )
            HubTile(
                labelRes = R.string.hub_settings,
                onClick = onSettings,
                iconRes = R.drawable.ic_sliders,
                modifier = Modifier.weight(1f).fillMaxSize()
            )
        }
    }
}

private const val MENU_TRANSITION = "menu-transition"
private const val MENU_FLOAT = "menu-float"
