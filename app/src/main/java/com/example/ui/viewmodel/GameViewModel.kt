package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entity.LevelHistory
import com.example.data.entity.PlayerProgress
import com.example.data.repository.GameRepository
import com.example.ui.audio.SoundPlayer
import com.example.ui.model.VeggieItem
import com.example.ui.model.VeggieType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.sqrt
import kotlin.random.Random

class GameViewModel(private val repository: GameRepository) : ViewModel() {

    // Persistent player progress from database
    val progress: StateFlow<PlayerProgress?> = repository.playerProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val history: StateFlow<List<LevelHistory>> = repository.levelHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Game Screen states
    private val _veggiePile = MutableStateFlow<List<VeggieItem>>(emptyList())
    val veggiePile: StateFlow<List<VeggieItem>> = _veggiePile.asStateFlow()

    private val _slots = MutableStateFlow<List<VeggieItem>>(emptyList())
    val slots: StateFlow<List<VeggieItem>> = _slots.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _maxTimerSeconds = MutableStateFlow(120)
    val maxTimerSeconds: StateFlow<Int> = _maxTimerSeconds.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _levelCleared = MutableStateFlow(false)
    val levelCleared: StateFlow<Boolean> = _levelCleared.asStateFlow()

    private val _levelFailed = MutableStateFlow(false)
    val levelFailed: StateFlow<Boolean> = _levelFailed.asStateFlow()

    private val _isGameActive = MutableStateFlow(false)
    val isGameActive: StateFlow<Boolean> = _isGameActive.asStateFlow()

    // Overlay settings
    private val overlapRadius = 0.16f // 16% of relative game board area

    // Timer Job
    private var timerJob: Job? = null
    private val _powerFreezeActive = MutableStateFlow(false)
    val powerFreezeActive: StateFlow<Boolean> = _powerFreezeActive.asStateFlow()

    private val _freezeTimerSeconds = MutableStateFlow(0)
    val freezeTimerSeconds: StateFlow<Int> = _freezeTimerSeconds.asStateFlow()

    // Dialog state management
    private val _isShopShowing = MutableStateFlow(false)
    val isShopShowing: StateFlow<Boolean> = _isShopShowing.asStateFlow()

    private val _isProfileShowing = MutableStateFlow(false)
    val isProfileShowing: StateFlow<Boolean> = _isProfileShowing.asStateFlow()

    private val _isSettingsShowing = MutableStateFlow(false)
    val isSettingsShowing: StateFlow<Boolean> = _isSettingsShowing.asStateFlow()

    private val _isDailyHarvestShowing = MutableStateFlow(false)
    val isDailyHarvestShowing: StateFlow<Boolean> = _isDailyHarvestShowing.asStateFlow()

    private val _isDailyHarvestAvailable = MutableStateFlow(false)
    val isDailyHarvestAvailable: StateFlow<Boolean> = _isDailyHarvestAvailable.asStateFlow()

    private val _isDailyQuestsShowing = MutableStateFlow(false)
    val isDailyQuestsShowing: StateFlow<Boolean> = _isDailyQuestsShowing.asStateFlow()

    // Keep history of slots actions for Unlimited/Cost-based Undo
    private val selectionHistory = mutableListOf<VeggieItem>()

    private val _levelFailedReason = MutableStateFlow<String?>(null)
    val levelFailedReason: StateFlow<String?> = _levelFailedReason.asStateFlow()

    private val _energyTimerSeconds = MutableStateFlow(0)
    val energyTimerSeconds: StateFlow<Int> = _energyTimerSeconds.asStateFlow()

    // Combo and Floating Match States
    private val _comboCount = MutableStateFlow(0)
    val comboCount: StateFlow<Int> = _comboCount.asStateFlow()

    private val _comboMultiplier = MutableStateFlow(1.0f)
    val comboMultiplier: StateFlow<Float> = _comboMultiplier.asStateFlow()

    private val _comboRemainingPercent = MutableStateFlow(0.0f)
    val comboRemainingPercent: StateFlow<Float> = _comboRemainingPercent.asStateFlow()

    private val _floatingMatches = MutableStateFlow<List<FloatingMatchItem>>(emptyList())
    val floatingMatches: StateFlow<List<FloatingMatchItem>> = _floatingMatches.asStateFlow()

    private var lastMatchTime = 0L
    private val comboWindowMs = 5000L // 5 seconds combo window
    private var activeComboWindowMs = 5000L
    private var comboTimerJob: Job? = null
    private var pendingMatchJob: Job? = null

