package com.example.partyofthepeople

/**
 * Represents a user profile in the application
 */
data class UserProfile(
    val userId: String = "",
    val username: String = "Freedom Fighter",
    val email: String = "",
    val avatar: String = "🦅",
    val joinDate: String = System.currentTimeMillis().toString(),
    val district: String = "District 1",
    val politicalRank: String = "Citizen",
    val freedomBucks: Int = 100,
    val badges: List<String> = listOf("🦅"),
    val stats: UserStats = UserStats(),
    val preferences: UserPreferences = UserPreferences(),
    val achievements: List<Achievement> = emptyList(),
    val latestAchievement: Achievement? = null,
    val voteHistory: List<VoteRecord> = emptyList(),
    val matchPercentages: Map<String, Int> = emptyMap(),
    val patriotPoints: Int = 0
)

data class UserStats(
    val votesCast: Int = 0,
    val lawsPassed: Int = 0,
    val streakDays: Int = 0,
    val participation: Int = 0,
    val positiveInteractions: Int = 0,
    val powerScore: Int = 100,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "🏆",
    val dateEarned: Long = System.currentTimeMillis()
)

data class UserPreferences(
    val darkMode: Boolean = false,
    val notifications: Boolean = true,
    val soundEffects: Boolean = true,
    val theme: String = "Default",
    val preferredTitleId: String = ""
)

data class VoteRecord(
    val proposalId: String = "",
    val proposalTitle: String = "",
    val supported: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General"
)

data class StateStats(
    val freedomFighters: Int = 0,
    val activeProposals: Int = 0,
    val lawsPassed: Int = 0
)

data class LeaderboardEntry(
    val userId: String = "",
    val username: String = "",
    val avatar: String = "🦅",
    val score: Int = 0,
    val rank: Int = 0
)

/**
 * Represents a bipartisan match between users
 */
data class BipartisanMatch(
    val userId: String = "",
    val username: String = "",
    val avatar: String = "🦅",
    val matchPercentage: Int = 0,
    val similarityType: SimilarityType = SimilarityType.MIXED
)

enum class SimilarityType {
    SIMILAR, OPPOSITE, MIXED
} 