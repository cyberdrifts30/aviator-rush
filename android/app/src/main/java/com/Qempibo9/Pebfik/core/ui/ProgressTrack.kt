package com.Qempibo9.Pebfik.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh

@Composable
fun ProgressTrack(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color = SurfaceHigh,
    fillColors: List<Color> = listOf(Secondary, Secondary),
    animated: Boolean = true
) {
    val target = progress.coerceIn(0f, 1f)
    val animatedValue by animateFloatAsState(targetValue = target, label = TRACK_LABEL)
    val value = if (animated) animatedValue else target
    val shape = RoundedCornerShape(height / 2)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(trackColor, shape)
    ) {
        if (value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(value)
                    .background(Brush.horizontalGradient(fillColors), shape)
            )
        }
    }
}

private const val TRACK_LABEL = "progress-track"
