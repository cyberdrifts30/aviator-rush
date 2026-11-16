package com.Qempibo9.Pebfik.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import com.Qempibo9.Pebfik.domain.usecase.ApplyFlightResultUseCase
import com.Qempibo9.Pebfik.domain.usecase.ClaimGoalUseCase
import com.Qempibo9.Pebfik.domain.usecase.ClaimMilestoneUseCase
import com.Qempibo9.Pebfik.domain.usecase.CreateFlightPlanUseCase
import com.Qempibo9.Pebfik.domain.usecase.EquipLiveryUseCase
import com.Qempibo9.Pebfik.domain.usecase.RefreshDailyGoalsUseCase
import com.Qempibo9.Pebfik.domain.usecase.ResolveRankUseCase
import com.Qempibo9.Pebfik.domain.usecase.SecureFlightUseCase
import com.Qempibo9.Pebfik.presentation.app.AppViewModel
import com.Qempibo9.Pebfik.presentation.collection.CollectionViewModel
import com.Qempibo9.Pebfik.presentation.game.GameViewModel
import com.Qempibo9.Pebfik.presentation.gameover.GameOverViewModel
import com.Qempibo9.Pebfik.presentation.goals.DailyGoalsViewModel
import com.Qempibo9.Pebfik.presentation.menu.MenuViewModel
import com.Qempibo9.Pebfik.presentation.onboarding.OnboardingViewModel
import com.Qempibo9.Pebfik.presentation.profile.ProfileViewModel
import com.Qempibo9.Pebfik.presentation.progress.ProgressMapViewModel
import com.Qempibo9.Pebfik.presentation.rewards.RewardsViewModel
import com.Qempibo9.Pebfik.presentation.settings.SettingsViewModel
import com.Qempibo9.Pebfik.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val catalog: GameCatalog
) : ViewModelProvider.Factory {

    private val resolveRank = ResolveRankUseCase()
    private val secureFlight = SecureFlightUseCase()
    private val createFlightPlan = CreateFlightPlanUseCase()
    private val applyFlightResult = ApplyFlightResultUseCase(catalog, secureFlight)
    private val refreshDailyGoals = RefreshDailyGoalsUseCase(catalog)
    private val claimGoal = ClaimGoalUseCase(catalog)
    private val claimMilestone = ClaimMilestoneUseCase(catalog)
    private val equipLivery = EquipLiveryUseCase(catalog)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) ->
                SplashViewModel(settings)

            modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
                OnboardingViewModel(progress)

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(progress, settings, catalog, resolveRank, refreshDailyGoals)

            modelClass.isAssignableFrom(GameViewModel::class.java) ->
                GameViewModel(progress, settings, catalog, createFlightPlan, applyFlightResult)

            modelClass.isAssignableFrom(GameOverViewModel::class.java) ->
                GameOverViewModel(progress, settings, catalog)

            modelClass.isAssignableFrom(ProgressMapViewModel::class.java) ->
                ProgressMapViewModel(progress, settings, catalog)

            modelClass.isAssignableFrom(DailyGoalsViewModel::class.java) ->
                DailyGoalsViewModel(progress, catalog, refreshDailyGoals, claimGoal)

            modelClass.isAssignableFrom(CollectionViewModel::class.java) ->
                CollectionViewModel(progress, settings, catalog, equipLivery)

            modelClass.isAssignableFrom(RewardsViewModel::class.java) ->
                RewardsViewModel(progress, catalog, claimMilestone)

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(progress, catalog, resolveRank)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settings, progress)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        @Suppress("UNCHECKED_CAST")
        return created as T
    }
}
