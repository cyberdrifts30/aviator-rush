package com.Qempibo9.Pebfik.core.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.theme.Primary
import com.Qempibo9.Pebfik.core.theme.Secondary

data class AmbientGlow(
    val color: Color,
    val anchorX: Float,
    val anchorY: Float,
    val radiusFraction: Float
)

@Composable
fun ScreenBackground(
    imageAlpha: Float,
    overlay: List<Color>,
    modifier: Modifier = Modifier,
    glows: List<AmbientGlow> = DefaultGlows,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(overlay.first())) {
        Image(
            painter = painterResource(R.drawable.bg_game),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().alpha(imageAlpha)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(overlay))
                .drawBehind {
                    glows.forEach { glow ->
                        val radius = size.minDimension * glow.radiusFraction
                        val center = Offset(size.width * glow.anchorX, size.height * glow.anchorY)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(glow.color, Color.Transparent),
                                center = center,
                                radius = radius
                            ),
                            radius = radius,
                            center = center
                        )
                    }
                }
        )
        content()
    }
}

val DefaultGlows = listOf(
    AmbientGlow(Primary.copy(alpha = 0.22f), 0.26f, 0.3f, 0.78f),
    AmbientGlow(Secondary.copy(alpha = 0.12f), 0.84f, 0.76f, 0.6f)
)
