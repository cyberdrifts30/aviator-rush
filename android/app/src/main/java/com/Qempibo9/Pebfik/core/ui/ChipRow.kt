package com.Qempibo9.Pebfik.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary

@Composable
fun ChipRow(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    chipHeight: Int = 48
) {
    val disabledLabel = stringResource(R.string.state_disabled_in_flight)
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.42f)
            .then(
                if (enabled) Modifier else Modifier.semantics { stateDescription = disabledLabel }
            )
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val shape = RoundedCornerShape(12.dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(chipHeight.dp)
                    .background(
                        if (selected) Primary.copy(alpha = 0.32f) else SurfaceHigh.copy(alpha = 0.6f),
                        shape
                    )
                    .border(
                        1.dp,
                        if (selected) Secondary else Secondary.copy(alpha = 0.16f),
                        shape
                    )
                    .clickable(enabled = enabled) { onSelect(index) }
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) TextPrimary else TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
