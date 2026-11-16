package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.core.config.GameConfig
import kotlin.math.min
import kotlin.math.roundToInt

class SecureFlightUseCase {

    fun streakMultiplier(streak: Int): Float =
        1f + min(streak, GameConfig.STREAK_CAP) * GameConfig.STREAK_STEP

    operator fun invoke(multiplier: Float, crates: Int, streak: Int): Int {
        val base = GameConfig.BASE_MILES * multiplier * streakMultiplier(streak)
        return base.roundToInt() + crates * GameConfig.CRATE_MILES
    }
}
