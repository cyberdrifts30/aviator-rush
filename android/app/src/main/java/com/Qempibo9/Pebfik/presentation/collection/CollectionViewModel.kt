package com.Qempibo9.Pebfik.presentation.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.domain.model.AppSettings
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import com.Qempibo9.Pebfik.domain.usecase.EquipLiveryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CollectionViewModel(
    private val progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    private val catalog: GameCatalog,
    private val equipLivery: EquipLiveryUseCase
) : ViewModel() {

    private val selection = MutableStateFlow(CollectionUiState.NO_SELECTION)

    val state: StateFlow<CollectionUiState> = combine(
        progressRepository.state,
        settingsRepository.settings,
        selection
    ) { progress, settings, selected -> map(progress, settings, selected) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
            map(
                progressRepository.state.value,
                settingsRepository.settings.value,
                selection.value
            )
        )

    fun onCardSelected(id: Int) {
        if (!progressRepository.state.value.unlocked.contains(id)) return
        selection.value = id
    }

    fun onDialogDismissed() {
        selection.value = CollectionUiState.NO_SELECTION
    }

    fun onEquip(id: Int) {
        progressRepository.update { equipLivery(it, id) }
        selection.value = CollectionUiState.NO_SELECTION
    }

    private fun map(
        progress: ProgressState,
        settings: AppSettings,
        selected: Int
    ): CollectionUiState {
        val items = catalog.collectibles.map { item ->
            CollectionCardUi(
                id = item.id,
                nameRes = item.nameRes,
                flavorRes = item.flavorRes,
                requirementRes = item.requirementRes,
                artIndex = item.artIndex,
                kind = item.kind,
                rarity = item.rarity,
                unlocked = progress.unlocked.contains(item.id),
                equipped = progress.equippedLivery == item.id
            )
        }
        return CollectionUiState(
            items = items,
            unlockedCount = items.count { it.unlocked },
            selectedId = selected,
            reducedMotion = settings.reducedMotion
        )
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
