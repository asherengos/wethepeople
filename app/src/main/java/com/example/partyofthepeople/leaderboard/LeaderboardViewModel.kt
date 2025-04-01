package com.example.partyofthepeople.leaderboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for leaderboard functionality
 */
class LeaderboardViewModel : ViewModel() {
    private val leaderboardManager = LeaderboardManager()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _leaderboardEntries = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardEntries: StateFlow<List<LeaderboardEntry>> = _leaderboardEntries
    
    private val _userRank = MutableStateFlow<LeaderboardEntry?>(null)
    val userRank: StateFlow<LeaderboardEntry?> = _userRank
    
    private val _districtLeaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val districtLeaderboard: StateFlow<List<LeaderboardEntry>> = _districtLeaderboard
    
    private val _selectedRankingType = MutableStateFlow(LeaderboardManager.RankingType.POWER_SCORE)
    val selectedRankingType: StateFlow<LeaderboardManager.RankingType> = _selectedRankingType
    
    private val _selectedDistrict = MutableStateFlow<String?>(null)
    val selectedDistrict: StateFlow<String?> = _selectedDistrict
    
    /**
     * Load leaderboard data based on the current ranking type
     */
    fun loadLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val rankingType = _selectedRankingType.value
                val entries = leaderboardManager.getTopRankedUsers(rankingType)
                
                // Use sample data if Firestore data is not yet populated
                _leaderboardEntries.value = if (entries.isEmpty()) {
                    generateSampleData(rankingType)
                } else {
                    entries
                }
                
