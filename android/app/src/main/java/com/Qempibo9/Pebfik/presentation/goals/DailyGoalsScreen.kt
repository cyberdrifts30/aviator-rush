package com.Qempibo9.Pebfik.presentation.goals

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.AccentDeep
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.Success
import com.Qempibo9.Pebfik.core.theme.TextOnAccent
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.MilesBadge
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.SectionHeader
import com.Qempibo9.Pebfik.core.util.groupNumber
import com.Qempibo9.Pebfik.core.util.hundredthsToDecimal
import com.Qempibo9.Pebfik.presentation.common.spriteRes

@Composable
fun DailyGoalsScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: DailyGoalsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    DailyGoalsContent(
        state = state,
        onClaim = viewModel::onClaim,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun DailyGoalsContent(
    state: DailyGoalsUiState,
    onClaim: (Int) -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.5f,
        overlay = listOf(
            BgBase.copy(alpha = 0.93f),
            SurfaceLow.copy(alpha = 0.7f),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SectionHeader(
                    titleRes = R.string.hub_daily_goals,
                    trailing = stringResource(
                        R.string.goals_reset_in,
                        state.resetHours,
                        state.resetMinutes
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                MilesBadge(miles = state.miles)
            }

            if (state.regenerated) {
                Text(
                    text = stringResource(R.string.goals_regenerated),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (state.loading) {
                    repeat(3) {
                        GlassPanel(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .alpha(0.45f)
                        ) {
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                } else {
                    state.goals.forEach { goal ->
                        GoalCard(
                            goal = goal,
                            onClaim = onClaim,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
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
private fun GoalCard(
    goal: GoalCardUi,
    onClaim: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateText = stringResource(
        when {
            goal.claimed -> R.string.state_goal_claimed
            goal.complete -> R.string.state_goal_ready
            else -> R.string.state_goal_active
        }
    )
    val progressText = if (goal.isMultiplier) {
        stringResource(
            R.string.fmt_progress_text,
            hundredthsToDecimal(goal.progress),
            hundredthsToDecimal(goal.target)
        )
    } else {
        stringResource(R.string.fmt_fraction, goal.progress, goal.target)
    }

    GlassPanel(
        modifier = modifier.fillMaxWidth().alpha(if (goal.claimed) 0.62f else 1f),
        cornerRadius = 20.dp,
        borderColor = if (goal.complete && !goal.claimed) {
            Accent.copy(alpha = 0.6f)
        } else {
            Secondary.copy(alpha = 0.18f)
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .semantics { stateDescription = stateText }
        ) {
            Image(
                painter = painterResource(spriteRes(goal.artIndex)),
                contentDescription = stringResource(goal.titleRes),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(goal.titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = progressText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProgressTrack(
                    progress = goal.fraction,
                    height = 6.dp,
                    fillColors = if (goal.complete) {
                        listOf(Accent, AccentDeep)
                    } else {
                        listOf(Secondary, Secondary)
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.goals_reward, groupNumber(goal.rewardMiles)),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (goal.claimed) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.action_claimed),
                        style = MaterialTheme.typography.labelSmall,
                        color = Success
                    )
                }
            } else {
                Button(
                    onClick = { onClaim(goal.goalId) },
                    enabled = goal.complete,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = TextOnAccent,
                        disabledContainerColor = SurfaceHigh,
                        disabledContentColor = TextSecondary
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = stringResource(R.string.action_claim),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

