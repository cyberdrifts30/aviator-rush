package com.Qempibo9.Pebfik.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.Qempibo9.Pebfik.core.theme.Secondary
import com.Qempibo9.Pebfik.core.theme.SurfaceHigh
import com.Qempibo9.Pebfik.core.theme.TextPrimary
import com.Qempibo9.Pebfik.core.theme.TextSecondary
import com.Qempibo9.Pebfik.core.ui.GlassPanel
import com.Qempibo9.Pebfik.core.ui.PrimaryButton
import com.Qempibo9.Pebfik.core.ui.ScreenBackground
import com.Qempibo9.Pebfik.core.ui.SecondaryButton

private data class OnboardingStep(
    val titleRes: Int,
    val bodyRes: Int,
    val artRes: Int,
    val artSize: Int
)

private val Steps = listOf(
    OnboardingStep(R.string.onboarding_title_1, R.string.onboarding_body_1, R.drawable.sprite_1, 150),
    OnboardingStep(R.string.onboarding_title_2, R.string.onboarding_body_2, R.drawable.sprite_2, 132),
    OnboardingStep(R.string.onboarding_title_3, R.string.onboarding_body_3, R.drawable.sprite_4, 132)
)

@Composable
fun OnboardingScreen(
    onDone: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: OnboardingViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    OnboardingContent(
        state = state,
        onNext = {
            if (state.isLast) {
                viewModel.markSeen()
                onDone()
            } else {
                viewModel.nextStep()
            }
        },
        onMenu = {
            viewModel.markSeen()
            onMenu()
        },
        modifier = modifier
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    onNext: () -> Unit,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenBackground(
        imageAlpha = 0.5f,
        overlay = listOf(
            BgBase.copy(alpha = 0.9f),
            Color(0xFF0E1930).copy(alpha = 0.92f),
            Color(0xFF0A1120).copy(alpha = 0.96f)
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.onboarding_header),
                style = MaterialTheme.typography.labelSmall,
                color = Secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            StepCard(state = state, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))
            StepDots(state = state)
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryButton(
                textRes = if (state.isLast) R.string.onboarding_start else R.string.onboarding_next,
                onClick = onNext,
                height = 60.dp
            )
            Spacer(modifier = Modifier.height(10.dp))
            SecondaryButton(
                textRes = R.string.menu,
                onClick = onMenu,
                leadingIcon = R.drawable.icon_home
            )
        }
    }
}

@Composable
private fun StepCard(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    GlassPanel(modifier = modifier.fillMaxWidth(), cornerRadius = 28.dp) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = {
                (slideInHorizontally { width -> width / 3 } + fadeIn(tween(300))) togetherWith
                    (slideOutHorizontally { width -> -width / 3 } + fadeOut(tween(300)))
            },
            label = ONBOARDING_ANIM,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            val step = Steps[index.coerceIn(Steps.indices)]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(24.dp)
            ) {
                Image(
                    painter = painterResource(step.artRes),
                    contentDescription = stringResource(step.titleRes),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(step.artSize.dp)
                )
                Spacer(modifier = Modifier.height(22.dp))
                Text(
                    text = stringResource(step.titleRes),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(step.bodyRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepDots(
    state: OnboardingUiState,
    modifier: Modifier = Modifier
) {
    val description = stringResource(
        R.string.onboarding_step_state,
        state.step + 1,
        state.totalSteps
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.semantics { stateDescription = description }
    ) {
        repeat(state.totalSteps) { index ->
            val active = index == state.step
            val width by animateDpAsState(
                targetValue = if (active) 22.dp else 8.dp,
                label = DOT_ANIM
            )
            Box(
                modifier = Modifier
                    .width(width)
                    .height(8.dp)
                    .background(
                        if (active) Accent else SurfaceHigh,
                        RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

private const val ONBOARDING_ANIM = "onboarding-step"
private const val DOT_ANIM = "onboarding-dot"
