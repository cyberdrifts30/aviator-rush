package com.Qempibo9.Pebfik.domain.model

enum class ItemKind {
    LIVERY,
    BADGE
}

enum class Rarity {
    COMMON,
    RARE,
    ELITE
}

data class CollectionItem(
    val id: Int,
    val nameRes: Int,
    val flavorRes: Int,
    val requirementRes: Int,
    val artIndex: Int,
    val kind: ItemKind,
    val rarity: Rarity
)
