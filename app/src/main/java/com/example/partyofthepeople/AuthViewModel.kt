package com.example.partyofthepeople

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partyofthepeople.achievements.AchievementManager
import com.example.partyofthepeople.leaderboard.LeaderboardManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthViewModel"

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile
    private val db = FirebaseFirestore.getInstance()
    private val achievementManager = AchievementManager()
    private val leaderboardManager = LeaderboardManager()

    init {
        // Check if user is already signed in
        auth.currentUser?.let {
            Log.d(TAG, "User already signed in: ${it.uid}")
            fetchUserProfile()
        }
    }

    fun signInAnonymously() {
        Log.d(TAG, "Starting anonymous sign-in")
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Anonymous sign-in successful: ${result.user?.uid}")
                fetchUserProfile()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Anonymous sign-in failed", e)
                if (e is FirebaseNetworkException) {
                    // Create an offline profile if network is unavailable
                    Log.d(TAG, "Network unavailable, creating offline profile")
                    createOfflineProfile()
                }
            }
    }

    fun signInWithGoogle(credential: AuthCredential) {
        Log.d(TAG, "Starting Google sign-in")
        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                Log.d(TAG, "Google sign-in successful: ${result.user?.uid}")
                fetchUserProfile()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Google sign-in failed", e)
                if (e is FirebaseNetworkException) {
                    // Create an offline profile if network is unavailable
                    Log.d(TAG, "Network unavailable, creating offline profile")
                    createOfflineProfile()
                }
            }
    }

    private fun createOfflineProfile() {
        val profile = UserProfile(
            userId = "offline_user",
            username = "Offline Patriot",
            avatar = "🦅",
            joinDate = System.currentTimeMillis().toString(),
            freedomBucks = 100,
            badges = listOf("🦅"),
            stats = UserStats(),
            politicalRank = "Citizen",
            district = "Unknown",
            latestAchievement = null,
            patriotPoints = 0
        )
        _userProfile.value = profile
        Log.d(TAG, "Created offline profile: ${profile.username}")
    }

    private fun fetchUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                try {
                    Log.d(TAG, "Fetching user profile for ${user.uid}")
                    
                    // Try to get existing profile from Firestore
                    val userDoc = db.collection("users").document(user.uid).get().await()
                    
                    if (userDoc.exists()) {
                        // User exists in database - get their profile
                        val userData = userDoc.data
                        if (userData != null) {
                            val profile = com.example.partyofthepeople.utils.ProfileUtils.mapToUserProfile(userData)
                            _userProfile.value = profile
                            
                            // Check for login streak achievement
                            val newAchievements = achievementManager.recordDailyLogin(user.uid, profile)
                            if (newAchievements.isNotEmpty()) {
                                // Update achievements in state
                                val updatedAchievements = profile.achievements.toMutableList()
                                updatedAchievements.addAll(newAchievements)
                                
                                val updatedProfile = profile.copy(
                                    achievements = updatedAchievements,
                                    latestAchievement = newAchievements.lastOrNull() ?: profile.latestAchievement
                                )
                                
                                _userProfile.value = updatedProfile
                                
                                // Update leaderboard stats since stats have changed
                                leaderboardManager.updateUserStats(updatedProfile.userId, updatedProfile)
                            }
                            
                            Log.d(TAG, "User profile loaded from Firestore")
                        }
                    } else {
                        // New user - create profile
                        createNewUserProfile(user.uid)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching user profile", e)
                    createNewUserProfile(user.uid)
                }
            }
        } else {
            Log.w(TAG, "Attempted to fetch profile but user is null")
            _userProfile.value = null
        }
    }
    
    private suspend fun createNewUserProfile(userId: String) {
        try {
            Log.d(TAG, "Creating new user profile for $userId")
            val user = auth.currentUser
            
            // Initialize with default stats including new Profile System 2.0 fields
            val stats = UserStats(
                votesCast = 0,
                lawsPassed = 0,
                streakDays = 1, // Start with 1 day streak
                positiveInteractions = 0,
                participation = 5, // Initial participation for signing up
                powerScore = 100, // Base power score
                lastLoginTimestamp = System.currentTimeMillis()
            )
            
            // Basic welcome achievement everyone gets
            val welcomeAchievement = Achievement(
                id = "welcome_achievement",
                title = "New Patriot",
                description = "Joined the Freedom Fighters community",
                icon = "🦅",
                dateEarned = System.currentTimeMillis()
            )
            
            val profile = UserProfile(
                userId = userId,
                username = user?.displayName ?: "Patriot",
                avatar = user?.photoUrl?.toString() ?: "🦅",
                joinDate = System.currentTimeMillis().toString(),
                freedomBucks = 100,
                badges = listOf("🦅"),
                stats = stats,
                politicalRank = "Citizen",
                district = "Unknown",
                latestAchievement = welcomeAchievement,
                achievements = listOf(welcomeAchievement),
                preferences = UserPreferences(
                    darkMode = false,
                    notifications = true,
                    soundEffects = true
                ),
                patriotPoints = 0
            )
            
            // Save to Firestore
            val userData = mapOf(
                "userId" to profile.userId,
                "username" to profile.username,
                "email" to (user?.email ?: ""),
                "avatar" to profile.avatar,
                "joinDate" to (profile.joinDate.toLongOrNull() ?: System.currentTimeMillis()),
                "freedomBucks" to profile.freedomBucks,
                "badges" to profile.badges,
                "stats" to mapOf(
                    "votesCast" to stats.votesCast,
                    "lawsPassed" to stats.lawsPassed,
                    "streakDays" to stats.streakDays,
                    "participation" to stats.participation,
                    "positiveInteractions" to stats.positiveInteractions,
                    "powerScore" to stats.powerScore,
                    "lastLoginTimestamp" to stats.lastLoginTimestamp
                ),
                "politicalRank" to profile.politicalRank,
                "district" to profile.district,
                "achievements" to listOf(
                    mapOf(
                        "id" to welcomeAchievement.id,
                        "title" to welcomeAchievement.title,
                        "description" to welcomeAchievement.description,
                        "icon" to welcomeAchievement.icon,
                        "dateEarned" to welcomeAchievement.dateEarned
                    )
                ),
                "preferences" to mapOf(
                    "darkMode" to profile.preferences.darkMode,
                    "notifications" to profile.preferences.notifications,
                    "soundEffects" to profile.preferences.soundEffects
                ),
                "patriotPoints" to profile.patriotPoints
            )
            
            db.collection("users").document(userId).set(userData).await()
            
            _userProfile.value = profile
            Log.d(TAG, "New user profile created and saved to Firestore")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating new user profile", e)
        }
    }

    fun signOut() {
        _userProfile.value = null
    }
}