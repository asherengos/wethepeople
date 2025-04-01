package com.example.partyofthepeople.leaderboard

import android.util.Log
import com.example.partyofthepeople.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Manages leaderboard operations - retrieving rankings, updating user scores, etc.
 */
class LeaderboardManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "LeaderboardManager"
    
    /**
     * Ranking type options
     */
    enum class RankingType {
        POWER_SCORE,       // Overall power score
        PARTICIPATION,     // General participation
        STREAK_DAYS,       // Login streak
        COMMENT_ACTIVITY   // Engagement through comments/interactions
    }
    
    private var currentRankingType: RankingType? = null
    private var currentUserId: String? = null
    
    /**
     * Get top ranked users by a specific ranking type
     */
    suspend fun getTopRankedUsers(rankingType: RankingType): List<LeaderboardEntry> {
        try {
            currentRankingType = rankingType
            currentUserId = Firebase.auth.currentUser?.uid
            
            // Define the field to query based on ranking type
            val statsField = when (rankingType) {
                RankingType.POWER_SCORE -> "stats.powerScore"
                RankingType.PARTICIPATION -> "stats.participation"
                RankingType.STREAK_DAYS -> "stats.streakDays"
                RankingType.COMMENT_ACTIVITY -> "stats.positiveInteractions"
            }
            
            // Query boosted users first (users who have paid for profile boost)
            val currentTime = System.currentTimeMillis()
            val boostedUsersSnapshot = db.collection("users")
                .whereGreaterThan("profileBoostEndTime", currentTime)
                .orderBy("profileBoostEndTime", Query.Direction.DESCENDING)
                .orderBy(statsField, Query.Direction.DESCENDING)
                .limit(5) // Limit to top 5 boosted users
                .get()
                .await()
            
            // Then query non-boosted users
            val nonBoostedUsersSnapshot = db.collection("users")
                .whereLessThanOrEqualTo("profileBoostEndTime", currentTime)
                .orderBy("profileBoostEndTime", Query.Direction.DESCENDING)
                .orderBy(statsField, Query.Direction.DESCENDING)
                .limit(20) // Limit to top 20 non-boosted users
                .get()
                .await()
            
            // Process boosted users
            val boostedUsers = boostedUsersSnapshot.documents.mapNotNull {
                it.data?.let { data -> mapUserToLeaderboardEntry(data, 0, currentUserId) }
            }
            
            // Process non-boosted users
            val nonBoostedUsers = nonBoostedUsersSnapshot.documents.mapNotNull {
                it.data?.let { data -> mapUserToLeaderboardEntry(data, 0, currentUserId) }
            }
            
            // Combine both lists and assign final ranks
            val allUsers = (boostedUsers + nonBoostedUsers).sortedByDescending { it.score }
            return allUsers.mapIndexed { index, entry ->
                entry.copy(rank = index + 1)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching top ranked users: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Get a user's rank in the global leaderboard
     */
    suspend fun getUserRank(userId: String, rankingType: RankingType): LeaderboardEntry? {
        try {
            currentUserId = userId
            currentRankingType = rankingType
            val statsField = when (rankingType) {
                RankingType.POWER_SCORE -> "stats.powerScore"
                RankingType.PARTICIPATION -> "stats.participation"
                RankingType.STREAK_DAYS -> "stats.streakDays"
                RankingType.COMMENT_ACTIVITY -> "stats.positiveInteractions"
            }
            
            // Get the user document first
            val userDoc = db.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                return null
            }
            
            // Convert to map for consistent processing
            val userData = userDoc.data as? Map<String, Any> ?: return null
            
            // Get the stats value based on ranking type with type safety
            val statsData = userData["stats"] ?: mapOf<String, Any>()
            if (statsData !is Map<*, *>) {
                Log.e(TAG, "Invalid stats data type for user ${userData["username"]}")
                return null
            }
            
            val statsMap = statsData.mapKeys { it.key.toString() }
                .mapValues { (_, value) -> value as? Number ?: 0 }
            val score = when (rankingType) {
                RankingType.POWER_SCORE -> (statsMap["powerScore"] as? Number)?.toInt() ?: 0
                RankingType.PARTICIPATION -> (statsMap["participation"] as? Number)?.toInt() ?: 0
                RankingType.STREAK_DAYS -> (statsMap["streakDays"] as? Number)?.toInt() ?: 0
                RankingType.COMMENT_ACTIVITY -> (statsMap["positiveInteractions"] as? Number)?.toInt() ?: 0
            }
            
            // Query to count how many users have higher scores
            val higherRankedQuery = db.collection("users")
                .whereGreaterThan(statsField, score)
                .count()
                .get(AggregateSource.SERVER)
                .await()
            
            // Rank is the number of users with higher scores + 1
            val rank = higherRankedQuery.count.toInt() + 1
            
            return LeaderboardEntry(
                userId = userId,
                username = userData["username"] as? String ?: "Unknown",
                district = userData["district"] as? String ?: "Unknown",
                rank = rank,
                score = score,
                achievementCount = (userData["achievements"] as? List<*>)?.size ?: 0,
                isCurrentUser = true,
                isBoosted = (userData["profileBoostEndTime"] as? Long ?: 0L) > System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user rank: ${e.message}")
            return null
        }
    }
    
    /**
     * Get leaderboard for a specific district
     */
    suspend fun getDistrictLeaderboard(district: String): List<LeaderboardEntry> {
        try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("district", district)
                .orderBy("stats.powerScore", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
            
            if (querySnapshot.documents.isEmpty()) {
                return emptyList()
            }
            
            // Process the results
            return querySnapshot.documents.mapIndexed { index, document ->
                val userData = document.data ?: mapOf<String, Any>()
                
                LeaderboardEntry(
                    userId = document.id,
                    username = userData["username"] as? String ?: "Unknown",
                    district = district,
                    rank = index + 1,
                    score = (userData["stats"] as? Map<String, Any>)?.get("powerScore") as? Int ?: 0,
                    achievementCount = (userData["achievements"] as? List<*>)?.size ?: 0,
                    isCurrentUser = document.id == currentUserId,
                    isBoosted = (userData["profileBoostEndTime"] as? Long ?: 0L) > System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting district leaderboard: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Update leaderboard with latest user stats (should be called after significant stat changes)
     */
    fun updateUserStats(userId: String, profile: UserProfile) {
        // Placeholder implementation for updating leaderboard stats
    }
    
    private fun mapUserToLeaderboardEntry(userData: Map<String, Any>, rank: Int, currentUserId: String?): LeaderboardEntry {
        val userId = userData["userId"] as? String ?: ""
        val username = userData["username"] as? String ?: "Unknown"
        val district = userData["district"] as? String ?: "Unknown"
        
        val statsData = userData["stats"]
        val statsMap = when {
            statsData is Map<*, *> -> statsData.mapKeys { it.key.toString() }
                .mapValues { (_, value) -> value as? Number ?: 0 }
            else -> mapOf<String, Number>()
        }
        
        val score = when (currentRankingType) {
            RankingType.POWER_SCORE -> (statsMap["powerScore"] as? Number)?.toInt() ?: 0
            RankingType.PARTICIPATION -> (statsMap["participation"] as? Number)?.toInt() ?: 0
            RankingType.STREAK_DAYS -> (statsMap["streakDays"] as? Number)?.toInt() ?: 0
            RankingType.COMMENT_ACTIVITY -> (statsMap["positiveInteractions"] as? Number)?.toInt() ?: 0
            else -> (statsMap["powerScore"] as? Number)?.toInt() ?: 0
        }
        
        val achievementsData = userData["achievements"]
        val achievementCount = when {
            achievementsData is List<*> -> achievementsData.size
            else -> 0
        }
        
        val boostEndTime = userData["profileBoostEndTime"] as? Long ?: 0L
        val isBoosted = boostEndTime > System.currentTimeMillis()
        
        return LeaderboardEntry(
            userId = userId,
            username = username,
            district = district,
            rank = rank,
            score = score,
            achievementCount = achievementCount,
            isCurrentUser = userId == currentUserId,
            isBoosted = isBoosted
        )
    }
} 