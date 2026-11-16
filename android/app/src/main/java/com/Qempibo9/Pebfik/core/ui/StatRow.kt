package com.Qempibo9.Pebfik.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StatRow(
    cards: List<StatCardData>,
    modifier: Modifier = Modifier
) {
    if (cards.size < 2) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        cards.forEach { card ->
            StatCard(data = card, modifier = Modifier.weight(1f))
        }
    }
}
