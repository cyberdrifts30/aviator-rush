package com.Qempibo9.Pebfik.core.di

import android.content.Context
import com.Qempibo9.Pebfik.data.local.PreferencesStorage
import com.Qempibo9.Pebfik.data.repository.ProgressRepositoryImpl
import com.Qempibo9.Pebfik.data.repository.SettingsRepositoryImpl
import com.Qempibo9.Pebfik.data.sample.SampleData
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository

object ServiceLocator {

    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var factory: AppViewModelFactory? = null

    fun init(context: Context) {
        if (factory != null) return
        val storage = PreferencesStorage(context)
        val progressRepository = ProgressRepositoryImpl(storage)
        val settingsRepository = SettingsRepositoryImpl(storage)
        progress = progressRepository
        settings = settingsRepository
        factory = AppViewModelFactory(progressRepository, settingsRepository, SampleData)
    }

    val catalog: GameCatalog get() = SampleData

    val progressRepository: ProgressRepository
        get() = requireNotNull(progress) { INIT_MESSAGE }

    val settingsRepository: SettingsRepository
        get() = requireNotNull(settings) { INIT_MESSAGE }

    val viewModelFactory: AppViewModelFactory
        get() = requireNotNull(factory) { INIT_MESSAGE }

    private const val INIT_MESSAGE = "ServiceLocator.init was not called"
}
