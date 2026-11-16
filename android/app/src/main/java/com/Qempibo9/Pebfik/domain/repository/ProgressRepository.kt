package com.Qempibo9.Pebfik.domain.repository

import com.Qempibo9.Pebfik.domain.model.ProgressState
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val state: StateFlow<ProgressState>
    fun update(transform: (ProgressState) -> ProgressState)
    fun reset()
    fun todayEpochDay(): Long
    fun minutesUntilReset(): Int
}
