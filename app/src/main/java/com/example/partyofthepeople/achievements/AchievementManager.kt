package com.example.partyofthepeople.achievements

import android.util.Log
import com.example.partyofthepeople.Achievement
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.utils.ProfileUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Manages achievement tracking, checking, and awarding
 */
class AchievementManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "AchievementManager"
    
    /**
     * Check all available achievements for a user
     */
    suspend fun checkAchievements(userId: String, userProfile: UserProfile): List<Achievement> {
        val newlyUnlockedAchievements = mutableListOf<Achievement>()
        
        try {
            // Get all available achievements
            val availableAchievements = getAvailableAchievements()
            
            // Get user's already unlocked achievements
            val userAchievementIds = userProfile.achievements.map { it.id }
            
            // Check each achievement
            for (achievement in availableAchievements) {
                if (achievement.id !in userAchievementIds && isAchievementUnlocked(achievement, userProfile)) {
                    // Award the achievement
                    awardAchievement(userId, achievement)
                    newlyUnlockedAchievements.add(achievement)
                    
                    Log.d(TAG, "Unlocked achievement: ${achievement.title} for user $userId")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking achievements: ${e.message}")
        }
        
        return newlyUnlockedAchievements
    }
    
    /**
     * Award an achievement to a user
     */
    private suspend fun awardAchievement(userId: String, achievement: Achievement) {
        try {
            // Get the user document reference
            val userRef = db.collection("users").document(userId)
            
            // Update the achievement in Firestore
            val updatedAchievement = achievement.copy(
                dateEarned = System.currentTimeMillis()
            )
            
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                
                // Get current achievements with type safety
                val achievementsData = snapshot.get("achievements") ?: listOf<Map<String, Any>>()
                if (achievementsData !is List<*>) {
                    Log.e(TAG, "Invalid achievements data type")
                    return@runTransaction
                }
                
                val achievements = achievementsData.mapNotNull { item ->
                    if (item !is Map<*, *>) null
                    else item.mapKeys { it.key.toString() }
                        .mapValues { (_, value) -> value as Any }
                }
                
                // Create updated achievement list
                val updatedAchievements = achievements.toMutableList()
                
                // Convert achievement to map
                val achievementMap = mapOf(
                    "id" to updatedAchievement.id,
                    "title" to updatedAchievement.title,
                    "description" to updatedAchievement.description,
                    "icon" to updatedAchievement.icon,
                    "dateEarned" to updatedAchievement.dateEarned
                )
                
                updatedAchievements.add(achievementMap)
                
                // Update Firestore
                transaction.update(userRef, "achievements", updatedAchievements)
                transaction.update(userRef, "latestAchievement", achievementMap)
            }.await()
            
            // Increment participation and positive interactions
            // Achievements increase both stats since they represent positive engagement
            ProfileUtils.incrementParticipation(userId, 5)
            ProfileUtils.incrementPositiveInteractions(userId, 10)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding achievement: ${e.message}")
        }
    }
    
    /**
     * Check if an achievement is unlocked based on its criteria
     */
    private fun isAchievementUnlocked(achievement: Achievement, userProfile: UserProfile): Boolean {
        return when (achievement.id) {
            // Participation achievements
            "votes_cast_10" -> userProfile.stats.votesCast >= 10
            "votes_cast_50" -> userProfile.stats.votesCast >= 50
            "votes_cast_100" -> userProfile.stats.votesCast >= 100
            
            // Streak achievements
            "streak_3_days" -> userProfile.stats.streakDays >= 3
            "streak_7_days" -> userProfile.stats.streakDays >= 7
            "streak_30_days" -> userProfile.stats.streakDays >= 30
            
            // Engagement achievements
            "high_participation" -> userProfile.stats.participation >= 50
            "positive_contributor" -> userProfile.stats.positiveInteractions >= 25
            
            // Power achievements
            "power_score_200" -> userProfile.stats.powerScore >= 200
            "power_score_500" -> userProfile.stats.powerScore >= 500
            
            // Freedom bucks achievements
            "freedom_bucks_1000" -> userProfile.freedomBucks >= 1000
            "freedom_bucks_5000" -> userProfile.freedomBucks >= 5000
            
            // Default case for custom or unknown achievements
            else -> false
        }
    }
    
    /**
     * Get all available achievements
     */
    private fun getAvailableAchievements(): List<Achievement> {
        return listOf(
            // Participation achievements
            Achievement(
                id = "welcome_achievement",
                title = "New Patriot",
                description = "Joined the Freedom Fighters community",
                icon = "🦅",
                dateEarned = 0
            ),
            Achievement(
                id = "votes_cast_10",
                title = "Novice Voter",
                description = "Cast 10 votes on proposals",
                icon = "🗳️",
                dateEarned = 0
            ),
            Achievement(
                id = "votes_cast_50",
                title = "Active Citizen",
                description = "Cast 50 votes on proposals",
                icon = "📝",
                dateEarned = 0
            ),
            Achievement(
                id = "votes_cast_100",
                title = "Democracy Enthusiast",
                description = "Cast 100 votes on proposals",
                icon = "🏆",
                dateEarned = 0
            ),
            
            // Streak achievements
            Achievement(
                id = "streak_3_days",
                title = "Dedicated Patriot",
                description = "Logged in for 3 consecutive days",
                icon = "🔥",
                dateEarned = 0
            ),
            Achievement(
                id = "streak_7_days",
                title = "Weekly Warrior",
                description = "Logged in for 7 consecutive days",
                icon = "📅",
                dateEarned = 0
            ),
            Achievement(
                id = "streak_30_days",
                title = "Freedom Fighter Veteran",
                description = "Logged in for 30 consecutive days",
                icon = "🦅",
                dateEarned = 0
            ),
            
            // Engagement achievements
            Achievement(
                id = "high_participation",
                title = "Active Participant",
                description = "Achieved 50+ participation points",
                icon = "🎯",
                dateEarned = 0
            ),
            Achievement(
                id = "positive_contributor",
                title = "Positive Contributor",
                description = "Received 25+ positive interaction points",
                icon = "👍",
                dateEarned = 0
            ),
            
            // Power achievements
            Achievement(
                id = "power_score_200",
                title = "Rising Star",
                description = "Reached a Power Score of 200",
                icon = "⭐",
                dateEarned = 0
            ),
            Achievement(
                id = "power_score_500",
                title = "Influential Leader",
                description = "Reached a Power Score of 500",
                icon = "🌟",
                dateEarned = 0
            ),
            
            // Freedom bucks achievements
            Achievement(
                id = "freedom_bucks_1000",
                title = "Freedom Investor",
                description = "Accumulated 1,000 Freedom Bucks",
                icon = "💰",
                dateEarned = 0
            ),
            Achievement(
                id = "freedom_bucks_5000",
                title = "Freedom Tycoon",
                description = "Accumulated 5,000 Freedom Bucks",
                icon = "💎",
                dateEarned = 0
            )
        )
    }
    
    /**
     * Trigger achievement check for specific activity
     */
    suspend fun checkForAchievement(userId: String, userProfile: UserProfile, action: String): List<Achievement> {
        // Increment participation based on action weight
        val participationAmount = when (action) {
            "vote" -> 1
            "comment" -> 2
            "proposal" -> 5
            "login" -> 1
            else -> 1
        }
        
        // Update participation
        ProfileUtils.incrementParticipation(userId, participationAmount)
        
        // Check for newly unlocked achievements
        return checkAchievements(userId, userProfile)
    }
    
    /**
     * Records a vote and checks for any achievements unlocked
     * @return List of new achievements earned
     */
    fun recordVote(userId: String, profile: UserProfile, supported: Boolean): List<Achievement> {
        // Placeholder implementation
        return emptyList()
    }
    
    /**
     * Records a daily login and checks for streak achievements
     * @return List of new achievements earned
     */
    fun recordDailyLogin(userId: String, profile: UserProfile): List<Achievement> {
        // Placeholder implementation
        return emptyList()
    }
} 