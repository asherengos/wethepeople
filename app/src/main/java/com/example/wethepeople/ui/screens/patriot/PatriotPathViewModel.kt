package com.example.wethepeople.ui.screens.patriot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.wethepeople.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltViewModel
class PatriotPathViewModel @Inject constructor() : ViewModel() {
    
    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()
    
    private val _currentXp = MutableStateFlow(300)
    val currentXp: StateFlow<Int> = _currentXp.asStateFlow()
    
    private val _xpToNextLevel = MutableStateFlow(1000)
    val xpToNextLevel: StateFlow<Int> = _xpToNextLevel.asStateFlow()
    
    private val _rewards = MutableStateFlow<List<PatriotReward>>(emptyList())
    val rewards: StateFlow<List<PatriotReward>> = _rewards.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        viewModelScope.launch(Dispatchers.Default) {
            loadRewards()
            _isLoading.value = false
        }
    }
    
    private suspend fun loadRewards() {
        val rewardsList = withContext(Dispatchers.Default) {
            listOf(
                PatriotReward(
                    level = 10,
                    title = "FREEDOM EAGLE",
                    description = "Unlock the majestic Freedom Eagle hat for Eagley",
                    iconResId = R.drawable.eagley_freedom_hat,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 9,
                    title = "PATRIOT'S SHIELD",
                    description = "Earn the legendary shield of democracy",
                    iconResId = R.drawable.patriot_shield,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 8,
                    title = "LIBERTY WINGS",
                    description = "Special wing customization for your profile",
                    iconResId = R.drawable.liberty_wings,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 7,
                    title = "STAR SPANGLED BANNER",
                    description = "Unlock the national anthem emote",
                    iconResId = R.drawable.star_spangled_banner,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 6,
                    title = "FOUNDING FATHER",
                    description = "Exclusive Founding Father title",
                    iconResId = R.drawable.founding_father,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 5,
                    title = "LIBERTY BELL",
                    description = "Ring the Liberty Bell animation",
                    iconResId = R.drawable.liberty_bell,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 4,
                    title = "CONSTITUTION SCROLL",
                    description = "Special constitution background",
                    iconResId = R.drawable.constitution_scroll,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 3,
                    title = "PATRIOT'S QUILL",
                    description = "Signature customization unlock",
                    iconResId = R.drawable.patriot_quill,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 2,
                    title = "FREEDOM COINS",
                    description = "100 Freedom Coins bonus",
                    iconResId = R.drawable.freedom_coins,
                    isUnlocked = false
                ),
                PatriotReward(
                    level = 1,
                    title = "PATRIOT CAP",
                    description = "Your first patriotic accessory",
                    iconResId = R.drawable.patriot_cap,
                    isUnlocked = true
                )
            ).also { rewards ->
                unlockRewardsForLevel(_currentLevel.value, rewards)
            }
        }
        _rewards.value = rewardsList
    }

    fun addXp(amount: Int) {
        viewModelScope.launch {
            val newXp = _currentXp.value + amount
            if (newXp >= _xpToNextLevel.value) {
                // Level up!
                _currentLevel.value++
                _currentXp.value = newXp - _xpToNextLevel.value
                _xpToNextLevel.value = calculateXpForNextLevel(_currentLevel.value)
                unlockRewardsForLevel(_currentLevel.value, _rewards.value)
            } else {
                _currentXp.value = newXp
            }
        }
    }

    private fun calculateXpForNextLevel(level: Int): Int {
        // Simple exponential XP curve: 1000 * (1.5^(level-1))
        // Use explicit double values and order of operations for clarity
        val base = 1.5
        val exponent = (level - 1).toDouble()
        val multiplier = Math.pow(base, exponent)
        val result = 1000.0 * multiplier
        // Ensure we always return a positive value, and at least 1000
        return Math.max(1000, result.toInt())
    }

    private fun unlockRewardsForLevel(level: Int, currentRewards: List<PatriotReward>) {
        val updatedRewards = currentRewards.map { reward ->
            if (reward.level <= level) {
                reward.copy(isUnlocked = true)
            } else {
                reward
            }
        }
        _rewards.value = updatedRewards
    }
} 