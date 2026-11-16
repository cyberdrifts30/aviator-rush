package com.Qempibo9.Pebfik.core.ui

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.util.groupNumber

@Composable
fun MilesBadge(
    miles: Int,
    modifier: Modifier = Modifier,
    animate: Boolean = true
) {
    val animatedMiles by animateIntAsState(targetValue = miles, label = MILES_LABEL)
    val shown = if (animate) animatedMiles else miles
    val formatted = groupNumber(shown)
    val description = stringResource(R.string.miles_description, formatted)
    val shape = RoundedCornerShape(18.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .defaultMinSize(minWidth = 104.dp)
            .height(36.dp)
            .background(SurfaceHigh.copy(alpha = 0.82f), shape)
            .border(1.dp, Accent.copy(alpha = 0.4f), shape)
            .padding(horizontal = 12.dp)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = description
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_star),
            contentDescription = null,
            tint = Accent,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = formatted,
            style = MaterialTheme.typography.titleMedium,
            color = Accent,
            maxLines = 1
        )
    }
}

private const val MILES_LABEL = "miles-badge"
