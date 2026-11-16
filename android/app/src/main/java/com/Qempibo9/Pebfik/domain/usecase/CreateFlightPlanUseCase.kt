package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.CrateDrop
import com.Qempibo9.Pebfik.domain.model.FlightPlan
import kotlin.random.Random

class CreateFlightPlanUseCase {

    operator fun invoke(stageIndex: Int, flightIndex: Int): FlightPlan {
        val seed = (stageIndex + 1) * GameConfig.SEED_A xor (flightIndex + 1) * GameConfig.SEED_B
        val random = Random(seed)
        val stall = drawStall(random)
        val crateCount = 2 + random.nextInt(4)
        val span = stall - 1.25f
        val crates = if (span <= 0.15f) {
            emptyList()
        } else {
            (0 until crateCount).map { index ->
                val slot = (index + 0.35f + random.nextFloat() * 0.3f) / crateCount
                CrateDrop(
                    id = index,
                    triggerMultiplier = 1.25f + span * slot,
                    horizontalFraction = 0.18f + random.nextFloat() * 0.64f,
                    verticalFraction = 0.22f + random.nextFloat() * 0.46f
                )
            }
        }
        return FlightPlan(
            seed = seed,
            stallMultiplier = stall,
            crates = crates,
            cloudOffsetA = random.nextFloat(),
            cloudOffsetB = random.nextFloat()
        )
    }

    private fun drawStall(random: Random): Float {
        val roll = random.nextInt(100)
        val range = when {
            roll < 18 -> 1.00f to 1.50f
            roll < 50 -> 1.50f to 2.50f
            roll < 75 -> 2.50f to 4.50f
            roll < 90 -> 4.50f to 8.00f
            roll < 98 -> 8.00f to 15.00f
            else -> 15.00f to 25.00f
        }
        return range.first + random.nextFloat() * (range.second - range.first)
    }
}
