package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.DailyGoalsState
import com.Qempibo9.Pebfik.domain.model.GoalProgress
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import kotlin.random.Random

class RefreshDailyGoalsUseCase(private val catalog: GameCatalog) {

    operator fun invoke(state: ProgressState, epochDay: Long): ProgressState {
        val current = state.goals
        val valid = current.epochDay == epochDay &&
            current.entries.size == GameConfig.DAILY_GOAL_COUNT &&
            current.entries.all { entry -> catalog.goalPool.any { it.id == entry.goalId } }
        if (valid) return state
        return state.copy(goals = generate(epochDay))
    }

    fun generate(epochDay: Long): DailyGoalsState {
        val random = Random((epochDay * GameConfig.SEED_A).toInt() xor GameConfig.SEED_B)
        val picked = catalog.goalPool.map { it.id }.shuffled(random).take(GameConfig.DAILY_GOAL_COUNT)
        return DailyGoalsState(
            epochDay = epochDay,
            entries = picked.map { GoalProgress(goalId = it, progress = 0, claimed = false) }
        )
    }
}
