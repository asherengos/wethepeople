package com.example.partyofthepeople.profile

import android.util.Log
import com.example.partyofthepeople.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Calendar

/**
 * Manages user titles, including checking eligibility, awarding, and retrieving titles
 */
class TitleManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "TitleManager"
    
    /**
     * Categories of titles
     */
    enum class TitleCategory {
        PARTICIPATION,
        ACHIEVEMENT,
        COMMUNITY,
        PREMIUM,
        HUMOROUS
    }
    
    /**
     * Data class representing a title with its requirements
     */
    data class Title(
        val id: String,
        val displayName: String,
        val description: String,
        val category: TitleCategory,
        val iconEmoji: String,
        val rarity: TitleRarity = TitleRarity.COMMON
    )
    
    /**
     * Rarity levels for titles
     */
    enum class TitleRarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC,
        LEGENDARY
    }
    
    /**
     * All available titles in the system
     */
    val allTitles = listOf(
        // Participation Titles
        Title(
            id = "based_citizen",
            displayName = "Based Citizen",
            description = "Completed your first vote", 
            category = TitleCategory.PARTICIPATION,
            iconEmoji = "🗳️"
        ),
        Title(
            id = "active_voter",
            displayName = "Active Voter",
            description = "Voted 10 times",
            category = TitleCategory.PARTICIPATION,
            iconEmoji = "📊",
            rarity = TitleRarity.UNCOMMON
        ),
        Title(
            id = "dedicated_patriot",
            displayName = "Dedicated Patriot",
            description = "Achieved a 30-day login streak",
            category = TitleCategory.PARTICIPATION,
            iconEmoji = "🦅",
            rarity = TitleRarity.RARE
        ),
        Title(
            id = "political_junkie",
            displayName = "Political Junkie",
            description = "Voted in 5 different categories",
            category = TitleCategory.PARTICIPATION,
            iconEmoji = "📰",
            rarity = TitleRarity.UNCOMMON
        ),
        
        // Achievement Titles
        Title(
            id = "meme_master",
            displayName = "Meme Master",
            description = "Received 50+ upvotes on comments",
            category = TitleCategory.ACHIEVEMENT,
            iconEmoji = "😂",
            rarity = TitleRarity.RARE
        ),
        Title(
            id = "debate_champion",
            displayName = "Debate Champion",
            description = "Won several debates",
            category = TitleCategory.ACHIEVEMENT,
            iconEmoji = "🎯",
            rarity = TitleRarity.RARE
        ),
        Title(
            id = "party_loyalist",
            displayName = "Party Loyalist",
            description = "Consistently voted with majority",
            category = TitleCategory.ACHIEVEMENT,
            iconEmoji = "🏛️",
            rarity = TitleRarity.UNCOMMON
        ),
        Title(
            id = "independent_thinker",
            displayName = "Independent Thinker",
            description = "Often voted against majority",
            category = TitleCategory.ACHIEVEMENT,
            iconEmoji = "🧠",
            rarity = TitleRarity.UNCOMMON
        ),
        
        // Community Titles
        Title(
            id = "campaign_manager",
            displayName = "Campaign Manager",
            description = "Referred 3+ friends",
            category = TitleCategory.COMMUNITY,
            iconEmoji = "📣",
            rarity = TitleRarity.RARE
        ),
        Title(
            id = "social_activist",
            displayName = "Social Activist",
            description = "Shared content on social media",
            category = TitleCategory.COMMUNITY,
            iconEmoji = "📱",
            rarity = TitleRarity.UNCOMMON
        ),
        Title(
            id = "coalition_builder",
            displayName = "Coalition Builder",
            description = "Engaged positively with opposite views",
            category = TitleCategory.COMMUNITY,
            iconEmoji = "🤝",
            rarity = TitleRarity.RARE
        ),
        
        // Premium Titles
        Title(
            id = "freedom_supporter",
            displayName = "Freedom Supporter",
            description = "Made any in-app purchase",
            category = TitleCategory.PREMIUM,
            iconEmoji = "💰",
            rarity = TitleRarity.RARE
        ),
        Title(
            id = "theme_collector",
            displayName = "Theme Collector",
            description = "Owns 3+ profile themes",
            category = TitleCategory.PREMIUM,
            iconEmoji = "🎨",
            rarity = TitleRarity.RARE
        ),
        
        // Humorous Titles
        Title(
            id = "night_owl",
            displayName = "Night Owl",
            description = "Active after midnight",
            category = TitleCategory.HUMOROUS,
            iconEmoji = "🦉",
            rarity = TitleRarity.UNCOMMON
        ),
        Title(
            id = "contrarian",
            displayName = "Contrarian",
            description = "Often votes against the crowd",
            category = TitleCategory.HUMOROUS,
            iconEmoji = "🔄",
            rarity = TitleRarity.UNCOMMON
        ),
        Title(
            id = "swing_voter",
            displayName = "Swing Voter",
            description = "Changed position multiple times",
            category = TitleCategory.HUMOROUS,
            iconEmoji = "🔄",
            rarity = TitleRarity.UNCOMMON
        )
    )
    
    /**
     * Get all titles a user has earned
     */
    suspend fun getUserTitles(userId: String): List<String> {
        try {
            val userDoc = db.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                return emptyList()
            }
            
            return userDoc.get("titles") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user titles: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Get complete title objects for all titles a user has earned
     */
    suspend fun getUserTitleObjects(userId: String): List<Title> {
        val titleIds = getUserTitles(userId)
        return allTitles.filter { it.id in titleIds }
    }
    
    /**
     * Get a title by its ID
     */
    fun getTitleById(titleId: String): Title? {
        return allTitles.find { it.id == titleId }
    }
    
    /**
     * Check if a user has earned a specific title
     */
    suspend fun hasTitle(userId: String, titleId: String): Boolean {
        val titles = getUserTitles(userId)
        return titleId in titles
    }
    
    /**
     * Award a title to a user
     */
    suspend fun awardTitle(userId: String, titleId: String): Boolean {
        try {
            // Make sure the title exists
            val title = getTitleById(titleId) ?: return false
            
            // Check if user already has this title
            if (hasTitle(userId, titleId)) {
                return true
            }
            
            // Award the title
            val userRef = db.collection("users").document(userId)
            db.runTransaction { transaction ->
                transaction.update(userRef, "titles", com.google.firebase.firestore.FieldValue.arrayUnion(titleId))
                
                // Also record when the title was earned
                val titleHistoryRef = userRef.collection("titleHistory").document(titleId)
                transaction.set(titleHistoryRef, mapOf(
                    "titleId" to titleId,
                    "earnedAt" to System.currentTimeMillis(),
                    "displayName" to title.displayName,
                    "category" to title.category.name
                ))
            }.await()
            
            Log.d(TAG, "Title awarded: $titleId to user: $userId")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding title: ${e.message}")
            return false
        }
    }
    
    /**
     * Set a user's preferred title
     */
    suspend fun setPreferredTitle(userId: String, titleId: String): Boolean {
        try {
            // Make sure the user has this title
            if (!hasTitle(userId, titleId)) {
                Log.d(TAG, "User doesn't have this title: $titleId")
                return false
            }
            
            val userRef = db.collection("users").document(userId)
            userRef.update("preferences.preferredTitleId", titleId).await()
            
            Log.d(TAG, "Updated preferred title: $titleId for user: $userId")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting preferred title: ${e.message}")
            return false
        }
    }
    
    /**
     * Get a user's preferred title
     */
    suspend fun getPreferredTitle(userId: String): String? {
        try {
            val userDoc = db.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                return null
            }
            
            val preferences = userDoc.get("preferences") as? Map<String, Any> ?: return null
            return preferences["preferredTitleId"] as? String
        } catch (e: Exception) {
            Log.e(TAG, "Error getting preferred title: ${e.message}")
            return null
        }
    }
    
    /**
     * Check and award titles based on user profile stats and activities
     * This should be called periodically to check for new titles
     */
    suspend fun checkAndAwardTitles(userProfile: UserProfile): List<Title> {
        val newlyAwardedTitles = mutableListOf<Title>()
        val userId = userProfile.userId
        
        try {
            // Check participation titles
            if (userProfile.stats.votesCast > 0 && !hasTitle(userId, "based_citizen")) {
                if (awardTitle(userId, "based_citizen")) {
                    getTitleById("based_citizen")?.let { newlyAwardedTitles.add(it) }
                }
            }
            
            if (userProfile.stats.votesCast >= 10 && !hasTitle(userId, "active_voter")) {
                if (awardTitle(userId, "active_voter")) {
                    getTitleById("active_voter")?.let { newlyAwardedTitles.add(it) }
                }
            }
            
            if (userProfile.stats.streakDays >= 30 && !hasTitle(userId, "dedicated_patriot")) {
                if (awardTitle(userId, "dedicated_patriot")) {
                    getTitleById("dedicated_patriot")?.let { newlyAwardedTitles.add(it) }
                }
            }
            
            // Check premium titles
            if (userProfile.ownedThemes.size >= 3 && !hasTitle(userId, "theme_collector")) {
                if (awardTitle(userId, "theme_collector")) {
                    getTitleById("theme_collector")?.let { newlyAwardedTitles.add(it) }
                }
            }
            
            // Check humorous titles - night owl
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (currentHour in 0..4 && !hasTitle(userId, "night_owl")) {
                if (awardTitle(userId, "night_owl")) {
                    getTitleById("night_owl")?.let { newlyAwardedTitles.add(it) }
                }
            }
            
            // More conditions can be added for other titles
            
            return newlyAwardedTitles
        } catch (e: Exception) {
            Log.e(TAG, "Error checking and awarding titles: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Purchase a title showcase for a specific title
     * This allows a user to highlight a title with special effects
     */
    suspend fun purchaseTitleShowcase(userId: String, titleId: String, freedomBucks: Int): Boolean {
        try {
            val showcasePrice = 50 // Cost in Freedom Bucks
            
            // Check if user has enough Freedom Bucks
            if (freedomBucks < showcasePrice) {
                Log.d(TAG, "Not enough Freedom Bucks for title showcase")
                return false
            }
            
            // Check if user has this title
            if (!hasTitle(userId, titleId)) {
                Log.d(TAG, "User doesn't have this title: $titleId")
                return false
            }
            
            // Calculate showcase end time (1 week)
            val showcaseEndTime = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
            
            // Update Firestore
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                // Verify user's current Freedom Bucks
                val snapshot = transaction.get(userRef)
                val currentFreedomBucks = snapshot.getLong("freedomBucks") ?: 0
                
                if (currentFreedomBucks < showcasePrice) {
                    throw Exception("Insufficient Freedom Bucks")
                }
                
                // Deduct Freedom Bucks
                transaction.update(userRef, "freedomBucks", currentFreedomBucks - showcasePrice)
                
                // Set showcase info
                transaction.update(userRef, 
                    "showcasedTitleId", titleId,
                    "showcaseEndTime", showcaseEndTime
                )
            }.await()
            
            Log.d(TAG, "Successfully purchased title showcase for: $titleId until: $showcaseEndTime")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing title showcase: ${e.message}")
            return false
        }
    }
    
    /**
     * Check if a user has a showcased title
     */
    suspend fun getShowcasedTitle(userId: String): Pair<String?, Long> {
        try {
            val userDoc = db.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                return Pair(null, 0L)
            }
            
            val titleId = userDoc.getString("showcasedTitleId")
            val endTime = userDoc.getLong("showcaseEndTime") ?: 0L
            
            // Check if showcase has expired
            if (endTime < System.currentTimeMillis()) {
                return Pair(null, 0L)
            }
            
            return Pair(titleId, endTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting showcased title: ${e.message}")
            return Pair(null, 0L)
        }
    }
} 