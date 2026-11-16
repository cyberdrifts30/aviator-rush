package com.Qempibo9.Pebfik.presentation.collection

import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.domain.model.Rarity

data class CollectionCardUi(
    val id: Int,
    val nameRes: Int,
    val flavorRes: Int,
    val requirementRes: Int,
    val artIndex: Int,
    val kind: ItemKind,
    val rarity: Rarity,
    val unlocked: Boolean,
    val equipped: Boolean
)

data class CollectionUiState(
    val items: List<CollectionCardUi> = emptyList(),
    val unlockedCount: Int = 0,
    val total: Int = GameConfig.TOTAL_COLLECTIBLES,
    val selectedId: Int = NO_SELECTION,
    val reducedMotion: Boolean = false
) {
    val selected: CollectionCardUi? get() = items.firstOrNull { it.id == selectedId }

    companion object {
        const val NO_SELECTION = -1
    }
}