    // Progression and Power-up states
    private val _requiredScore = MutableStateFlow(400)
    val requiredScore: StateFlow<Int> = _requiredScore.asStateFlow()

    private val _boardGridSize = MutableStateFlow(3)
    val boardGridSize: StateFlow<Int> = _boardGridSize.asStateFlow()

    private val _specialVeggieLaserRow = MutableStateFlow<Float?>(null)
    val specialVeggieLaserRow: StateFlow<Float?> = _specialVeggieLaserRow.asStateFlow()

    private val _specialVeggieLaserCol = MutableStateFlow<Float?>(null)
    val specialVeggieLaserCol: StateFlow<Float?> = _specialVeggieLaserCol.asStateFlow()

    init {
        // Create initial progress if not exists in database
        viewModelScope.launch {
            delay(300) // wait for database check
            val current = repository.getProgressDirect()
            if (current == null) {
                val initial = PlayerProgress()
                repository.saveProgress(initial)
                checkDailyRewards(initial)
            } else {
                SoundPlayer.soundVolume = current.soundVolume
                SoundPlayer.musicVolume = current.musicVolume
                SoundPlayer.isMuted = current.isMuted
                checkDailyRewards(current)
            }

            // Initial energy check and regeneration calculation on app startup
            checkAndRegenerateEnergy()

            // Periodically check and regenerate energy every 1 second
            launch {
                while (true) {
                    delay(1000)
                    checkAndRegenerateEnergy()
                }
            }
        }

        // Reactively sync volume and mute changes with SoundPlayer
        viewModelScope.launch {
            progress.collect { prog ->
                if (prog != null) {
                    SoundPlayer.soundVolume = prog.soundVolume
                    SoundPlayer.musicVolume = prog.musicVolume
                    SoundPlayer.isMuted = prog.isMuted
                }
            }
        }
    }

    fun showShop(show: Boolean) { _isShopShowing.value = show }
    fun showProfile(show: Boolean) { _isProfileShowing.value = show }
    fun showSettings(show: Boolean) { _isSettingsShowing.value = show }
    fun showDailyHarvest(show: Boolean) { _isDailyHarvestShowing.value = show }
    fun showDailyQuests(show: Boolean) { _isDailyQuestsShowing.value = show }

    fun startLevel(levelNumber: Int) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: PlayerProgress()
            if (prog.energy <= 0) {
                // No energy left! Refill prompt or simple pop
                SoundPlayer.playError()
                return@launch
            }

            // Consume 1 energy and trigger the timer if energy was full
            val wasFull = prog.energy >= prog.maxEnergy
            val newRefillTime = if (wasFull) System.currentTimeMillis() else prog.lastEnergyRefillTime
            repository.saveProgress(
                prog.copy(
                    energy = (prog.energy - 1).coerceAtLeast(0),
                    lastEnergyRefillTime = newRefillTime
                )
            )
            incrementQuestProgress(3, 1)

            _score.value = 0
            _levelCleared.value = false
            _levelFailed.value = false
            _levelFailedReason.value = null
            _slots.value = emptyList()
            selectionHistory.clear()
            _powerFreezeActive.value = false
            _freezeTimerSeconds.value = 0

            // Reset combo and floating matches
            _comboCount.value = 0
            _comboMultiplier.value = 1.0f
            _comboRemainingPercent.value = 0.0f
            activeComboWindowMs = 5000L
            _floatingMatches.value = emptyList()
            lastMatchTime = 0L
            comboTimerJob?.cancel()

            // Choose duration
            val duration = when (levelNumber) {
                1 -> 120
                2 -> 150
                3 -> 180
                else -> 200
            }
            _maxTimerSeconds.value = duration
            _timerSeconds.value = duration

            // Generate levels
            generateLevelBoard(levelNumber)

