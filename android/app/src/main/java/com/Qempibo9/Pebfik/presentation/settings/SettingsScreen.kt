package com.Qempibo9.Pebfik.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.Qempibo9.Pebfik.core.theme.Danger
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextMuted
import com.Qempibo9.Pebfik.core.theme.TextOnAccent
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.ChipRow
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.SectionHeader
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.presentation.dialog.ConfirmResetDialog

@Composable
fun SettingsScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SettingsViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.resetFlow.collect { onMenu() }
    }

    SettingsContent(
        state = state,
        onReducedMotion = viewModel::onReducedMotionChanged,
        onAutoSecure = viewModel::onAutoSecureSelected,
        onAltitudeUnit = viewModel::onAltitudeUnitSelected,
        onResetRequested = viewModel::onResetRequested,
        onResetConfirmed = viewModel::onResetConfirmed,
        onResetDismissed = viewModel::onResetDismissed,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onReducedMotion: (Boolean) -> Unit,
    onAutoSecure: (Int) -> Unit,
    onAltitudeUnit: (Int) -> Unit,
    onResetRequested: () -> Unit,
    onResetConfirmed: () -> Unit,
    onResetDismissed: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val autoLabels = listOf(
        stringResource(R.string.auto_off),
        stringResource(R.string.fmt_auto, AUTO_LOW),
        stringResource(R.string.fmt_auto, AUTO_MID),
        stringResource(R.string.fmt_auto, AUTO_HIGH)
    )
    val unitLabels = listOf(
        stringResource(R.string.unit_multiplier),
        stringResource(R.string.unit_metres)
    )
    val motionState = stringResource(
        if (state.reducedMotion) R.string.state_on else R.string.state_off
    )

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
            SectionHeader(titleRes = R.string.hub_settings)

            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .semantics { stateDescription = motionState }
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_reduced_motion),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.setting_reduced_motion_body),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = state.reducedMotion,
                            onCheckedChange = onReducedMotion,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TextOnAccent,
                                checkedTrackColor = Accent,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SurfaceHigh
                            )
                        )
                    }
                    Divider()
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(
                            text = stringResource(R.string.setting_auto_secure),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ChipRow(
                            labels = autoLabels,
                            selectedIndex = state.autoSecureIndex,
                            onSelect = onAutoSecure
                        )
                    }
                    Divider()
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(
                            text = stringResource(R.string.setting_altitude_unit),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ChipRow(
                            labels = unitLabels,
                            selectedIndex = if (state.altitudeUnit == AltitudeUnit.METRES) 1 else 0,
                            onSelect = onAltitudeUnit
                        )
                    }
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onResetRequested,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Danger.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Danger
                        ),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.setting_reset),
                            style = MaterialTheme.typography.labelLarge,
                            color = Danger
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.settings_footer),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }

    if (state.confirmReset) {
        ConfirmResetDialog(onConfirm = onResetConfirmed, onDismiss = onResetDismissed)
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Secondary.copy(alpha = 0.07f))
    )
}

private const val AUTO_LOW = "2"
private const val AUTO_MID = "5"
private const val AUTO_HIGH = "10"
