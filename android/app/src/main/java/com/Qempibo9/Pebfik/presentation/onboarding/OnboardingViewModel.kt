package com.Qempibo9.Pebfik.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    fun nextStep() {
        _state.update { current ->
            if (current.isLast) current else current.copy(step = current.step + 1)
        }
    }

    fun markSeen() {
        progressRepository.update { it.copy(onboardingSeen = true) }
    }
}