            _isGameActive.value = true
            startTimer()
        }
    }

    private fun generateLevelBoard(level: Int) {
        // Required score rises linearly: Level 1 -> 300, Level 8 -> 1000, etc.
        val required = 300 + (level - 1) * 100
        _requiredScore.value = required
        
        // Grid dimension increases every 3 levels (3x3, 4x4, 5x5, 6x6...)
        val gridDim = 3 + (level - 1) / 3
        _boardGridSize.value = gridDim

        // Determine difficulty variables
        val typesCount = when (level) {
            1, 2, 3 -> 3
            4, 5, 6 -> 4
            7, 8, 9 -> 5
            else -> (level + 2).coerceAtMost(VeggieType.entries.size)
        }

        val triplesCount = required / 100

        // Selected veggie types for this level
        val shuffledTypes = VeggieType.entries.shuffled()
        val levelTypes = shuffledTypes.take(typesCount)

        val itemsList = mutableListOf<VeggieItem>()
        var depthIndex = 0

        // Create virtual grid cell coordinates to distribute vegetables
        val cells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until gridDim) {
            for (c in 0 until gridDim) {
                cells.add(Pair(r, c))
            }
        }
        cells.shuffle()

        // Distribute triples among types
        for (i in 0 until triplesCount) {
            val type = levelTypes[i % typesCount]
            // Generate 3 items for this type
            for (j in 0..2) {
                val cellIndex = depthIndex % cells.size
                val (r, c) = cells[cellIndex]

                val cellWidth = 0.74f / gridDim
                val cellHeight = 0.74f / gridDim
                
                val baseMinX = 0.10f + c * cellWidth
                val baseMinY = 0.10f + r * cellHeight
                
                val jitterX = Random.nextFloat() * 0.05f - 0.025f
                val jitterY = Random.nextFloat() * 0.05f - 0.025f
                
                val x = (baseMinX + cellWidth / 2f + jitterX).coerceIn(0.08f, 0.82f)
                val y = (baseMinY + cellHeight / 2f + jitterY).coerceIn(0.08f, 0.82f)
                
                val rotation = Random.nextFloat() * 80f - 40f // -40 to +40 degrees

                itemsList.add(
                    VeggieItem(
                        id = UUID.randomUUID().toString(),
                        type = type,
                        xPercent = x,
                        yPercent = y,
                        rotation = rotation,
                        depth = depthIndex++
                    )
                )
            }
        }

        // Sort items by depth and calculate initial overlap
        val sortedList = itemsList.sortedBy { it.depth }
        calculateOverlaps(sortedList)
        _veggiePile.value = sortedList
    }

    private fun calculateOverlaps(items: List<VeggieItem>) {
        for (i in items.indices) {
            val itemA = items[i]
            var covered = false
            // Check if any item on top (index > i) overlaps with item A
            for (j in i + 1 until items.size) {
                val itemB = items[j]
                val dx = itemA.xPercent - itemB.xPercent
                val dy = itemA.yPercent - itemB.yPercent
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < overlapRadius) {
                    covered = true
                    break
                }
            }
            itemA.isCovered = covered
        }
    }

    fun selectVeggie(item: VeggieItem) {
        if (item.isCovered) {
            SoundPlayer.playError()
            return
        }

        // Play tap
        SoundPlayer.playVeggieTap()

        // Track action for undo
        selectionHistory.add(item)

        // Remove from pile
        val currentPile = _veggiePile.value.filter { it.id != item.id }
        calculateOverlaps(currentPile)
        _veggiePile.value = currentPile

        // Add to slots
        val currentSlots = _slots.value.toMutableList()
        currentSlots.add(item)

        // Sort slots to group similar types side by side
        currentSlots.sortBy { it.type.ordinal }
        _slots.value = currentSlots

        // Check if there is a group of >= 3 in the slots
        val typeGroups = currentSlots.groupBy { it.type }
        var hasGroupOf3 = false
        for ((type, group) in typeGroups) {
            if (group.size >= 3) {
                hasGroupOf3 = true
            }
        }

        if (hasGroupOf3) {
            // Restart 300ms quick-tap window to allow matching 4 or more
            pendingMatchJob?.cancel()
            pendingMatchJob = viewModelScope.launch {
                delay(300)
                checkMatches(_slots.value.toMutableList())
            }
        } else {
            // No match groups yet, check basket size limit immediately
            if (currentSlots.size >= 7) {
                triggerLevelFailed("basket_full")
            }
        }
    }

    private fun checkScoreProgression() {
        if (_score.value >= _requiredScore.value && _isGameActive.value) {
            triggerLevelCleared()
        }
    }

    private fun checkAutoClearBonus() {
        val pile = _veggiePile.value
        val slotsVal = _slots.value
        val totalItems = pile + slotsVal
        if (totalItems.isEmpty()) return
        
        // Count how many of each remaining type exist
        val typeCounts = totalItems.groupBy { it.type }.mapValues { it.value.size }
        val maxOfAnyType = typeCounts.values.maxOrNull() ?: 0
        if (maxOfAnyType < 3) {
            // No more triples can be formed! Clear all remaining and award bonus points!
            viewModelScope.launch {
                delay(600)
                _score.value += totalItems.size * 150 // award auto-clear bonus!
                _veggiePile.value = emptyList()
                _slots.value = emptyList()
                triggerLevelCleared()
            }
        }
    }

    private fun triggerSpecialVeggiePowerUp() {
        // Choose randomly between clearing a horizontal row or a vertical column of the pile
        val isRowClear = Random.nextBoolean()
        viewModelScope.launch {
            if (isRowClear) {
                // Pick a random row coordinate between 0.2 and 0.8 to target the pile
                val targetY = Random.nextFloat() * 0.6f + 0.2f
                _specialVeggieLaserRow.value = targetY
                
                // Blast sound!
                SoundPlayer.playLevelUp()
                
                delay(150)
                // Remove vegetables in this row range
                val currentPile = _veggiePile.value.toMutableList()
                val iterator = currentPile.iterator()
                var clearedAny = false
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (Math.abs(item.yPercent - targetY) < 0.15f) {
                        iterator.remove()
                        _score.value += 40 // bonus points for power-up harvesting
                        clearedAny = true
                    }
                }
                if (clearedAny) {
                    calculateOverlaps(currentPile)
                    _veggiePile.value = currentPile
                }
                
                delay(650)
                _specialVeggieLaserRow.value = null
            } else {
                // Pick a random column coordinate between 0.2 and 0.8
                val targetX = Random.nextFloat() * 0.6f + 0.2f
                _specialVeggieLaserCol.value = targetX
                
                // Blast sound!
                SoundPlayer.playLevelUp()
                
                delay(150)
                // Remove vegetables in this column range
                val currentPile = _veggiePile.value.toMutableList()
                val iterator = currentPile.iterator()
                var clearedAny = false
                while (iterator.hasNext()) {
                    val item = iterator.next()
                    if (Math.abs(item.xPercent - targetX) < 0.15f) {
                        iterator.remove()
                        _score.value += 40 // bonus points
                        clearedAny = true
                    }
                }
                if (clearedAny) {
                    calculateOverlaps(currentPile)
                    _veggiePile.value = currentPile
                }
                
                delay(650)
                _specialVeggieLaserCol.value = null
            }

            // After power-up clear, check if we need to auto-clear remaining unmatched leftovers
            checkAutoClearBonus()
            checkScoreProgression()
        }
    }

    private fun checkMatches(currentSlots: MutableList<VeggieItem>) {
        val typeGroups = currentSlots.groupBy { it.type }
        var matched = false
        var specialVeggieTriggered = false
        var lastClearedType: VeggieType? = null

        val updatedSlots = currentSlots.toMutableList()

        for ((type, group) in typeGroups) {
            if (group.size >= 3) {
                matched = true
                val matchCount = group.size
                lastClearedType = type

                // Find indices of these items in slots before removing
                val matchIndices = mutableListOf<Int>()
                _slots.value.forEachIndexed { idx, item ->
                    if (item.type == type && matchIndices.size < matchCount) {
                        matchIndices.add(idx)
                    }
                }

                // Remove from updated slots and history
                updatedSlots.removeAll { it.type == type }
                selectionHistory.removeAll { it.type == type }

                // Calculate points
                val basePoints = 100
                val now = System.currentTimeMillis()
                if (lastMatchTime > 0 && now - lastMatchTime <= activeComboWindowMs) {
                    _comboCount.value += 1
                } else {
                    _comboCount.value = 0
                }
                _comboMultiplier.value = 1.0f + _comboCount.value * 0.5f
                lastMatchTime = now

                // Calculate the new active combo window for this combo level!
                val nextWindow = (comboWindowMs / (1.0f + _comboCount.value * 0.5f)).toLong().coerceAtLeast(1200L)
                activeComboWindowMs = nextWindow

                val matchBonus = if (matchCount >= 4) 1.5f else 1.0f
                val finalPoints = (basePoints * _comboMultiplier.value * matchBonus).toInt()
                _score.value += finalPoints

                // Increment Quest 1 (Match 15 Veggies) and Quest 2 (Achieve 3x Combo)
                incrementQuestProgress(1, matchCount)
                if (_comboCount.value >= 3) {
                    incrementQuestProgress(2, _comboCount.value)
                }

                // Restart combo timer
                comboTimerJob?.cancel()
                comboTimerJob = viewModelScope.launch {
                    while (true) {
                        val elapsed = System.currentTimeMillis() - lastMatchTime
                        val remaining = (activeComboWindowMs - elapsed).coerceAtLeast(0L)
                        _comboRemainingPercent.value = remaining.toFloat() / activeComboWindowMs
                        if (remaining <= 0L) {
                            _comboCount.value = 0
                            _comboMultiplier.value = 1.0f
                            _comboRemainingPercent.value = 0.0f
                            activeComboWindowMs = comboWindowMs // reset back to base 5s
                            break
                        }
                        delay(30)
                    }
                }

                // Add to floating matches
                val newFloatingItems = matchIndices.map { slotIndex ->
                    FloatingMatchItem(
                        type = type,
                        initialIndex = slotIndex,
                        scoreGained = finalPoints,
                        comboMultiplier = _comboMultiplier.value
                    )
                }
                _floatingMatches.value = _floatingMatches.value + newFloatingItems

                // Auto clean up from floating matches list after 800ms
                viewModelScope.launch {
                    delay(800)
                    _floatingMatches.value = _floatingMatches.value.filter { item ->
                        newFloatingItems.none { it.id == item.id }
                    }
                }

                // Play match or combo sound
                viewModelScope.launch {
                    if (_comboCount.value > 0) {
                        SoundPlayer.playCombo(_comboMultiplier.value)
                    } else {
                        SoundPlayer.playMatch()
                    }
                }

                // Trigger Special Veggie power-up if matchCount >= 4
                if (matchCount >= 4) {
                    specialVeggieTriggered = true
                }
            }
        }

        if (matched) {
            _slots.value = updatedSlots
        }

        if (specialVeggieTriggered && lastClearedType != null) {
            triggerSpecialVeggiePowerUp()
        }

        checkScoreProgression()

        // Evaluate level result
        if (_veggiePile.value.isEmpty() && _slots.value.isEmpty()) {
            triggerLevelCleared()
        } else if (_slots.value.size >= 7) {
            triggerLevelFailed("basket_full")
        }
    }

    fun useUndo() {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            if (prog.powerUndoCount <= 0 && prog.coins < 10) {
                SoundPlayer.playError()
                return@launch
            }

            if (selectionHistory.isEmpty()) {
                SoundPlayer.playError()
                return@launch
            }

            // Deduct power or coins
            val updatedProg = if (prog.powerUndoCount > 0) {
                prog.copy(powerUndoCount = prog.powerUndoCount - 1)
            } else {
                prog.copy(coins = prog.coins - 10)
            }
            repository.saveProgress(updatedProg)

            // Undo action
            SoundPlayer.playMenuTap()
            val itemToReturn = selectionHistory.removeAt(selectionHistory.size - 1)

            // Remove from slots
            _slots.value = _slots.value.filter { it.id != itemToReturn.id }

            // Put back in pile
            val restoredPile = _veggiePile.value.toMutableList()
            restoredPile.add(itemToReturn)
            val sortedPile = restoredPile.sortedBy { it.depth }
            calculateOverlaps(sortedPile)
            _veggiePile.value = sortedPile
        }
    }

    fun useMagnet() {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            if (prog.powerMagnetCount <= 0 && prog.coins < 20) {
                SoundPlayer.playError()
                return@launch
            }

            val pile = _veggiePile.value
            if (pile.isEmpty()) return@launch

            // Find a candidate:
            // 1. Try to find a type already in slots
            // 2. Or just pick any uncompleted type in the pile
            val currentSlots = _slots.value
            val targetType = if (currentSlots.isNotEmpty()) {
                currentSlots.groupBy { it.type }.minByOrNull { 3 - it.value.size }?.key
            } else {
                pile.shuffled().firstOrNull()?.type
            } ?: return@launch

            // Pull items of this targetType from the board (up to what's needed to match, max 3)
            val existingInSlotsCount = currentSlots.filter { it.type == targetType }.size
            val needed = 3 - existingInSlotsCount
            if (needed <= 0) return@launch

            // Take uncovered/available items if possible, or just any of that type
            val matchingInPile = pile.filter { it.type == targetType }.sortedBy { it.isCovered }
            if (matchingInPile.isEmpty()) {
                SoundPlayer.playError()
                return@launch
            }

            val toPull = matchingInPile.take(needed)

            // Deduct power or coins
            val updatedProg = if (prog.powerMagnetCount > 0) {
                prog.copy(powerMagnetCount = prog.powerMagnetCount - 1)
            } else {
                prog.copy(coins = prog.coins - 20)
            }
            repository.saveProgress(updatedProg)

            SoundPlayer.playMatch()

            // Pull them
            val pulledIds = toPull.map { it.id }.toSet()
            val nextPile = _veggiePile.value.filter { it.id !in pulledIds }
            calculateOverlaps(nextPile)
            _veggiePile.value = nextPile

            val nextSlots = _slots.value.toMutableList()
            nextSlots.addAll(toPull)
            nextSlots.sortBy { it.type.ordinal }
            _slots.value = nextSlots

            // Process matches
            checkMatches(nextSlots)
        }
    }

    fun useFreeze() {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            if (prog.powerFreezeCount <= 0 && prog.coins < 15) {
                SoundPlayer.playError()
                return@launch
            }

            if (_powerFreezeActive.value) {
                SoundPlayer.playError()
                return@launch
            }

            // Deduct power or coins
            val updatedProg = if (prog.powerFreezeCount > 0) {
                prog.copy(powerFreezeCount = prog.powerFreezeCount - 1)
            } else {
                prog.copy(coins = prog.coins - 15)
            }
            repository.saveProgress(updatedProg)

            SoundPlayer.playTone(900.0, 300)
            _powerFreezeActive.value = true
            _freezeTimerSeconds.value = 15 // freeze for 15 seconds
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0 && _isGameActive.value) {
                delay(1000)

                if (_powerFreezeActive.value) {
                    _freezeTimerSeconds.value -= 1
                    if (_freezeTimerSeconds.value <= 0) {
                        _powerFreezeActive.value = false
                    }
                } else {
                    _timerSeconds.value -= 1
                }

                if (_timerSeconds.value <= 0) {
                    triggerLevelFailed("time_up")
                }
            }
        }
    }

    private fun triggerLevelCleared() {
        _isGameActive.value = false
        timerJob?.cancel()

        viewModelScope.launch {
            SoundPlayer.playLevelUp()

            val prog = repository.getProgressDirect() ?: PlayerProgress()
            val nextLevel = prog.level + 1

            // Calculate stars
            val percentLeft = (_timerSeconds.value.toFloat() / _maxTimerSeconds.value.toFloat()) * 100
            val stars = when {
                percentLeft >= 50 -> 3
                percentLeft >= 20 -> 2
                else -> 1
            }

            // Highscore
            val pointsEarned = _score.value + (_timerSeconds.value * 2) // score + time bonus
            val updatedCoins = prog.coins + 50 // Level clear bonus

            val updatedProgress = prog.copy(
                level = nextLevel,
                coins = updatedCoins
            )
            repository.saveProgress(updatedProgress)

            // Save history
            repository.insertHistory(
                LevelHistory(
                    levelNumber = prog.level,
                    score = pointsEarned,
                    stars = stars,
                    completionTimeSec = _maxTimerSeconds.value - _timerSeconds.value
                )
            )

            _score.value = pointsEarned
            _levelCleared.value = true
        }
    }

    private fun triggerLevelFailed(reason: String) {
        _isGameActive.value = false
        timerJob?.cancel()
        SoundPlayer.playError()
        _levelFailedReason.value = reason
        _levelFailed.value = true
    }

    fun quitGame() {
        _isGameActive.value = false
        timerJob?.cancel()
        _levelCleared.value = false
        _levelFailed.value = false
        _levelFailedReason.value = null
    }

    fun buyPowerUp(powerType: String) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            val cost = when (powerType) {
                "undo" -> 30
                "magnet" -> 50
                "freeze" -> 40
                "energy" -> 100
                else -> 0
            }

            if (prog.coins < cost) {
                SoundPlayer.playError()
                return@launch
            }

            SoundPlayer.playTone(523.25, 60, "pluck", 0.35)
            delay(40)
            SoundPlayer.playTone(783.99, 110, "pluck", 0.4)

            val updatedProg = when (powerType) {
                "undo" -> prog.copy(coins = prog.coins - cost, powerUndoCount = prog.powerUndoCount + 1)
                "magnet" -> prog.copy(coins = prog.coins - cost, powerMagnetCount = prog.powerMagnetCount + 1)
                "freeze" -> prog.copy(coins = prog.coins - cost, powerFreezeCount = prog.powerFreezeCount + 1)
                "energy" -> prog.copy(coins = prog.coins - cost, energy = prog.energy + 5, lastEnergyRefillTime = System.currentTimeMillis())
                else -> prog
            }

            repository.saveProgress(updatedProg)
            incrementQuestProgress(4, cost)
        }
    }

    fun changePlayerName(newName: String) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            repository.saveProgress(prog.copy(name = newName.trim().ifEmpty { "Çiftçi" }))
        }
    }

    fun toggleMuteAll() {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            val nextMuted = !prog.isMuted
            repository.saveProgress(prog.copy(isMuted = nextMuted))
            if (!nextMuted) {
                SoundPlayer.isMuted = false
                SoundPlayer.playMenuTap()
            }
        }
    }

    fun setSoundVolume(volume: Float) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            repository.saveProgress(prog.copy(soundVolume = volume))
        }
    }

    fun setMusicVolume(volume: Float) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            repository.saveProgress(prog.copy(musicVolume = volume))
        }
    }

    fun checkAndRegenerateEnergy() {
        viewModelScope.launch {
            val prog = progress.value ?: return@launch
            val now = System.currentTimeMillis()
            val maxEnergy = prog.maxEnergy
            val currentEnergy = prog.energy
            
            if (currentEnergy >= maxEnergy) {
                _energyTimerSeconds.value = 0
                return@launch
            }
            
            // 1 energy every 3 minutes (180,000 ms)
            val refillIntervalMs = 180000L 
            val timePassed = now - prog.lastEnergyRefillTime
            if (timePassed >= refillIntervalMs) {
                val energyToGain = (timePassed / refillIntervalMs).toInt()
                val newEnergy = (currentEnergy + energyToGain).coerceAtMost(maxEnergy)
                
                val nextRefillTime = if (newEnergy >= maxEnergy) {
                    now
                } else {
                    prog.lastEnergyRefillTime + (energyToGain * refillIntervalMs)
                }
                
                repository.saveProgress(prog.copy(
                    energy = newEnergy,
                    lastEnergyRefillTime = nextRefillTime
                ))
            } else {
                val remainingMs = refillIntervalMs - (timePassed % refillIntervalMs)
                _energyTimerSeconds.value = (remainingMs / 1000).toInt()
            }
        }
    }

    fun buyCoinsWithRealMoney(packId: String) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            val coinsToGain = when (packId) {
                "starter" -> 250
                "gardener" -> 600
                "master" -> 1500
                else -> 0
            }
            if (coinsToGain > 0) {
                // Play golden coins jingle sound
                SoundPlayer.playTone(523.25, 50, "soft", 0.4)
                delay(60)
                SoundPlayer.playTone(659.25, 50, "soft", 0.4)
                delay(60)
                SoundPlayer.playTone(783.99, 50, "soft", 0.4)
                delay(60)
                SoundPlayer.playTone(1046.50, 140, "decay", 0.5)

                repository.saveProgress(prog.copy(coins = prog.coins + coinsToGain))
            }
        }
    }

    private fun isDifferentDay(timeA: Long, timeB: Long): Boolean {
        if (timeA == 0L) return true
        val calA = java.util.Calendar.getInstance().apply { timeInMillis = timeA }
        val calB = java.util.Calendar.getInstance().apply { timeInMillis = timeB }
        return calA.get(java.util.Calendar.YEAR) != calB.get(java.util.Calendar.YEAR) ||
               calA.get(java.util.Calendar.DAY_OF_YEAR) != calB.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun checkDailyRewards(current: PlayerProgress) {
        val now = System.currentTimeMillis()
        var updated = current

        // 1. Check if Daily Harvest is available (different day)
        val harvestAvailable = isDifferentDay(current.lastDailyRewardClaimTime, now)
        if (harvestAvailable) {
            _isDailyHarvestAvailable.value = true
            _isDailyHarvestShowing.value = true // Automatically show the popup upon first login/startup
        }

        // 2. Check if Daily Quests need to be reset (different day)
        val questsResetNeeded = isDifferentDay(current.lastDailyQuestsResetTime, now)
        if (questsResetNeeded) {
            updated = updated.copy(
                lastDailyQuestsResetTime = now,
                quest1Progress = 0,
                quest1Completed = false,
                quest1Claimed = false,
                quest2Progress = 0,
                quest2Completed = false,
                quest2Claimed = false,
                quest3Progress = 0,
                quest3Completed = false,
                quest3Claimed = false,
                quest4Progress = 0,
                quest4Completed = false,
                quest4Claimed = false
            )
        }

        if (updated != current) {
            viewModelScope.launch {
                repository.saveProgress(updated)
            }
        }
    }

    fun claimDailyHarvest() {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            val now = System.currentTimeMillis()
            if (!isDifferentDay(prog.lastDailyRewardClaimTime, now)) {
                // Already claimed
                _isDailyHarvestAvailable.value = false
                _isDailyHarvestShowing.value = false
                return@launch
            }

            // Reward: 200 Coins + 5 Energy
            val updatedProg = prog.copy(
                coins = prog.coins + 200,
                energy = (prog.energy + 5).coerceAtMost(prog.maxEnergy + 5),
                lastDailyRewardClaimTime = now
            )
            repository.saveProgress(updatedProg)
            
            _isDailyHarvestAvailable.value = false
            _isDailyHarvestShowing.value = false

            // Play celebratory sound
            SoundPlayer.playTone(523.25, 100, "pluck", 0.5)
            delay(100)
            SoundPlayer.playTone(659.25, 100, "pluck", 0.5)
            delay(100)
            SoundPlayer.playTone(783.99, 250, "pluck", 0.6)
        }
    }

    fun incrementQuestProgress(questIndex: Int, amount: Int) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            
            // Check if reset is needed first
            val now = System.currentTimeMillis()
            var currentProg = if (isDifferentDay(prog.lastDailyQuestsResetTime, now)) {
                prog.copy(
                    lastDailyQuestsResetTime = now,
                    quest1Progress = 0, quest1Completed = false, quest1Claimed = false,
                    quest2Progress = 0, quest2Completed = false, quest2Claimed = false,
                    quest3Progress = 0, quest3Completed = false, quest3Claimed = false,
                    quest4Progress = 0, quest4Completed = false, quest4Claimed = false
                )
            } else {
                prog
            }

            val updatedProg = when (questIndex) {
                1 -> {
                    if (currentProg.quest1Completed) return@launch
                    val nextProgress = currentProg.quest1Progress + amount
                    val completed = nextProgress >= 15
                    currentProg.copy(quest1Progress = nextProgress, quest1Completed = completed)
                }
                2 -> {
                    if (currentProg.quest2Completed) return@launch
                    val nextProgress = amount
                    val completed = nextProgress >= 3
                    if (completed) {
                        currentProg.copy(quest2Progress = 1, quest2Completed = true)
                    } else {
                        currentProg
                    }
                }
                3 -> {
                    if (currentProg.quest3Completed) return@launch
                    val nextProgress = currentProg.quest3Progress + amount
                    val completed = nextProgress >= 2
                    currentProg.copy(quest3Progress = nextProgress, quest3Completed = completed)
                }
                4 -> {
                    if (currentProg.quest4Completed) return@launch
                    val nextProgress = currentProg.quest4Progress + amount
                    val completed = nextProgress >= 50
                    currentProg.copy(quest4Progress = nextProgress, quest4Completed = completed)
                }
                else -> currentProg
            }

            if (updatedProg != prog) {
                repository.saveProgress(updatedProg)
            }
        }
    }

    fun claimQuestReward(questIndex: Int) {
        viewModelScope.launch {
            val prog = repository.getProgressDirect() ?: return@launch
            
            val alreadyClaimed = when (questIndex) {
                1 -> prog.quest1Claimed || !prog.quest1Completed
                2 -> prog.quest2Claimed || !prog.quest2Completed
                3 -> prog.quest3Claimed || !prog.quest3Completed
                4 -> prog.quest4Claimed || !prog.quest4Completed
                else -> true
            }

            if (alreadyClaimed) {
                SoundPlayer.playError()
                return@launch
            }

            var updatedProg = prog
            when (questIndex) {
                1 -> {
                    // Reward: 50 coins (altın)
                    updatedProg = prog.copy(
                        coins = prog.coins + 50,
                        quest1Claimed = true
                    )
                }
                2 -> {
                    // Reward: +1 Magnet (mıknatıs)
                    updatedProg = prog.copy(
                        powerMagnetCount = prog.powerMagnetCount + 1,
                        quest2Claimed = true
                    )
                }
                3 -> {
                    // Reward: +1 Freeze (dondurma)
                    updatedProg = prog.copy(
                        powerFreezeCount = prog.powerFreezeCount + 1,
                        quest3Claimed = true
                    )
                }
                4 -> {
                    // Reward: +5 Energy (enerji)
                    updatedProg = prog.copy(
                        energy = (prog.energy + 5).coerceAtMost(prog.maxEnergy + 5),
                        quest4Claimed = true
                    )
                }
            }

            repository.saveProgress(updatedProg)

            // Play nice reward claim tone
            SoundPlayer.playTone(587.33, 100, "sine", 0.4)
            delay(100)
            SoundPlayer.playTone(880.00, 200, "sine", 0.5)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class FloatingMatchItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val type: com.example.ui.model.VeggieType,
    val initialIndex: Int,
    val scoreGained: Int,
    val comboMultiplier: Float,
    val timestamp: Long = System.currentTimeMillis()
)
