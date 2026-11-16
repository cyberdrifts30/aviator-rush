package com.Qempibo9.Pebfik.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Qempibo9.Pebfik.core.config.GameConfig
import com.Qempibo9.Pebfik.domain.model.FlightOutcome
import com.Qempibo9.Pebfik.domain.model.FlightPlan
import com.Qempibo9.Pebfik.domain.repository.GameCatalog
import com.Qempibo9.Pebfik.domain.repository.ProgressRepository
import com.Qempibo9.Pebfik.domain.repository.SettingsRepository
import com.Qempibo9.Pebfik.domain.usecase.ApplyFlightResultUseCase
import com.Qempibo9.Pebfik.domain.usecase.CreateFlightPlanUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

sealed interface GameEvent {
    data class Finished(val secured: Boolean) : GameEvent
    data object Abandoned : GameEvent
}

class GameViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val catalog: GameCatalog,
    private val createFlightPlan: CreateFlightPlanUseCase,
    private val applyFlightResult: ApplyFlightResultUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val events = Channel<GameEvent>(Channel.BUFFERED)
    val eventFlow: Flow<GameEvent> = events.receiveAsFlow()

    private var loopJob: Job? = null
    private var resolveJob: Job? = null
    private var plan: FlightPlan = createFlightPlan(0, 0)
    private var elapsedMs = 0L
    private var crateShownAtMs = -1L
    private var nextCrateIndex = 0
    private var collectedCrates = 0
    private var boostUses = 0
    private var tickCounter = 0

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _state.update { current ->
                    current.copy(
                        autoSecureIndex = if (current.phase == FlightPhase.IDLE) {
                            settings.autoSecureIndex
                        } else {
                            current.autoSecureIndex
                        },
                        reducedMotion = settings.reducedMotion,
                        altitudeUnit = settings.altitudeUnit
                    )
                }
            }
        }
        viewModelScope.launch {
            progressRepository.state.collect { progress ->
                if (_state.value.phase != FlightPhase.IDLE) return@collect
                val stage = catalog.stages.getOrNull(progress.stageIndex) ?: catalog.stages.last()
                _state.update {
                    it.copy(
                        fuel = progress.fuel,
                        streak = progress.streak,
                        miles = progress.miles,
                        flightNumber = progress.flights + 1,
                        stageNumber = min(progress.stageIndex + 1, GameConfig.TOTAL_STAGES),
                        stageKind = stage.kind,
                        stageTarget = stage.target,
                        stageProgress = progress.stageProgress,
                        showHint = progress.flights < GameConfig.HINT_FLIGHTS
                    )
                }
            }
        }
    }

    private fun initialState(): GameUiState {
        val progress = progressRepository.state.value
        val settings = settingsRepository.settings.value
        val stage = catalog.stages.getOrNull(progress.stageIndex) ?: catalog.stages.last()
        return GameUiState(
            fuel = progress.fuel,
            streak = progress.streak,
            miles = progress.miles,
            autoSecureIndex = settings.autoSecureIndex,
            flightNumber = progress.flights + 1,
            stageNumber = min(progress.stageIndex + 1, GameConfig.TOTAL_STAGES),
            stageKind = stage.kind,
            stageTarget = stage.target,
            stageProgress = progress.stageProgress,
            showHint = progress.flights < GameConfig.HINT_FLIGHTS,
            reducedMotion = settings.reducedMotion,
            altitudeUnit = settings.altitudeUnit
        )
    }

    fun onPrimaryAction() {
        when (_state.value.phase) {
            FlightPhase.IDLE -> startFlight()
            FlightPhase.FLYING -> secure(ceiling = false)
            else -> Unit
        }
    }

    fun onAutoSecureSelected(index: Int) {
        if (_state.value.phase != FlightPhase.IDLE) return
        _state.update { it.copy(autoSecureIndex = index) }
    }

    fun onBoostToggled() {
        val current = _state.value
        if (current.phase != FlightPhase.FLYING) return
        if (!current.boosting && current.fuel <= 0f) return
        if (!current.boosting) boostUses++
        _state.update { it.copy(boosting = !it.boosting) }
    }

    fun onCrateTapped(id: Int) {
        val current = _state.value
        if (current.phase != FlightPhase.FLYING) return
        if (current.activeCrate?.id != id) return
        collectedCrates++
        crateShownAtMs = -1L
        _state.update { it.copy(activeCrate = null, crates = it.crates + 1) }
    }

    fun pauseFlight() {
        if (_state.value.phase != FlightPhase.FLYING) return
        loopJob?.cancel()
        loopJob = null
        _state.update { it.copy(phase = FlightPhase.PAUSED, boosting = false) }
    }

    fun resumeFlight() {
        if (_state.value.phase != FlightPhase.PAUSED) return
        _state.update { it.copy(phase = FlightPhase.FLYING) }
        startLoop()
    }

    fun abandonFlight() {
        loopJob?.cancel()
        loopJob = null
        resolveJob?.cancel()
        resolveJob = null
        _state.update {
            it.copy(
                phase = FlightPhase.IDLE,
                multiplier = 1f,
                altitudeM = 0,
                boosting = false,
                crates = 0,
                trail = emptyList(),
                activeCrate = null
            )
        }
        viewModelScope.launch { events.send(GameEvent.Abandoned) }
    }

    private fun startFlight() {
        val progress = progressRepository.state.value
        plan = createFlightPlan(progress.stageIndex, progress.flights)
        elapsedMs = 0L
        crateShownAtMs = -1L
        nextCrateIndex = 0
        collectedCrates = 0
        boostUses = 0
        tickCounter = 0
        _state.update {
            it.copy(
                phase = FlightPhase.FLYING,
                multiplier = 1f,
                altitudeM = 0,
                fuel = progress.fuel,
                boosting = false,
                crates = 0,
                trail = listOf(0f),
                activeCrate = null,
                cloudOffsetA = plan.cloudOffsetA,
                cloudOffsetB = plan.cloudOffsetB,
                seed = plan.seed
            )
        }
        startLoop()
    }

    private fun startLoop() {
        loopJob?.cancel()
        loopJob = viewModelScope.launch {
            while (isActive && _state.value.phase == FlightPhase.FLYING) {
                delay(GameConfig.TICK_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val current = _state.value
        if (current.phase != FlightPhase.FLYING) return
        elapsedMs += GameConfig.TICK_MS
        tickCounter++

        val boosting = current.boosting && current.fuel > 0f
        val fuel = if (boosting) {
            (current.fuel - GameConfig.FUEL_DRAIN).coerceAtLeast(0f)
        } else {
            current.fuel
        }
        val factor = if (boosting) GameConfig.BOOST_FACTOR else 1f
        val multiplier = current.multiplier * (1f + GameConfig.CLIMB_RATE * factor)
        val altitude = ((multiplier - 1f) * 1000f).roundToInt()

        val trail = if (tickCounter % TRAIL_EVERY == 0) {
            (current.trail + (multiplier - 1f)).takeLast(GameConfig.TRAIL_SAMPLES)
        } else {
            current.trail
        }

        var crate = current.activeCrate
        if (crate != null && elapsedMs - crateShownAtMs > GameConfig.CRATE_LIFETIME_MS) {
            crate = null
            crateShownAtMs = -1L
        }
        if (crate == null && nextCrateIndex < plan.crates.size) {
            val drop = plan.crates[nextCrateIndex]
            if (multiplier >= drop.triggerMultiplier) {
                nextCrateIndex++
                crateShownAtMs = elapsedMs
                crate = CrateUi(drop.id, drop.horizontalFraction, drop.verticalFraction)
            }
        }

        _state.update {
            it.copy(
                multiplier = multiplier,
                altitudeM = altitude,
                fuel = fuel,
                boosting = boosting,
                trail = trail,
                activeCrate = crate
            )
        }

        val autoTarget = GameConfig.AUTO_SECURE_STEPS.getOrElse(current.autoSecureIndex) { 0f }
        if (autoTarget > 0f && multiplier >= autoTarget) {
            secure(ceiling = false)
            return
        }
        if (elapsedMs >= GameConfig.MAX_FLIGHT_MS) {
            secure(ceiling = true)
            return
        }
        if (elapsedMs >= GameConfig.MIN_FLIGHT_MS && multiplier >= plan.stallMultiplier) {
            stall()
        }
    }

    private fun secure(ceiling: Boolean) {
        loopJob?.cancel()
        loopJob = null
        _state.update { it.copy(phase = FlightPhase.SECURING, boosting = false) }
        resolveJob = viewModelScope.launch {
            delay(GameConfig.SECURE_ANIM_MS)
            finish(secured = true, ceiling = ceiling)
        }
    }

    private fun stall() {
        loopJob?.cancel()
        loopJob = null
        _state.update { it.copy(phase = FlightPhase.STALLING, boosting = false, activeCrate = null) }
        resolveJob = viewModelScope.launch {
            delay(GameConfig.STALL_ANIM_MS)
            finish(secured = false, ceiling = false)
        }
    }

    private fun finish(secured: Boolean, ceiling: Boolean) {
        val current = _state.value
        val outcome = FlightOutcome(
            secured = secured,
            multiplier = current.multiplier,
            crates = collectedCrates,
            boosts = boostUses,
            ceilingReached = ceiling
        )
        progressRepository.update { progress ->
            applyFlightResult(progress.copy(fuel = current.fuel.coerceAtLeast(0f)), outcome)
        }
        val progress = progressRepository.state.value
        val stage = catalog.stages.getOrNull(progress.stageIndex) ?: catalog.stages.last()
        _state.update {
            it.copy(
                phase = FlightPhase.IDLE,
                fuel = progress.fuel,
                streak = progress.streak,
                miles = progress.miles,
                flightNumber = progress.flights + 1,
                stageNumber = min(progress.stageIndex + 1, GameConfig.TOTAL_STAGES),
                stageKind = stage.kind,
                stageTarget = stage.target,
                stageProgress = progress.stageProgress,
                showHint = progress.flights < GameConfig.HINT_FLIGHTS,
                activeCrate = null
            )
        }
        viewModelScope.launch { events.send(GameEvent.Finished(secured)) }
    }

    override fun onCleared() {
        loopJob?.cancel()
        resolveJob?.cancel()
        super.onCleared()
    }

    private companion object {
        const val TRAIL_EVERY = 3
    }
}
