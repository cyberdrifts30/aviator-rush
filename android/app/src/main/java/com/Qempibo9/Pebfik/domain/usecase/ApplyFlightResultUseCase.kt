package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.DailyGoalsState
import com.Qempibo9.Pebfik.domain.model.FlightOutcome
import com.Qempibo9.Pebfik.domain.model.FlightResult
import com.Qempibo9.Pebfik.domain.model.GoalKind
import com.Qempibo9.Pebfik.domain.model.GoalProgress
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.model.StageKind
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ApplyFlightResultUseCase(
    private val catalog: GameCatalog,
    private val secureFlight: SecureFlightUseCase
) {

    operator fun invoke(state: ProgressState, outcome: FlightOutcome): ProgressState {
        val crateMiles = outcome.crates * GameConfig.CRATE_MILES
        val earned = if (outcome.secured) {
            secureFlight(outcome.multiplier, outcome.crates, state.streak)
        } else {
            crateMiles
        }
        val newStreak = if (outcome.secured) state.streak + 1 else 0
        val hundredths = (outcome.multiplier * 100f).roundToInt()

        var stageIndex = state.stageIndex
        var stageProgress = state.stageProgress
        var unlocked = state.unlocked
        var bonusMiles = 0
        var fuel = state.fuel
        var stageCompleted = false
        var unlockedCollectible = FlightResult.NO_COLLECTIBLE

        if (outcome.secured) {
            fuel = min(GameConfig.FUEL_FULL, fuel + GameConfig.FUEL_REFILL_SECURE)
        }

        val stage = catalog.stages.getOrNull(stageIndex)
        if (stage != null) {
            stageProgress = when (stage.kind) {
                StageKind.SECURE_FLIGHTS -> stageProgress + if (outcome.secured) 1 else 0
                StageKind.REACH_MULTIPLIER -> max(stageProgress, hundredths)
                StageKind.COLLECT_CRATES -> stageProgress + outcome.crates
                StageKind.SECURE_STREAK -> max(stageProgress, newStreak)
            }
            if (stageProgress >= stage.target) {
                stageCompleted = true
                bonusMiles = stage.rewardMiles
                fuel = min(GameConfig.FUEL_FULL, fuel + GameConfig.FUEL_REFILL_STAGE)
                if (stage.collectibleId >= 0 && !unlocked.contains(stage.collectibleId)) {
                    unlocked = unlocked + stage.collectibleId
                    unlockedCollectible = stage.collectibleId
                }
                stageIndex = min(stageIndex + 1, GameConfig.TOTAL_STAGES)
                stageProgress = 0
            }
        }

        val result = FlightResult(
            secured = outcome.secured,
            multiplier = outcome.multiplier,
            altitudeM = ((outcome.multiplier - 1f) * 1000f).roundToInt(),
            crates = outcome.crates,
            boosts = outcome.boosts,
            miles = earned + bonusMiles,
            streakAfter = newStreak,
            stageCompleted = stageCompleted,
            unlockedCollectible = unlockedCollectible,
            ceilingReached = outcome.ceilingReached
        )

        val goals = advanceGoals(state.goals, outcome, newStreak, hundredths, stageCompleted)

        return state.copy(
            miles = state.miles + result.miles,
            fuel = fuel,
            stageIndex = stageIndex,
            stageProgress = stageProgress,
            streak = newStreak,
            bestStreak = max(state.bestStreak, newStreak),
            bestMultiplier = max(state.bestMultiplier, if (outcome.secured) outcome.multiplier else state.bestMultiplier),
            flights = state.flights + 1,
            secured = state.secured + if (outcome.secured) 1 else 0,
            crates = state.crates + outcome.crates,
            boosts = state.boosts + outcome.boosts,
            unlocked = unlocked,
            recentFlights = (listOf(result) + state.recentFlights).take(GameConfig.RECENT_FLIGHTS),
            goals = goals,
            lastFlight = result
        )
    }

    private fun advanceGoals(
        goals: DailyGoalsState,
        outcome: FlightOutcome,
        newStreak: Int,
        hundredths: Int,
        stageCompleted: Boolean
    ): DailyGoalsState {
        if (goals.entries.isEmpty()) return goals
        val entries = goals.entries.map { entry ->
            val goal = catalog.goalPool.firstOrNull { it.id == entry.goalId } ?: return@map entry
            val advanced = when (goal.kind) {
                GoalKind.SECURE_FLIGHTS -> entry.progress + if (outcome.secured) 1 else 0
                GoalKind.REACH_MULTIPLIER -> max(entry.progress, hundredths)
                GoalKind.COLLECT_CRATES -> entry.progress + outcome.crates
                GoalKind.BOOST_TIMES -> entry.progress + outcome.boosts
                GoalKind.SECURE_STREAK -> max(entry.progress, newStreak)
                GoalKind.COMPLETE_STAGE -> entry.progress + if (stageCompleted) 1 else 0
            }
            entry.copy(progress = min(advanced, goal.target))
        }
        return goals.copy(entries = entries)
    }
}

fun GoalProgress.isComplete(target: Int): Boolean = progress >= target
