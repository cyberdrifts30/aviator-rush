package com.Qempibo9.Pebfik.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.usecase.ResolveRankUseCase
import com.Qempibo9.Pebfik.presentation.common.rankNameRes
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    progressRepository: ProgressRepository,
    private val catalog: GameCatalog,
    private val resolveRank: ResolveRankUseCase
) : ViewModel() {

    val state: StateFlow<ProfileUiState> = progressRepository.state
        .map { map(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(progressRepository.state.value)
        )

    private fun map(progress: ProgressState): ProfileUiState {
        val equipped = catalog.collectibles.firstOrNull { it.id == progress.equippedLivery }
        val next = resolveRank.next(progress.miles)
        return ProfileUiState(
            miles = progress.miles,
            rankNameRes = rankNameRes(resolveRank(progress.miles)),
            nextRankNameRes = next?.let { rankNameRes(it) } ?: 0,
            milesToNext = resolveRank.milesToNext(progress.miles),
            rankProgress = resolveRank.progressToNext(progress.miles),
            streak = progress.streak,
            equippedArtIndex = equipped?.artIndex ?: 1,
            equippedNameRes = equipped?.nameRes ?: 0,
            flights = progress.flights,
            secured = progress.secured,
            bestMultiplier = progress.bestMultiplier,
            bestStreak = progress.bestStreak,
            crates = progress.crates,
            stagesComplete = progress.stageIndex,
            recent = progress.recentFlights.map {
                RecentFlightUi(secured = it.secured, multiplier = it.multiplier)
            }
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
