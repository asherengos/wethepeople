package com.example.partyofthepeople.leaderboard

/**
 * Represents a single entry in the leaderboard
 */
data class LeaderboardEntry(
    val userId: String,
    val username: String,
    val district: String,
    val rank: Int,
    val score: Int,
    val achievementCount: Int,
    val isCurrentUser: Boolean = false,
    val isBoosted: Boolean = false
) 