package com.Qempibo9.Pebfik.data.repository

import com.Qempibo9.Pebfik.data.local.PreferencesStorage
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val storage: PreferencesStorage
) : SettingsRepository {

    private val _settings = MutableStateFlow(storage.loadSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override fun setReducedMotion(enabled: Boolean) = write { it.copy(reducedMotion = enabled) }

    override fun setAutoSecureIndex(index: Int) = write { it.copy(autoSecureIndex = index) }

    override fun setAltitudeUnit(unit: AltitudeUnit) = write { it.copy(altitudeUnit = unit) }

    private fun write(transform: (AppSettings) -> AppSettings) {
        val next = transform(_settings.value)
        if (next == _settings.value) return
        _settings.value = next
        storage.saveSettings(next)
    }
}
