package com.Qempibo9.Pebfik.presentation.rewards

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
import com.Qempibo9.Pebfik.core.ui.ProgressTrack
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.SectionHeader
import com.Qempibo9.Pebfik.core.util.groupNumber

@Composable
fun RewardsScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: RewardsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    RewardsContent(
        state = state,
        onClaim = viewModel::onClaim,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun RewardsContent(
    state: RewardsUiState,
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
            SectionHeader(
                titleRes = R.string.hub_rewards,
                trailing = stringResource(
                    R.string.rewards_total,
                    groupNumber(state.totalMiles)
                )
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (state.loading) {
                    repeat(4) {
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
                    state.rows.forEach { row ->
                        MilestoneRow(
                            row = row,
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
private fun MilestoneRow(
    row: MilestoneRowUi,
    onClaim: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val stateText = stringResource(
        when {
            row.claimed -> R.string.state_reward_claimed
            row.claimable -> R.string.state_reward_ready
            else -> R.string.state_reward_locked
        }
    )
    GlassPanel(
        modifier = modifier.fillMaxWidth().alpha(if (row.claimed) 0.58f else 1f),
        cornerRadius = 20.dp,
        borderColor = if (row.claimable) Accent.copy(alpha = 0.6f) else Secondary.copy(alpha = 0.18f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .semantics { stateDescription = stateText }
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_4),
                contentDescription = stringResource(R.string.hub_rewards),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(
                        R.string.rewards_milestone,
                        groupNumber(row.requiredMiles)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(row.contentsRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent
                )
                Spacer(modifier = Modifier.height(7.dp))
                ProgressTrack(
                    progress = row.fraction,
                    height = 6.dp,
                    fillColors = listOf(Secondary, Accent)
                )
                if (row.remaining > 0) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = stringResource(
                            R.string.rewards_remaining,
                            groupNumber(row.remaining)
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            if (row.claimed) {
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
                    onClick = { onClaim(row.index) },
                    enabled = row.claimable,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentDeep,
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
