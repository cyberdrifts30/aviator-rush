package com.Qempibo9.Pebfik.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.Danger
import com.Qempibo9.Pebfik.core.theme.PrimaryBright
import com.Qempibo9.Pebfik.core.theme.Secondary
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
import com.Qempibo9.Pebfik.core.ui.StatCardData
import com.Qempibo9.Pebfik.core.ui.StatRow
import com.Qempibo9.Pebfik.core.util.decimal2
import com.Qempibo9.Pebfik.core.util.groupNumber
import com.Qempibo9.Pebfik.presentation.common.spriteRes

@Composable
fun ProfileScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: ProfileViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    ProfileContent(state = state, onMenu = onMenu, modifier = modifier)
}

@Composable
private fun ProfileContent(
    state: ProfileUiState,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topCards = buildList {
        if (state.flights > 0) {
            add(StatCardData(R.string.stat_flights, state.flights.toString(), Secondary))
        }
        if (state.secured > 0) {
            add(StatCardData(R.string.stat_secured, state.secured.toString(), Success))
        }
        if (state.bestMultiplier > 0f) {
            add(
                StatCardData(
                    R.string.stat_best,
                    stringResource(R.string.fmt_multiplier, decimal2(state.bestMultiplier)),
                    Accent
                )
            )
        }
    }
    val bottomCards = buildList {
        if (state.bestStreak > 0) {
            add(
                StatCardData(
                    R.string.stat_best_streak,
                    stringResource(R.string.fmt_streak, state.bestStreak),
                    PrimaryBright
                )
            )
        }
        if (state.crates > 0) {
            add(StatCardData(R.string.stat_crates, state.crates.toString(), Secondary))
        }
        if (state.stagesComplete > 0) {
            add(StatCardData(R.string.stat_stages, state.stagesComplete.toString(), Accent))
        }
    }

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
                titleRes = R.string.hub_profile,
                trailing = stringResource(R.string.profile_miles, groupNumber(state.miles))
            )
            PilotCard(state = state)
            if (topCards.size >= 2) {
                StatRow(cards = topCards)
            }
            if (bottomCards.size >= 2) {
                StatRow(cards = bottomCards)
            }
            if (topCards.size < 2 && bottomCards.size < 2) {
                Text(
                    text = stringResource(R.string.profile_no_stats),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            RecentStrip(state = state, modifier = Modifier.weight(1f))
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }
}

@Composable
private fun PilotCard(state: ProfileUiState) {
    GlassPanel(modifier = Modifier.fillMaxWidth().height(172.dp), cornerRadius = 24.dp) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(18.dp)
        ) {
            Image(
                painter = painterResource(spriteRes(state.equippedArtIndex)),
                contentDescription = if (state.equippedNameRes != 0) {
                    stringResource(state.equippedNameRes)
                } else {
                    stringResource(R.string.hub_profile)
                },
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(state.rankNameRes),
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (state.nextRankNameRes != 0) {
                    Text(
                        text = stringResource(
                            R.string.profile_next_rank,
                            groupNumber(state.milesToNext),
                            stringResource(state.nextRankNameRes)
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = Secondary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.profile_max_rank),
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                ProgressTrack(
                    progress = state.rankProgress,
                    height = 6.dp,
                    fillColors = listOf(Accent, PrimaryBright)
                )
                if (state.streak > 0) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .background(Accent, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fmt_streak_label, state.streak),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextOnAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentStrip(state: ProfileUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_recent),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (state.recent.isEmpty()) {
            Text(
                text = stringResource(R.string.profile_no_flights),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                state.recent.forEach { flight ->
                    val tint = if (flight.secured) Success else Danger
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(tint.copy(alpha = 0.16f), RoundedCornerShape(14.dp))
                            .border(1.dp, tint.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        Text(
                            text = stringResource(
                                R.string.fmt_multiplier,
                                decimal2(flight.multiplier)
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = tint,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
