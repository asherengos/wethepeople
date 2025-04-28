package com.example.wethepeople.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.wethepeople.data.model.CitizenRank
import com.example.wethepeople.data.model.LeaderboardEntry as ModelLeaderboardEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeaderboardViewModel : ViewModel() {
    private val _leaderboardEntries = MutableStateFlow<List<ModelLeaderboardEntry>>(emptyList())
    val leaderboardEntries: StateFlow<List<ModelLeaderboardEntry>> = _leaderboardEntries.asStateFlow()

    private val currentUserId = "current_user" // Replace with actual user ID from auth system

    init {
        // TODO: Replace with actual data from repository
        loadMockData()
    }

    fun getCurrentUserId(): String = currentUserId

    private fun loadMockData() {
        val mockEntries = listOf(
            ModelLeaderboardEntry(
                userId = "user1",
                username = "TruePatriot1776",
                patriotPoints = 1776,
                citizenRank = CitizenRank.FOUNDING_FATHER.title
            ),
            ModelLeaderboardEntry(
                userId = "current_user",
                username = "FreedomFighter",
                patriotPoints = 1492,
                citizenRank = CitizenRank.GOVERNOR.title
            ),
            ModelLeaderboardEntry(
                userId = "user3",
                username = "LibertyBell",
                patriotPoints = 1234,
                citizenRank = CitizenRank.SENATOR.title
            ),
            ModelLeaderboardEntry(
                userId = "user4",
                username = "EagleEyes",
                patriotPoints = 987,
                citizenRank = CitizenRank.CONGRESSMAN.title
            ),
            ModelLeaderboardEntry(
                userId = "user5",
                username = "ConstitutionGuard",
                patriotPoints = 876,
                citizenRank = CitizenRank.MINUTEMAN.title
            ),
            ModelLeaderboardEntry(
                userId = "user6",
                username = "MinuteMaster",
                patriotPoints = 765,
                citizenRank = CitizenRank.MINUTEMAN.title
            ),
            ModelLeaderboardEntry(
                userId = "user7",
                username = "VotingVoice",
                patriotPoints = 150,
                citizenRank = CitizenRank.VOTER.title
            ),
            ModelLeaderboardEntry(
                userId = "user8",
                username = "NewPatriot",
                patriotPoints = 50,
                citizenRank = CitizenRank.CANVASSER.title
            )
        )
        _leaderboardEntries.value = mockEntries.sortedByDescending { it.patriotPoints }
    }
} 