package com.Qempibo9.Pebfik.presentation.common

import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.domain.model.Rank
import com.Qempibo9.Pebfik.domain.model.Rarity
import com.Qempibo9.Pebfik.domain.model.StageKind

fun rankNameRes(rank: Rank): Int = when (rank) {
    Rank.CADET -> R.string.rank_cadet
    Rank.PILOT -> R.string.rank_pilot
    Rank.CAPTAIN -> R.string.rank_captain
    Rank.ACE -> R.string.rank_ace
    Rank.LEGEND -> R.string.rank_legend
}

fun spriteRes(index: Int): Int = when (index) {
    1 -> R.drawable.sprite_1
    2 -> R.drawable.sprite_2
    3 -> R.drawable.sprite_3
    4 -> R.drawable.sprite_4
    else -> R.drawable.sprite_5
}

fun stageRequirementRes(kind: StageKind): Int = when (kind) {
    StageKind.SECURE_FLIGHTS -> R.string.stage_req_secure
    StageKind.REACH_MULTIPLIER -> R.string.stage_req_reach
    StageKind.COLLECT_CRATES -> R.string.stage_req_crates
    StageKind.SECURE_STREAK -> R.string.stage_req_streak
}

fun rarityRes(rarity: Rarity): Int = when (rarity) {
    Rarity.COMMON -> R.string.rarity_common
    Rarity.RARE -> R.string.rarity_rare
    Rarity.ELITE -> R.string.rarity_elite
}

fun itemKindRes(kind: ItemKind): Int = when (kind) {
    ItemKind.LIVERY -> R.string.item_kind_livery
    ItemKind.BADGE -> R.string.item_kind_badge
}
