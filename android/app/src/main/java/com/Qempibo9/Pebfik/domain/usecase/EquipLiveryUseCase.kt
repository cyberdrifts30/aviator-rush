package com.Qempibo9.Pebfik.domain.usecase

import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.domain.model.ProgressState
import com.Qempibo9.Pebfik.domain.repository.GameCatalog

class EquipLiveryUseCase(private val catalog: GameCatalog) {

    operator fun invoke(state: ProgressState, itemId: Int): ProgressState {
        val item = catalog.collectibles.firstOrNull { it.id == itemId } ?: return state
        if (item.kind != ItemKind.LIVERY) return state
        if (!state.unlocked.contains(itemId)) return state
        return state.copy(equippedLivery = itemId)
    }
}
