package com.Qempibo9.Pebfik.data.repository

import com.Qempibo9.Pebfik.data.local.PreferencesStorage
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProgressRepositoryImpl(
    private val storage: PreferencesStorage
) : ProgressRepository {

    private val _state = MutableStateFlow(storage.loadProgress())
    override val state: StateFlow<ProgressState> = _state.asStateFlow()

    override fun update(transform: (ProgressState) -> ProgressState) {
        val next = transform(_state.value)
        if (next == _state.value) return
        _state.value = next
        storage.saveProgress(next)
    }

    override fun reset() {
        storage.clearProgress()
        val fresh = ProgressState(onboardingSeen = _state.value.onboardingSeen)
        _state.value = fresh
        storage.saveProgress(fresh)
    }

    override fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    override fun minutesUntilReset(): Int {
        val now = LocalDateTime.now()
        return 24 * 60 - (now.hour * 60 + now.minute)
    }
}
