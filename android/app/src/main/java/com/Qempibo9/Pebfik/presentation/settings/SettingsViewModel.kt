package com.Qempibo9.Pebfik.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val confirmReset = MutableStateFlow(false)

    private val resetEvents = Channel<Unit>(Channel.BUFFERED)
    val resetFlow: Flow<Unit> = resetEvents.receiveAsFlow()

    val state: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        confirmReset
    ) { settings, confirm ->
        SettingsUiState(
            reducedMotion = settings.reducedMotion,
            autoSecureIndex = settings.autoSecureIndex,
            altitudeUnit = settings.altitudeUnit,
            confirmReset = confirm
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        SettingsUiState(
            reducedMotion = settingsRepository.settings.value.reducedMotion,
            autoSecureIndex = settingsRepository.settings.value.autoSecureIndex,
            altitudeUnit = settingsRepository.settings.value.altitudeUnit
        )
    )

    fun onReducedMotionChanged(enabled: Boolean) = settingsRepository.setReducedMotion(enabled)

    fun onAutoSecureSelected(index: Int) = settingsRepository.setAutoSecureIndex(index)

    fun onAltitudeUnitSelected(index: Int) {
        val unit = if (index == 1) AltitudeUnit.METRES else AltitudeUnit.MULTIPLIER
        settingsRepository.setAltitudeUnit(unit)
    }

    fun onResetRequested() {
        confirmReset.value = true
    }

    fun onResetDismissed() {
        confirmReset.value = false
    }

    fun onResetConfirmed() {
        progressRepository.reset()
        confirmReset.value = false
        viewModelScope.launch { resetEvents.send(Unit) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