                // Also get the current user's rank
                val currentUserId = Firebase.auth.currentUser?.uid
                if (currentUserId != null) {
                    _userRank.value = leaderboardManager.getUserRank(currentUserId, rankingType) 
                        ?: generateSampleUserRank(rankingType)
                }
            } catch (e: Exception) {
                Log.e("LeaderboardViewModel", "Error loading leaderboard: ${e.message}")
                // Fallback to sample data in case of error
                _leaderboardEntries.value = generateSampleData(_selectedRankingType.value)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Generate a sample user rank for demonstration
     */
    private fun generateSampleUserRank(rankingType: LeaderboardManager.RankingType): LeaderboardEntry {
        val entries = generateSampleData(rankingType)
        // Place the user randomly in the top 10 for demo purposes
        val randomPosition = (3..9).random()
        return entries[randomPosition].copy(
            userId = Firebase.auth.currentUser?.uid ?: "current_user",
            username = Firebase.auth.currentUser?.displayName ?: "You",
            isCurrentUser = true
        )
    }
    
    /**
     * Change the ranking type and reload the leaderboard
     */
    fun setRankingType(rankingType: LeaderboardManager.RankingType) {
        if (_selectedRankingType.value != rankingType) {
            _selectedRankingType.value = rankingType
            loadLeaderboard()
        }
    }
    
    /**
     * Load district leaderboard data
     */
    fun loadDistrictLeaderboard(district: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedDistrict.value = district
                val entries = leaderboardManager.getDistrictLeaderboard(district)
                
                // Use sample data if no district entries found
                _districtLeaderboard.value = if (entries.isEmpty()) {
                    generateSampleDistrictData(district)
                } else {
                    entries
                }
            } catch (e: Exception) {
                Log.e("LeaderboardViewModel", "Error loading district leaderboard: ${e.message}")
                // Fallback to sample data for district
                _districtLeaderboard.value = generateSampleDistrictData(district)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Generate sample data for a specific district
     */
    private fun generateSampleDistrictData(district: String): List<LeaderboardEntry> {
        // Get a subset of the sample data and modify it to be from the specified district
        return generateSampleData(LeaderboardManager.RankingType.POWER_SCORE)
            .take(6)
            .mapIndexed { index, entry ->
                entry.copy(
                    rank = index + 1,
                    district = district,
                    // Make user the top district entry if they exist
                    isCurrentUser = index == 0 && Firebase.auth.currentUser != null
                )
            }
    }
    
    /**
     * Get ranking type display name
     */
    fun getRankingTypeDisplayName(rankingType: LeaderboardManager.RankingType): String {
        return when (rankingType) {
            LeaderboardManager.RankingType.POWER_SCORE -> "Power Score"
            LeaderboardManager.RankingType.PARTICIPATION -> "Participation"
            LeaderboardManager.RankingType.STREAK_DAYS -> "Streak Days"
            LeaderboardManager.RankingType.COMMENT_ACTIVITY -> "Comment Activity"
        }
    }
    
    /**
     * Get ranking value display label
     */
    fun getRankingValueLabel(rankingType: LeaderboardManager.RankingType): String {
        return when (rankingType) {
            LeaderboardManager.RankingType.POWER_SCORE -> "Power"
            LeaderboardManager.RankingType.PARTICIPATION -> "Points"
            LeaderboardManager.RankingType.STREAK_DAYS -> "Days"
            LeaderboardManager.RankingType.COMMENT_ACTIVITY -> "Interactions"
        }
    }
    
    /**
     * Get icon for ranking type
     */
    fun getRankingTypeIcon(rankingType: LeaderboardManager.RankingType): String {
        return when (rankingType) {
            LeaderboardManager.RankingType.POWER_SCORE -> "⚡"
            LeaderboardManager.RankingType.PARTICIPATION -> "🏆"
            LeaderboardManager.RankingType.STREAK_DAYS -> "🔥"
            LeaderboardManager.RankingType.COMMENT_ACTIVITY -> "💬"
        }
    }
    
    /**
     * Generate sample data for demonstration or during initial Firebase population
     */
    private fun generateSampleData(rankingType: LeaderboardManager.RankingType): List<LeaderboardEntry> {
        val baseList = listOf(
            LeaderboardEntry(
                userId = "user1",
                username = "EagleDefender1776",
                district = "District 1",
                rank = 1,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 87 else 1250,
                achievementCount = 12,
                isBoosted = true
            ),
            LeaderboardEntry(
                userId = "user2",
                username = "ConstitutionWarrior",
                district = "District 3",
                rank = 2,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 72 else 980,
                achievementCount = 9
            ),
            LeaderboardEntry(
                userId = "user3",
                username = "LibertyGuardian",
                district = "District 2",
                rank = 3,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 65 else 920,
                achievementCount = 8
            ),
            LeaderboardEntry(
                userId = "user4",
                username = "FreedomFighter76",
                district = "District 5",
                rank = 4,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 58 else 780,
                achievementCount = 7
            ),
            LeaderboardEntry(
                userId = "user5",
                username = "PatriotDefender",
                district = "District 4",
                rank = 5,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 45 else 650,
                achievementCount = 6
            ),
            LeaderboardEntry(
                userId = "user6",
                username = "1776Forever",
                district = "District 1",
                rank = 6,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 37 else 540,
                achievementCount = 5
            ),
            LeaderboardEntry(
                userId = "user7",
                username = "LibertyBell",
                district = "District 3",
                rank = 7,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 29 else 480,
                achievementCount = 4
            ),
            LeaderboardEntry(
                userId = "user8",
                username = "FlagWaver",
                district = "District 2",
                rank = 8,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 22 else 410,
                achievementCount = 3
            ),
            LeaderboardEntry(
                userId = "user9",
                username = "AmericanDreamer",
                district = "District 5",
                rank = 9,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 18 else 350,
                achievementCount = 2
            ),
            LeaderboardEntry(
                userId = "user10",
                username = "ConstitutionalPatriot",
                district = "District 4",
                rank = 10,
                score = if (rankingType == LeaderboardManager.RankingType.COMMENT_ACTIVITY) 12 else 280,
                achievementCount = 1
            )
        )
        
        return when (rankingType) {
            LeaderboardManager.RankingType.POWER_SCORE -> baseList 
            LeaderboardManager.RankingType.PARTICIPATION -> baseList.map { it.copy(score = it.score / 2) }
            LeaderboardManager.RankingType.STREAK_DAYS -> baseList.map { it.copy(score = (it.score / 100).coerceAtMost(30)) }
            LeaderboardManager.RankingType.COMMENT_ACTIVITY -> baseList // Already set appropriate scores
        }
    }
} 