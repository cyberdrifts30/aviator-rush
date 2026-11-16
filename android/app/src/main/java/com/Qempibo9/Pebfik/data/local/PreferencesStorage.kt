package com.Qempibo9.Pebfik.data.local

import android.content.Context
import android.content.SharedPreferences
import com.Qempibo9.Pebfik.domain.model.AltitudeUnit
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.model.DailyGoalsState
import com.Qempibo9.Pebfik.domain.model.FlightResult
import com.Qempibo9.Pebfik.domain.model.GoalProgress
import com.Qempibo9.Pebfik.domain.model.ProgressState

class PreferencesStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORE, Context.MODE_PRIVATE)

    fun loadProgress(): ProgressState {
        val defaults = ProgressState()
        return ProgressState(
            miles = prefs.getInt(KEY_MILES, defaults.miles),
            fuel = prefs.getFloat(KEY_FUEL, defaults.fuel),
            stageIndex = prefs.getInt(KEY_STAGE_INDEX, defaults.stageIndex),
            stageProgress = prefs.getInt(KEY_STAGE_PROGRESS, defaults.stageProgress),
            streak = prefs.getInt(KEY_STREAK, defaults.streak),
            bestStreak = prefs.getInt(KEY_BEST_STREAK, defaults.bestStreak),
            bestMultiplier = prefs.getFloat(KEY_BEST_MULTIPLIER, defaults.bestMultiplier),
            flights = prefs.getInt(KEY_FLIGHTS, defaults.flights),
            secured = prefs.getInt(KEY_SECURED, defaults.secured),
            crates = prefs.getInt(KEY_CRATES, defaults.crates),
            boosts = prefs.getInt(KEY_BOOSTS, defaults.boosts),
            unlocked = decodeIntSet(prefs.getString(KEY_UNLOCKED, null), defaults.unlocked),
            equippedLivery = prefs.getInt(KEY_EQUIPPED, defaults.equippedLivery),
            claimedMilestones = decodeIntSet(prefs.getString(KEY_MILESTONES, null), emptySet()),
            recentFlights = decodeFlights(prefs.getString(KEY_RECENT, null)),
            goals = decodeGoals(prefs.getString(KEY_GOALS, null)),
            onboardingSeen = prefs.getBoolean(KEY_ONBOARDING, defaults.onboardingSeen),
            lastFlight = decodeFlight(prefs.getString(KEY_LAST_FLIGHT, null))
        )
    }

    fun saveProgress(state: ProgressState) {
        prefs.edit()
            .putInt(KEY_MILES, state.miles)
            .putFloat(KEY_FUEL, state.fuel)
            .putInt(KEY_STAGE_INDEX, state.stageIndex)
            .putInt(KEY_STAGE_PROGRESS, state.stageProgress)
            .putInt(KEY_STREAK, state.streak)
            .putInt(KEY_BEST_STREAK, state.bestStreak)
            .putFloat(KEY_BEST_MULTIPLIER, state.bestMultiplier)
            .putInt(KEY_FLIGHTS, state.flights)
            .putInt(KEY_SECURED, state.secured)
            .putInt(KEY_CRATES, state.crates)
            .putInt(KEY_BOOSTS, state.boosts)
            .putString(KEY_UNLOCKED, state.unlocked.joinToString(SEP_ITEM))
            .putInt(KEY_EQUIPPED, state.equippedLivery)
            .putString(KEY_MILESTONES, state.claimedMilestones.joinToString(SEP_ITEM))
            .putString(KEY_RECENT, state.recentFlights.joinToString(SEP_ITEM) { encodeFlight(it) })
            .putString(KEY_GOALS, encodeGoals(state.goals))
            .putBoolean(KEY_ONBOARDING, state.onboardingSeen)
            .putString(KEY_LAST_FLIGHT, state.lastFlight?.let { encodeFlight(it) } ?: "")
            .apply()
    }

    fun clearProgress() {
        prefs.edit()
            .remove(KEY_MILES)
            .remove(KEY_FUEL)
            .remove(KEY_STAGE_INDEX)
            .remove(KEY_STAGE_PROGRESS)
            .remove(KEY_STREAK)
            .remove(KEY_BEST_STREAK)
            .remove(KEY_BEST_MULTIPLIER)
            .remove(KEY_FLIGHTS)
            .remove(KEY_SECURED)
            .remove(KEY_CRATES)
            .remove(KEY_BOOSTS)
            .remove(KEY_UNLOCKED)
            .remove(KEY_EQUIPPED)
            .remove(KEY_MILESTONES)
            .remove(KEY_RECENT)
            .remove(KEY_GOALS)
            .remove(KEY_LAST_FLIGHT)
            .apply()
    }

    fun loadSettings(): AppSettings = AppSettings(
        reducedMotion = prefs.getBoolean(KEY_REDUCED_MOTION, false),
        autoSecureIndex = prefs.getInt(KEY_AUTO_SECURE, 0),
        altitudeUnit = if (prefs.getInt(KEY_ALTITUDE_UNIT, 0) == 1) {
            AltitudeUnit.METRES
        } else {
            AltitudeUnit.MULTIPLIER
        }
    )

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putBoolean(KEY_REDUCED_MOTION, settings.reducedMotion)
            .putInt(KEY_AUTO_SECURE, settings.autoSecureIndex)
            .putInt(KEY_ALTITUDE_UNIT, if (settings.altitudeUnit == AltitudeUnit.METRES) 1 else 0)
            .apply()
    }

    private fun decodeIntSet(raw: String?, fallback: Set<Int>): Set<Int> {
        if (raw == null) return fallback
        if (raw.isBlank()) return emptySet()
        return raw.split(SEP_ITEM).mapNotNull { it.trim().toIntOrNull() }.toSet()
    }

    private fun encodeFlight(result: FlightResult): String = listOf(
        if (result.secured) "1" else "0",
        result.multiplier.toString(),
        result.altitudeM.toString(),
        result.crates.toString(),
        result.boosts.toString(),
        result.miles.toString(),
        result.streakAfter.toString(),
        if (result.stageCompleted) "1" else "0",
        result.unlockedCollectible.toString(),
        if (result.ceilingReached) "1" else "0"
    ).joinToString(SEP_FIELD)

    private fun decodeFlight(raw: String?): FlightResult? {
        if (raw.isNullOrBlank()) return null
        val parts = raw.split(SEP_FIELD)
        if (parts.size < 10) return null
        val multiplier = parts[1].toFloatOrNull() ?: return null
        return FlightResult(
            secured = parts[0] == "1",
            multiplier = multiplier,
            altitudeM = parts[2].toIntOrNull() ?: 0,
            crates = parts[3].toIntOrNull() ?: 0,
            boosts = parts[4].toIntOrNull() ?: 0,
            miles = parts[5].toIntOrNull() ?: 0,
            streakAfter = parts[6].toIntOrNull() ?: 0,
            stageCompleted = parts[7] == "1",
            unlockedCollectible = parts[8].toIntOrNull() ?: FlightResult.NO_COLLECTIBLE,
            ceilingReached = parts[9] == "1"
        )
    }

    private fun decodeFlights(raw: String?): List<FlightResult> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(SEP_ITEM).mapNotNull { decodeFlight(it) }
    }

    private fun encodeGoals(goals: DailyGoalsState): String {
        if (goals.entries.isEmpty()) return ""
        val body = goals.entries.joinToString(SEP_ITEM) { entry ->
            listOf(
                entry.goalId.toString(),
                entry.progress.toString(),
                if (entry.claimed) "1" else "0"
            ).joinToString(SEP_FIELD)
        }
        return goals.epochDay.toString() + SEP_BLOCK + body
    }

    private fun decodeGoals(raw: String?): DailyGoalsState {
        if (raw.isNullOrBlank()) return DailyGoalsState.EMPTY
        val blocks = raw.split(SEP_BLOCK)
        if (blocks.size < 2) return DailyGoalsState.EMPTY
        val day = blocks[0].toLongOrNull() ?: return DailyGoalsState.EMPTY
        val entries = blocks[1].split(SEP_ITEM).mapNotNull { chunk ->
            val parts = chunk.split(SEP_FIELD)
            if (parts.size < 3) return@mapNotNull null
            val id = parts[0].toIntOrNull() ?: return@mapNotNull null
            GoalProgress(
                goalId = id,
                progress = parts[1].toIntOrNull() ?: 0,
                claimed = parts[2] == "1"
            )
        }
        if (entries.isEmpty()) return DailyGoalsState.EMPTY
        return DailyGoalsState(day, entries)
    }

    private companion object {
        const val STORE = "aviator_rush_store"
        const val SEP_ITEM = ","
        const val SEP_FIELD = ":"
        const val SEP_BLOCK = "|"
        const val KEY_MILES = "miles"
        const val KEY_FUEL = "fuel"
        const val KEY_STAGE_INDEX = "stage_index"
        const val KEY_STAGE_PROGRESS = "stage_progress"
        const val KEY_STREAK = "streak"
        const val KEY_BEST_STREAK = "best_streak"
        const val KEY_BEST_MULTIPLIER = "best_multiplier"
        const val KEY_FLIGHTS = "flights"
        const val KEY_SECURED = "secured"
        const val KEY_CRATES = "crates"
        const val KEY_BOOSTS = "boosts"
        const val KEY_UNLOCKED = "unlocked"
        const val KEY_EQUIPPED = "equipped"
        const val KEY_MILESTONES = "milestones"
        const val KEY_RECENT = "recent"
        const val KEY_GOALS = "goals"
        const val KEY_ONBOARDING = "onboarding_seen"
        const val KEY_LAST_FLIGHT = "last_flight"
        const val KEY_REDUCED_MOTION = "reduced_motion"
        const val KEY_AUTO_SECURE = "auto_secure"
        const val KEY_ALTITUDE_UNIT = "altitude_unit"
    }
}
