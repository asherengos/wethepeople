package com.example.wethepeople.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

class MissionsViewModel : ViewModel() {
    private val _currentLevel = MutableStateFlow(1)
    val currentLevel: StateFlow<Int> = _currentLevel.asStateFlow()

    private val _currentExp = MutableStateFlow(0)
    val currentExp: StateFlow<Int> = _currentExp.asStateFlow()

    private val _expToNextLevel = MutableStateFlow(1000)
    val expToNextLevel: StateFlow<Int> = _expToNextLevel.asStateFlow()

    private val _dailyMissions = MutableStateFlow<List<Mission>>(emptyList())
    val dailyMissions: StateFlow<List<Mission>> = _dailyMissions.asStateFlow()

    private val _weeklyMissions = MutableStateFlow<List<Mission>>(emptyList())
    val weeklyMissions: StateFlow<List<Mission>> = _weeklyMissions.asStateFlow()

    init {
        viewModelScope.launch {
            loadMockMissions()
        }
    }

    private suspend fun loadMockMissions() {
        val mockDailyMissions = listOf(
            Mission(
                id = "d1",
                title = "Daily Debate",
                description = "Participate in 3 debates",
                points = 100,
                target = 3,
                progress = 0,
                isDaily = true,
                isCompleted = false,
                type = MissionType.DEBATE
            ),
            Mission(
                id = "d2",
                title = "Voice Your Opinion",
                description = "Cast 5 votes on debates",
                points = 50,
                target = 5,
                progress = 0,
                isDaily = true,
                isCompleted = false,
                type = MissionType.VOTE
            ),
            Mission(
                id = "d3",
                title = "Spread the Word",
                description = "Share 2 debates with friends",
                points = 75,
                target = 2,
                progress = 0,
                isDaily = true,
                isCompleted = false,
                type = MissionType.SHARE
            )
        )

        val mockWeeklyMissions = listOf(
            Mission(
                id = "w1",
                title = "Active Citizen",
                description = "Participate in 15 debates",
                points = 500,
                target = 15,
                progress = 0,
                isDaily = false,
                isCompleted = false,
                type = MissionType.DEBATE
            ),
            Mission(
                id = "w2",
                title = "Community Leader",
                description = "Get 50 upvotes on your comments",
                points = 300,
                target = 50,
                progress = 0,
                isDaily = false,
                isCompleted = false,
                type = MissionType.COMMENT
            ),
            Mission(
                id = "w3",
                title = "Freedom Fighter",
                description = "Collect 1000 Freedom Bucks",
                points = 400,
                target = 1000,
                progress = 0,
                isDaily = false,
                isCompleted = false,
                type = MissionType.COLLECT
            )
        )

        _dailyMissions.update { mockDailyMissions }
        _weeklyMissions.update { mockWeeklyMissions }
    }

    fun updateMissionProgress(missionId: String, progress: Int) {
        viewModelScope.launch {
            _dailyMissions.update { missions ->
                missions.map { mission ->
                    if (mission.id == missionId) {
                        val newProgress = progress.coerceIn(0, mission.target)
                        mission.copy(
                            progress = newProgress,
                            isCompleted = newProgress >= mission.target
                        )
                    } else mission
                }
            }
        }
    }

    fun addExperience(points: Int) {
        viewModelScope.launch {
            val newExp = _currentExp.value + points
            if (newExp >= _expToNextLevel.value) {
                _currentLevel.value += 1
                _currentExp.value = newExp - _expToNextLevel.value
                _expToNextLevel.value += 500
            } else {
                _currentExp.value = newExp
            }
        }
    }
} 