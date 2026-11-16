package com.Qempibo9.Pebfik.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val TICK_MS = 16L
    const val CLIMB_RATE = 0.0045f
    const val BOOST_FACTOR = 2.4f
    const val FUEL_FULL = 100f
    const val FUEL_DRAIN = 1.8f
    const val FUEL_REFILL_SECURE = 34f
    const val FUEL_REFILL_STAGE = 12f
    const val MIN_FLIGHT_MS = 1200L
    const val MAX_FLIGHT_MS = 12000L
    const val BASE_MILES = 100
    const val CRATE_MILES = 40
    const val CRATE_LIFETIME_MS = 1400L
    const val STREAK_STEP = 0.15f
    const val STREAK_CAP = 5
    const val TOTAL_STAGES = 12
    const val TOTAL_COLLECTIBLES = 12
    const val DAILY_GOAL_COUNT = 3
    const val SEED_A = 7919
    const val SEED_B = 104729
    const val TRAIL_SAMPLES = 48
    const val SECURE_ANIM_MS = 420L
    const val STALL_ANIM_MS = 900L
    const val HINT_FLIGHTS = 3
    const val RECENT_FLIGHTS = 5
    val AUTO_SECURE_STEPS = listOf(0f, 2f, 5f, 10f)
}
