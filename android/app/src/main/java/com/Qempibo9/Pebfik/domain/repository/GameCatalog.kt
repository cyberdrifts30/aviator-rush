package com.Qempibo9.Pebfik.domain.repository

import com.Qempibo9.Pebfik.domain.model.CollectionItem
import com.Qempibo9.Pebfik.domain.model.DailyGoal
import com.Qempibo9.Pebfik.domain.model.Milestone
import com.Qempibo9.Pebfik.domain.model.Stage

interface GameCatalog {
    val stages: List<Stage>
    val goalPool: List<DailyGoal>
    val collectibles: List<CollectionItem>
    val milestones: List<Milestone>
}
