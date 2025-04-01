package com.example.partyofthepeople.profile

import android.util.Log
import com.example.partyofthepeople.UserProfile
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Extension properties for backward compatibility
 */
// Map to store per-user theme data
private val userThemesMap = mutableMapOf<String, List<String>>()
// Map to store per-user title data
private val userTitlesMap = mutableMapOf<String, List<String>>()

// Extension properties for UserProfile
val UserProfile.ownedThemes: List<String>
    get() = userThemesMap[userId] ?: listOf("Default")

val UserProfile.titles: List<String>
    get() = userTitlesMap[userId] ?: listOf("Rookie")

/**
 * Manages profile customization, themes, and premium features
 */
class ProfileManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "ProfileManager"
    
    // Placeholder for owned themes
    val defaultThemes: List<String> = listOf("Default", "Dark Mode", "Retro")

    // Placeholder for titles
    val defaultTitles: List<String> = listOf("Rookie", "Veteran", "Champion")

    // Placeholder for profile boost end time
    var profileBoostEndTime: Long = System.currentTimeMillis() + 86400000 // 24 hours from now
    
    // Function to load user's themes
    fun loadUserThemes(userId: String, themes: List<String>) {
        userThemesMap[userId] = themes
    }
    
    // Function to load user's titles
    fun loadUserTitles(userId: String, titles: List<String>) {
        userTitlesMap[userId] = titles
    }
    
    /**
     * Purchase a profile theme using Freedom Bucks
     * @return true if purchase was successful, false otherwise
     */
    suspend fun purchaseTheme(userId: String, userProfile: UserProfile, themeId: String): Boolean {
        try {
            val theme = ProfileThemes.getThemeById(themeId)
            
            // Check if user already owns this theme
            if (userProfile.ownedThemes.contains(themeId)) {
                Log.d(TAG, "User already owns theme: $themeId")
                return true
            }
            
            // Check if user has enough Freedom Bucks
            if (userProfile.freedomBucks < theme.price) {
                Log.d(TAG, "Not enough Freedom Bucks to purchase theme: $themeId. Required: ${theme.price}, Available: ${userProfile.freedomBucks}")
                return false
            }
            
            // Update Firestore
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                // Get latest user data
                val snapshot = transaction.get(userRef)
                val currentFreedomBucks = snapshot.getLong("freedomBucks") ?: 0
                
                // Verify again with the most up-to-date data
                if (currentFreedomBucks < theme.price) {
                    throw Exception("Insufficient Freedom Bucks")
                }
                
                // Deduct Freedom Bucks
                transaction.update(userRef, "freedomBucks", currentFreedomBucks - theme.price)
                
                // Add theme to owned themes
                transaction.update(userRef, "ownedThemes", FieldValue.arrayUnion(themeId))
            }.await()
            
            Log.d(TAG, "Successfully purchased theme: $themeId")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing theme: ${e.message}")
            return false
        }
    }
    
    /**
     * Apply a theme to the user's profile
     */
    suspend fun applyTheme(userId: String, userProfile: UserProfile, themeId: String): Boolean {
        try {
            // Check if user owns this theme
            if (!userProfile.ownedThemes.contains(themeId) && themeId != "default") {
                Log.d(TAG, "User does not own theme: $themeId")
                return false
            }
            
            // Update Firestore
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                transaction.update(userRef, "preferences.theme", themeId)
            }.await()
            
            Log.d(TAG, "Successfully applied theme: $themeId")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying theme: ${e.message}")
            return false
        }
    }
    
    /**
     * Set preferred title from user's earned titles
     */
    suspend fun setPreferredTitle(userId: String, userProfile: UserProfile, titleId: String): Boolean {
        try {
            // Check if user has this title
            if (!userProfile.titles.contains(titleId)) {
                Log.d(TAG, "User does not have title: $titleId")
                return false
            }
            
            // Update Firestore
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                transaction.update(userRef, "preferences.preferredTitleId", titleId)
            }.await()
            
            Log.d(TAG, "Successfully set preferred title: $titleId")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting preferred title: ${e.message}")
            return false
        }
    }
    
    /**
     * Purchase a temporary profile boost (highlights in leaderboard for 24 hours)
     */
    suspend fun purchaseProfileBoost(userId: String, userProfile: UserProfile, durationHours: Int = 24): Boolean {
        try {
            val boostPrice = 50 // Cost in Freedom Bucks
            
            // Check if user has enough Freedom Bucks
            if (userProfile.freedomBucks < boostPrice) {
                Log.d(TAG, "Not enough Freedom Bucks for profile boost. Required: $boostPrice, Available: ${userProfile.freedomBucks}")
                return false
            }
            
            // Calculate boost end time
            val boostEndTime = System.currentTimeMillis() + (durationHours * 60 * 60 * 1000)
            
            // Update Firestore
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                // Get latest user data
                val snapshot = transaction.get(userRef)
                val currentFreedomBucks = snapshot.getLong("freedomBucks") ?: 0
                
                // Verify again with the most up-to-date data
                if (currentFreedomBucks < boostPrice) {
                    throw Exception("Insufficient Freedom Bucks")
                }
                
                // Deduct Freedom Bucks
                transaction.update(userRef, "freedomBucks", currentFreedomBucks - boostPrice)
                
                // Set boost end time
                transaction.update(userRef, "profileBoostEndTime", boostEndTime)
            }.await()
            
            Log.d(TAG, "Successfully purchased profile boost until: $boostEndTime")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing profile boost: ${e.message}")
            return false
        }
    }
    
    /**
     * Check if a user's profile is currently boosted
     */
    fun isProfileBoosted(userProfile: UserProfile): Boolean {
        val currentTime = System.currentTimeMillis()
        // For compatibility with profiles that don't have profileBoostEndTime
        val boostEndTime = getProfileBoostEndTime(userProfile)
        return boostEndTime > currentTime
    }
    
    /**
     * Get remaining time for profile boost in minutes
     */
    fun getProfileBoostTimeRemaining(userProfile: UserProfile): Int {
        val currentTime = System.currentTimeMillis()
        // For compatibility with profiles that don't have profileBoostEndTime
        val boostEndTime = getProfileBoostEndTime(userProfile)
        val remainingMillis = boostEndTime - currentTime
        
        if (remainingMillis <= 0) return 0
        
        return (remainingMillis / (60 * 1000)).toInt()
    }
    
    /**
     * Helper method to get profile boost end time with a default value
     */
    private fun getProfileBoostEndTime(userProfile: UserProfile): Long {
        // Get from user profile field or extension data, or use default (no boost)
        return profileBoostEndTime // Use the class member as fallback
    }
} 