package com.Qempibo9.Pebfik.presentation.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.Danger
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceLocked
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.Success
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.ui.EmptyState
import com.Qempibo9.Pebfik.core.ui.LockedTile
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton
import com.Qempibo9.Pebfik.core.ui.SectionHeader
import com.Qempibo9.Pebfik.domain.model.Rarity
import com.Qempibo9.Pebfik.presentation.common.spriteRes
import com.Qempibo9.Pebfik.presentation.dialog.CollectionDetailDialog

@Composable
fun CollectionScreen(
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: CollectionViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectionContent(
        state = state,
        onCardSelected = viewModel::onCardSelected,
        onEquip = viewModel::onEquip,
        onDismiss = viewModel::onDialogDismissed,
        onMenu = onMenu,
        modifier = modifier
    )
}

@Composable
private fun CollectionContent(
    state: CollectionUiState,
    onCardSelected: (Int) -> Unit,
    onEquip: (Int) -> Unit,
    onDismiss: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.5f,
        overlay = listOf(
            BgBase.copy(alpha = 0.93f),
            SurfaceLow.copy(alpha = 0.7f),
            BgBase.copy(alpha = 0.96f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SectionHeader(
                titleRes = R.string.hub_collection,
                trailing = stringResource(
                    R.string.collection_unlocked,
                    state.unlockedCount,
                    state.total
                )
            )
            if (state.unlockedCount == 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    EmptyState(
                        artRes = R.drawable.sprite_2,
                        titleRes = R.string.collection_empty_title,
                        messageRes = R.string.collection_empty_body
                    )
                }
            } else {
                CollectionGrid(
                    state = state,
                    onCardSelected = onCardSelected,
                    modifier = Modifier.weight(1f)
                )
            }
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }
    val selected = state.selected
    if (selected != null) {
        CollectionDetailDialog(
            card = selected,
            onEquip = onEquip,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun CollectionGrid(
    state: CollectionUiState,
    onCardSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        (0 until 4).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                (0 until 3).forEach { column ->
                    val card = state.items.getOrNull(row * 3 + column)
                    if (card != null) {
                        CollectionCard(
                            card = card,
                            onClick = { onCardSelected(card.id) },
                            modifier = Modifier.weight(1f).fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionCard(
    card: CollectionCardUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val rarityColor = when (card.rarity) {
        Rarity.COMMON -> Secondary
        Rarity.RARE -> Accent
        Rarity.ELITE -> Danger
    }
    val stateText = stringResource(
        when {
            card.equipped -> R.string.state_item_equipped
            card.unlocked -> R.string.state_item_unlocked
            else -> R.string.state_item_locked
        }
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(
                if (card.unlocked) SurfaceLow.copy(alpha = 0.86f) else SurfaceLocked.copy(alpha = 0.72f),
                shape
            )
            .border(
                1.dp,
                if (card.equipped) Accent else rarityColor.copy(alpha = 0.26f),
                shape
            )
            .clickable(enabled = card.unlocked) { onClick() }
            .padding(6.dp)
            .semantics { stateDescription = stateText }
    ) {
        if (card.unlocked) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(spriteRes(card.artIndex)),
                    contentDescription = stringResource(card.nameRes),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(44.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(card.nameRes),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Spacer(
                    modifier = Modifier
                        .size(6.dp)
                        .background(rarityColor, CircleShape)
                )
            }
            if (card.equipped) {
                Text(
                    text = stringResource(R.string.state_item_equipped),
                    style = MaterialTheme.typography.labelSmall,
                    color = Success,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(spriteRes(card.artIndex)),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(38.dp).alpha(0.22f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                LockedTile(requirementRes = card.requirementRes, iconSize = 16)
            }
        }
    }
}
