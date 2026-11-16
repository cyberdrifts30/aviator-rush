package com.Qempibo9.Pebfik.presentation.onboarding

data class OnboardingUiState(
    val step: Int = 0,
    val totalSteps: Int = 3
) {
    val isLast: Boolean get() = step >= totalSteps - 1
}
