package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.domain.model.Rank

class ResolveRankUseCase {

    operator fun invoke(miles: Int): Rank =
        Rank.entries.last { miles >= it.requiredMiles }

    fun next(miles: Int): Rank? =
        Rank.entries.firstOrNull { miles < it.requiredMiles }

    fun progressToNext(miles: Int): Float {
        val current = invoke(miles)
        val upcoming = next(miles) ?: return 1f
        val span = (upcoming.requiredMiles - current.requiredMiles).toFloat()
        if (span <= 0f) return 1f
        return ((miles - current.requiredMiles) / span).coerceIn(0f, 1f)
    }

    fun milesToNext(miles: Int): Int {
        val upcoming = next(miles) ?: return 0
        return upcoming.requiredMiles - miles
    }
}
