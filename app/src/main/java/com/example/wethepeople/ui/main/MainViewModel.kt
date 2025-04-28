package com.example.wethepeople.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.R
import com.example.wethepeople.data.model.CitizenRank
import com.example.wethepeople.data.repository.EagleyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.example.wethepeople.data.CustomizationRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import android.util.Log

data class UserStats(
    val patriotPoints: Int = 0,
    val freedomBucks: Int = 0,
    val citizenRank: CitizenRank = CitizenRank.CANVASSER
)

data class Mission(
    val title: String,
    val reward: Int,
    val progress: Float,
    val isComplete: Boolean = false
)

data class Activity(
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class EagleyDisplayData(
    val baseResourceId: Int = R.drawable.eagley_base,
    val hatResourceId: Int? = null,
    val accessoryResourceId: Int? = null,
    val hatItemId: String? = null,
    val accessoryItemId: String? = null,
    val customizations: Map<String, Pair<androidx.compose.ui.geometry.Offset, Float>> = emptyMap()
)

// Changed to AndroidViewModel to get Application context for Repository
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val eagleyRepository = EagleyRepository(application)
    private val customizationRepository = CustomizationRepository(
        application.getSharedPreferences("eagley_customization", android.content.Context.MODE_PRIVATE)
    )
    
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats.asStateFlow()

    private val _dailyMissions = MutableStateFlow<List<Mission>>(listOf(
        Mission("Cast 3 votes today", 30, 0.6f),
        Mission("Share 1 proposal", 15, 0.0f),
        Mission("Comment on 2 debates", 25, 0.5f)
    ))
    val dailyMissions: StateFlow<List<Mission>> = _dailyMissions.asStateFlow()

    private val _recentActivities = MutableStateFlow<List<Activity>>(listOf(
        Activity("You earned 10 Patriot Points for voting"),
        Activity("CitizenX agreed with your debate point"),
        Activity("You completed a daily mission"),
        Activity("New proposal added: Tax Reform 2023")
    ))
    val recentActivities: StateFlow<List<Activity>> = _recentActivities.asStateFlow()
    
    // StateFlow for Eagley's display data
    private val _eagleyDisplayData = MutableStateFlow(EagleyDisplayData())
    val eagleyData: StateFlow<EagleyDisplayData> = _eagleyDisplayData.asStateFlow()

    // Rank up celebration state
    private val _showRankUpCelebration = MutableStateFlow(false)
    val showRankUpCelebration: StateFlow<Boolean> = _showRankUpCelebration.asStateFlow()
    
    // Navigation events
    private val _navigationEvent = MutableStateFlow<String?>(null)
    val navigationEvent: StateFlow<String?> = _navigationEvent.asStateFlow()
    
    val currentRank: CitizenRank
        get() = _userStats.value.citizenRank

    init {
        // Combine flows to get consistent updates
        viewModelScope.launch {
            combine(
                eagleyRepository.userDataFlow, 
                customizationRepository.customizationsFlow
            ) { userData, customizations ->
                // This lambda executes whenever either userData or customizations changes
                
                // Update user stats based on userData
                _userStats.value = _userStats.value.copy(
                    patriotPoints = userData.patriotPoints,
                    freedomBucks = userData.freedomBucks,
                    // Assuming rank is derived or handled elsewhere
                    // citizenRank = CitizenRank.fromPoints(userData.patriotPoints) 
                )

                // Determine resource IDs from selected item IDs in userData
                val baseId = userData.appearance.selectedColorSchemeId
                val hatId = userData.appearance.selectedHatId
                val accessoryId = userData.appearance.selectedAccessoryId
                
                val baseResId = eagleyRepository.getItemById(baseId ?: "color_default")?.resourceId ?: R.drawable.eagley_base
                val hatResId = hatId?.let { eagleyRepository.getItemById(it)?.resourceId }
                val accessoryResId = accessoryId?.let { eagleyRepository.getItemById(it)?.resourceId }

                // Create the new display data, combining latest items and latest customizations
                val newDisplayData = EagleyDisplayData(
                    baseResourceId = baseResId,
                    hatResourceId = hatResId,
                    accessoryResourceId = accessoryResId,
                    hatItemId = hatId,
                    accessoryItemId = accessoryId,
                    customizations = customizations // Use the latest map from customizationsFlow
                )
                
                // Update the state only if it actually changed
                if (_eagleyDisplayData.value != newDisplayData) {
                     _eagleyDisplayData.value = newDisplayData
                     Log.d("MainViewModel", "Updated eagleyDisplayData: $newDisplayData")
                     // Optional: Add activity log only if customizations specifically changed?
                     // if (_eagleyDisplayData.value.customizations != customizations && customizations.isNotEmpty()) {
                     //     addActivity("Eagley customizations updated!")
                     // }
                }

            }.collect() // Start collecting the combined flow
        }
    }

    fun addPatriotPoints(points: Int) {
        viewModelScope.launch { 
            eagleyRepository.addPatriotPoints(points)
            // User stats (_userStats) will update automatically via the combine flow
            // Trigger rank up celebration if needed (logic might need adjustment based on combine flow)
             val previousRank = _userStats.value.citizenRank
             val newRank = CitizenRank.fromPoints(eagleyRepository.userDataFlow.value.patriotPoints)
             if (newRank.ordinal > previousRank.ordinal) {
                 showRankUpCelebration()
             }
            addActivity("You earned $points Patriot Points!")
        }
    }

    fun addFreedomBucks(amount: Int) {
        viewModelScope.launch { 
            // Need an equivalent function in EagleyRepository
            // eagleyRepository.addFreedomBucks(amount)
             _userStats.value = _userStats.value.copy(
                  freedomBucks = _userStats.value.freedomBucks + amount
             )
            addActivity("You earned $amount Freedom Bucks!")
        }
    }

    fun updateMissionProgress(index: Int, newProgress: Float) {
        viewModelScope.launch {
            val currentMissions = _dailyMissions.value.toMutableList()
            if (index < currentMissions.size) {
                val mission = currentMissions[index]
                val isComplete = newProgress >= 1.0f
                currentMissions[index] = mission.copy(
                    progress = newProgress,
                    isComplete = isComplete
                )
                _dailyMissions.value = currentMissions

                if (isComplete && !mission.isComplete) {
                    addPatriotPoints(mission.reward)
                    addActivity("Mission Complete: ${mission.title}")
                }
            }
        }
    }

    private fun addActivity(text: String) {
        viewModelScope.launch {
            val currentActivities = _recentActivities.value.toMutableList()
            currentActivities.add(0, Activity(text))
            if (currentActivities.size > 10) {
                currentActivities.removeLastOrNull()
            }
            _recentActivities.value = currentActivities
        }
    }

    fun onCustomizeClick() {
        _navigationEvent.value = "${com.example.wethepeople.navigation.Screen.Main.route}?${com.example.wethepeople.navigation.NavArgs.InitialTab}=${com.example.wethepeople.navigation.Screen.Eagly.route}"
    }
    
    fun onNavigationHandled() {
        _navigationEvent.value = null
    }

    fun dismissRankUpCelebration() {
        _showRankUpCelebration.value = false
    }

    fun showRankUpCelebration() {
        _showRankUpCelebration.value = true
    }
} 