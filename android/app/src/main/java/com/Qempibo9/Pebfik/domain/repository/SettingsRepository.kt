package com.Qempibo9.Pebfik.domain.repository

import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.AppSettings
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val settings: StateFlow<AppSettings>
    fun setReducedMotion(enabled: Boolean)
    fun setAutoSecureIndex(index: Int)
    fun setAltitudeUnit(unit: AltitudeUnit)
}
