package com.Qempibo9.Pebfik.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.R
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.theme.Accent
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.core.theme.BgDeep
import com.Qempibo9.Pebfik.core.theme.BgMid
import com.Qempibo9.Pebfik.core.theme.CloudTint
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.TextMuted
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.ui.AmbientGlow
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import kotlin.random.Random

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: SplashViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val finished by viewModel.finished.collectAsStateWithLifecycle()
    val reducedMotion by viewModel.reducedMotion.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashContent(reducedMotion = reducedMotion, modifier = modifier)
}

@Composable
private fun SplashContent(
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = SPLASH_TRANSITION)
    val twinkle by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = SPLASH_TWINKLE
    )
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = SPLASH_DRIFT
    )
    val float by transition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = SPLASH_FLOAT
    )

    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) {
            Triple(random.nextFloat(), random.nextFloat(), 0.8f + random.nextFloat() * 1.8f)
        }
    }

    val activeTwinkle = if (reducedMotion) 0.75f else twinkle
    val activeDrift = if (reducedMotion) 0.25f else drift
    val activeFloat = if (reducedMotion) 0f else float

    ScreenBackground(
        imageAlpha = 0.35f,
        overlay = listOf(
            BgDeep.copy(alpha = 0.97f),
            BgMid.copy(alpha = 0.94f),
            BgBase.copy(alpha = 0.98f)
        ),
        glows = listOf(AmbientGlow(Accent.copy(alpha = 0.1f), 0.5f, 0.38f, 0.75f)),
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            stars.forEach { star ->
                drawCircle(
                    color = Color.White.copy(alpha = 0.22f + activeTwinkle * 0.45f),
                    radius = star.third,
                    center = Offset(size.width * star.first, size.height * star.second)
                )
            }
            val bandOne = (size.width * activeDrift) - size.width * 0.3f
            val bandTwo = size.width * 1.1f - (size.width * activeDrift) * 1.4f
            drawCircle(
                color = CloudTint.copy(alpha = 0.26f),
                radius = size.width * 0.38f,
                center = Offset(bandOne, size.height * 0.74f)
            )
            drawCircle(
                color = CloudTint.copy(alpha = 0.18f),
                radius = size.width * 0.3f,
                center = Offset(bandTwo, size.height * 0.2f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 32.dp)
        ) {
            GlassPanel(
                cornerRadius = 32.dp,
                borderColor = Secondary.copy(alpha = 0.28f),
                elevation = 24.dp,
                modifier = Modifier
                    .size(168.dp)
                    .offset(y = activeFloat.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.app_title),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(132.dp)
                )
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(
                modifier = Modifier
                    .width(96.dp)
                    .height(2.dp)
                    .background(Accent, RoundedCornerShape(1.dp))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.splash_subtitle),
                style = MaterialTheme.typography.labelSmall,
                color = Secondary.copy(alpha = 0.82f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(34.dp))
            LinearProgressIndicator(
                color = Secondary,
                trackColor = SurfaceHigh,
                modifier = Modifier
                    .width(196.dp)
                    .height(4.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.splash_loading),
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().alpha(0.9f)
            )
        }
    }
}

private const val SPLASH_TRANSITION = "splash-transition"
private const val SPLASH_TWINKLE = "splash-twinkle"
private const val SPLASH_DRIFT = "splash-drift"
private const val SPLASH_FLOAT = "splash-float"
private const val STAR_COUNT = 46
private const val STAR_SEED = 20261116
