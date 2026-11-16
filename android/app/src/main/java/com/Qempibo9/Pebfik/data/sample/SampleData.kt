package com.Qempibo9.Pebfik.data.sample

import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.domain.model.CollectionItem
import com.Qempibo9.Pebfik.domain.model.DailyGoal
import com.Qempibo9.Pebfik.domain.model.GoalKind
import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.domain.model.Milestone
import com.Qempibo9.Pebfik.domain.model.Rarity
import com.Qempibo9.Pebfik.domain.model.Stage
import com.Qempibo9.Pebfik.domain.model.StageKind
import com.Qempibo9.Pebfik.domain.repository.GameCatalog

object SampleData : GameCatalog {

    override val stages: List<Stage> = listOf(
        Stage(0, R.string.stage_name_1, StageKind.SECURE_FLIGHTS, 3, 250, 1),
        Stage(1, R.string.stage_name_2, StageKind.COLLECT_CRATES, 6, 300, 2),
        Stage(2, R.string.stage_name_3, StageKind.REACH_MULTIPLIER, 300, 400, 3),
        Stage(3, R.string.stage_name_4, StageKind.SECURE_FLIGHTS, 5, 500, 4),
        Stage(4, R.string.stage_name_5, StageKind.SECURE_STREAK, 2, 600, 5),
        Stage(5, R.string.stage_name_6, StageKind.COLLECT_CRATES, 12, 700, 6),
        Stage(6, R.string.stage_name_7, StageKind.REACH_MULTIPLIER, 450, 850, 7),
        Stage(7, R.string.stage_name_8, StageKind.SECURE_FLIGHTS, 7, 950, 8),
        Stage(8, R.string.stage_name_9, StageKind.SECURE_STREAK, 3, 1100, 9),
        Stage(9, R.string.stage_name_10, StageKind.COLLECT_CRATES, 20, 1250, 10),
        Stage(10, R.string.stage_name_11, StageKind.REACH_MULTIPLIER, 800, 1400, 11),
        Stage(11, R.string.stage_name_12, StageKind.SECURE_FLIGHTS, 10, 1500, -1)
    )

    override val goalPool: List<DailyGoal> = listOf(
        DailyGoal(0, R.string.goal_title_1, GoalKind.SECURE_FLIGHTS, 4, 250, 1),
        DailyGoal(1, R.string.goal_title_2, GoalKind.REACH_MULTIPLIER, 300, 250, 1),
        DailyGoal(2, R.string.goal_title_3, GoalKind.COLLECT_CRATES, 10, 250, 2),
        DailyGoal(3, R.string.goal_title_4, GoalKind.BOOST_TIMES, 6, 250, 3),
        DailyGoal(4, R.string.goal_title_5, GoalKind.SECURE_STREAK, 2, 250, 4),
        DailyGoal(5, R.string.goal_title_6, GoalKind.COMPLETE_STAGE, 1, 250, 4)
    )

    override val collectibles: List<CollectionItem> = listOf(
        CollectionItem(0, R.string.item_name_1, R.string.item_flavor_1, R.string.item_req_1, 1, ItemKind.LIVERY, Rarity.COMMON),
        CollectionItem(1, R.string.item_name_2, R.string.item_flavor_2, R.string.item_req_2, 1, ItemKind.LIVERY, Rarity.COMMON),
        CollectionItem(2, R.string.item_name_3, R.string.item_flavor_3, R.string.item_req_3, 2, ItemKind.BADGE, Rarity.COMMON),
        CollectionItem(3, R.string.item_name_4, R.string.item_flavor_4, R.string.item_req_4, 5, ItemKind.LIVERY, Rarity.RARE),
        CollectionItem(4, R.string.item_name_5, R.string.item_flavor_5, R.string.item_req_5, 3, ItemKind.BADGE, Rarity.COMMON),
        CollectionItem(5, R.string.item_name_6, R.string.item_flavor_6, R.string.item_req_6, 4, ItemKind.LIVERY, Rarity.RARE),
        CollectionItem(6, R.string.item_name_7, R.string.item_flavor_7, R.string.item_req_7, 2, ItemKind.BADGE, Rarity.RARE),
        CollectionItem(7, R.string.item_name_8, R.string.item_flavor_8, R.string.item_req_8, 1, ItemKind.LIVERY, Rarity.ELITE),
        CollectionItem(8, R.string.item_name_9, R.string.item_flavor_9, R.string.item_req_9, 3, ItemKind.BADGE, Rarity.RARE),
        CollectionItem(9, R.string.item_name_10, R.string.item_flavor_10, R.string.item_req_10, 5, ItemKind.BADGE, Rarity.ELITE),
        CollectionItem(10, R.string.item_name_11, R.string.item_flavor_11, R.string.item_req_11, 4, ItemKind.BADGE, Rarity.ELITE),
        CollectionItem(11, R.string.item_name_12, R.string.item_flavor_12, R.string.item_req_12, 4, ItemKind.LIVERY, Rarity.ELITE)
    )

    override val milestones: List<Milestone> = listOf(
        Milestone(0, 1000, R.string.milestone_contents_1, 500, -1),
        Milestone(1, 5000, R.string.milestone_contents_2, 2500, -1),
        Milestone(2, 15000, R.string.milestone_contents_3, 6000, -1),
        Milestone(3, 40000, R.string.milestone_contents_4, 15000, -1)
    )
}
