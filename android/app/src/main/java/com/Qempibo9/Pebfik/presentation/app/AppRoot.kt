package com.Qempibo9.Pebfik.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Qempibo9.Pebfik.core.di.ServiceLocator
import com.Qempibo9.Pebfik.core.navigation.Screen
import com.Qempibo9.Pebfik.core.theme.BgBase
import com.Qempibo9.Pebfik.presentation.collection.CollectionScreen
import com.Qempibo9.Pebfik.presentation.game.GameScreen
import com.Qempibo9.Pebfik.presentation.gameover.GameOverScreen
import com.Qempibo9.Pebfik.presentation.goals.DailyGoalsScreen
import com.Qempibo9.Pebfik.presentation.menu.MenuScreen
import com.Qempibo9.Pebfik.presentation.onboarding.OnboardingScreen
import com.Qempibo9.Pebfik.presentation.profile.ProfileScreen
import com.Qempibo9.Pebfik.presentation.progress.ProgressMapScreen
import com.Qempibo9.Pebfik.presentation.rewards.RewardsScreen
import com.Qempibo9.Pebfik.presentation.settings.SettingsScreen
import com.Qempibo9.Pebfik.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = ServiceLocator.viewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    Box(modifier = Modifier.fillMaxSize().background(BgBase)) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = {
                when (targetState) {
                    is Screen.Game ->
                        (slideInVertically { height -> height / 6 } + fadeIn(tween(320))) togetherWith
                            fadeOut(tween(220))

                    is Screen.GameOver ->
                        (scaleIn(initialScale = 0.94f) + fadeIn(tween(360))) togetherWith
                            fadeOut(tween(240))

                    else -> fadeIn(tween(420)) togetherWith fadeOut(tween(280))
                }
            },
            label = APP_ROOT_ANIM,
            modifier = Modifier.fillMaxSize()
        ) { current ->
            when (current) {
                Screen.Splash -> SplashScreen(
                    onFinished = appViewModel::onSplashFinished
                )

                Screen.Onboarding -> OnboardingScreen(
                    onDone = appViewModel::openMenu,
                    onMenu = appViewModel::openMenu
                )

                Screen.Menu -> MenuScreen(
                    onPlay = { appViewModel.navigate(Screen.Game) },
                    onProgressMap = { appViewModel.navigate(Screen.ProgressMap) },
                    onDailyGoals = { appViewModel.navigate(Screen.DailyGoals) },
                    onCollection = { appViewModel.navigate(Screen.Collection) },
                    onRewards = { appViewModel.navigate(Screen.Rewards) },
                    onProfile = { appViewModel.navigate(Screen.Profile) },
                    onSettings = { appViewModel.navigate(Screen.Settings) },
                    onHowToPlay = { appViewModel.navigate(Screen.Onboarding) }
                )

                Screen.Game -> GameScreen(
                    onGameOver = { secured ->
                        appViewModel.navigateReplacing(Screen.GameOver(secured))
                    },
                    onMenu = appViewModel::openMenu
                )

                is Screen.GameOver -> GameOverScreen(
                    onPlayAgain = { appViewModel.navigateReplacing(Screen.Game) },
                    onMenu = appViewModel::openMenu
                )

                Screen.ProgressMap -> ProgressMapScreen(onMenu = appViewModel::openMenu)

                Screen.DailyGoals -> DailyGoalsScreen(onMenu = appViewModel::openMenu)

                Screen.Collection -> CollectionScreen(onMenu = appViewModel::openMenu)

                Screen.Rewards -> RewardsScreen(onMenu = appViewModel::openMenu)

                Screen.Profile -> ProfileScreen(onMenu = appViewModel::openMenu)

                Screen.Settings -> SettingsScreen(onMenu = appViewModel::openMenu)
            }
        }
    }
}

private const val APP_ROOT_ANIM = "app-root"
