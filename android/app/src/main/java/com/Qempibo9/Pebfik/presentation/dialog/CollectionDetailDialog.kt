package com.Qempibo9.Pebfik.presentation.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceLow
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.domain.model.ItemKind
import com.Qempibo9.Pebfik.presentation.collection.CollectionCardUi
import com.Qempibo9.Pebfik.presentation.common.itemKindRes
import com.Qempibo9.Pebfik.presentation.common.rarityRes
import com.Qempibo9.Pebfik.presentation.common.spriteRes

@Composable
fun CollectionDetailDialog(
    card: CollectionCardUi,
    onEquip: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceLow,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(card.nameRes),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(spriteRes(card.artIndex)),
                    contentDescription = stringResource(card.nameRes),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(132.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(card.flavorRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(
                        R.string.collection_detail_meta,
                        stringResource(itemKindRes(card.kind)),
                        stringResource(rarityRes(card.rarity)),
                        stringResource(card.requirementRes)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = Secondary,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            if (card.kind == ItemKind.LIVERY && !card.equipped) {
                TextButton(onClick = { onEquip(card.id) }) {
                    Text(
                        text = stringResource(R.string.action_equip),
                        style = MaterialTheme.typography.labelLarge,
                        color = Accent
                    )
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.action_close),
                        style = MaterialTheme.typography.labelLarge,
                        color = Accent
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_close),
                    style = MaterialTheme.typography.labelLarge,
                    color = Secondary
                )
            }
        }
    )
}
