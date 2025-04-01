package com.example.partyofthepeople.utils

import android.util.Log
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.UserStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utilities for managing user profiles
 */
object ProfileUtils {
    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "ProfileUtils"
    
    /**
     * Calculate the PowerScore based on participation and positive interactions
     * Formula: Base 100 + (participation * 0.5) + (positiveInteractions * 0.8)
     */
    fun calculatePowerScore(participation: Int, positiveInteractions: Int): Int {
        val baseScore = 100
        val participationFactor = participation * 0.5
        val interactionsFactor = positiveInteractions * 0.8
        
        return (baseScore + participationFactor + interactionsFactor).toInt()
    }
    
    /**
     * Update the user's stats in Firestore
     */
    suspend fun updateUserStats(userId: String, stats: UserStats) {
        val db = FirebaseFirestore.getInstance()
        val updatedStats = stats.copy(
            powerScore = calculatePowerScore(stats.participation, stats.positiveInteractions)
        )
        
        db.collection("users").document(userId)
            .update("stats", updatedStats)
            .await()
    }
    
    /**
     * Increment the participation stat for a user
     */
    suspend fun incrementParticipation(userId: String, amount: Int = 1) {
        try {
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val statsData = snapshot.get("stats") ?: mapOf<String, Any>()
                if (statsData !is Map<*, *>) {
                    Log.e(TAG, "Invalid stats data type for user $userId")
                    return@runTransaction
                }
                
                val currentStats = statsData.mapKeys { it.key.toString() }
                    .mapValues { (_, value) -> value as? Number ?: 0 }
                val currentParticipation = currentStats["participation"] as? Long ?: 0
                
                transaction.update(userRef, "stats.participation", currentParticipation + amount)
                
                // Also update power score when participation changes
                updatePowerScore(transaction, userRef, snapshot)
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing participation: ${e.message}")
        }
    }
    
    /**
     * Increment the positive interactions stat for a user
     */
    suspend fun incrementPositiveInteractions(userId: String, amount: Int = 1) {
        try {
            val userRef = db.collection("users").document(userId)
            
            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val statsData = snapshot.get("stats") ?: mapOf<String, Any>()
                if (statsData !is Map<*, *>) {
                    Log.e(TAG, "Invalid stats data type for user $userId")
                    return@runTransaction
                }
                
                val currentStats = statsData.mapKeys { it.key.toString() }
                    .mapValues { (_, value) -> value as? Number ?: 0 }
                val currentPositiveInteractions = currentStats["positiveInteractions"] as? Long ?: 0
                
                transaction.update(userRef, "stats.positiveInteractions", currentPositiveInteractions + amount)
                
                // Also update power score when positive interactions change
                updatePowerScore(transaction, userRef, snapshot)
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error incrementing positive interactions: ${e.message}")
        }
    }
    
    /**
     * Update the power score for a user based on their stats
     */
    private fun updatePowerScore(
        transaction: com.google.firebase.firestore.Transaction,
        userRef: com.google.firebase.firestore.DocumentReference,
        snapshot: com.google.firebase.firestore.DocumentSnapshot
    ) {
        try {
            val statsData = snapshot.get("stats") ?: mapOf<String, Any>()
            if (statsData !is Map<*, *>) {
                Log.e(TAG, "Invalid stats data type in updatePowerScore")
                return
            }
            
            val currentStats = statsData.mapKeys { it.key.toString() }
                .mapValues { (_, value) -> value as? Number ?: 0 }
            
            // Get all stats with defaults for calculation
            val participation = currentStats["participation"] as? Long ?: 0
            val positiveInteractions = currentStats["positiveInteractions"] as? Long ?: 0
            val votesCast = currentStats["votesCast"] as? Long ?: 0
            val lawsPassed = currentStats["lawsPassed"] as? Long ?: 0
            val streakDays = currentStats["streakDays"] as? Long ?: 0
            
            // Calculate the power score based on a weighted formula
            val powerScore = calculatePowerScore(
                participation.toInt(),
                positiveInteractions.toInt(),
                votesCast.toInt(),
                lawsPassed.toInt(),
                streakDays.toInt()
            )
            
            transaction.update(userRef, "stats.powerScore", powerScore)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating power score in transaction: ${e.message}")
        }
    }
    
    /**
     * Calculate the power score based on user statistics
     */
    fun calculatePowerScore(
        participation: Int,
        positiveInteractions: Int,
        votesCast: Int,
        lawsPassed: Int,
        streakDays: Int
    ): Int {
        // Base participation score
        var score = participation
        
        // Add bonus for positive interactions (2x weight)
        score += (positiveInteractions * 2)
        
        // Add points for votes
        score += (votesCast * 3)
        
        // Add major points for laws passed (10x weight)
        score += (lawsPassed * 10)
        
        // Add streak bonus
        score += (streakDays * 5)
        
        return score
    }
    
    /**
     * Maps Firestore data to UserProfile object
     */
    fun mapToUserProfile(userData: Map<String, Any>): UserProfile {
        try {
            // Extract basic profile data
            val username = userData["username"] as? String ?: "Freedom Fighter"
            val userId = userData["userId"] as? String ?: ""
            val email = userData["email"] as? String ?: ""
            val avatar = userData["avatar"] as? String ?: ""
            val joinDate = (userData["joinDate"] as? Long)?.toString() ?: System.currentTimeMillis().toString()
            val district = userData["district"] as? String ?: "District 1"
            val politicalRank = userData["politicalRank"] as? String ?: "Citizen"
            val freedomBucks = (userData["freedomBucks"] as? Number)?.toInt() ?: 100
            
            // Extract stats
            val statsData = userData["stats"]
            val statsMap = when {
                statsData is Map<*, *> -> statsData.mapKeys { it.key.toString() }
                    .mapValues { (_, value) -> value as? Number ?: 0 }
                else -> mapOf<String, Number>()
            }
            val stats = UserStats(
                votesCast = statsMap["votesCast"] as? Int ?: 0,
                lawsPassed = statsMap["lawsPassed"] as? Int ?: 0,
                streakDays = statsMap["streakDays"] as? Int ?: 0,
                participation = statsMap["participation"] as? Int ?: 0,
                positiveInteractions = statsMap["positiveInteractions"] as? Int ?: 0,
                powerScore = statsMap["powerScore"] as? Int ?: 100,
                lastLoginTimestamp = statsMap["lastLoginTimestamp"] as? Long ?: System.currentTimeMillis()
            )
            
            // Extract badges
            val badgesData = userData["badges"] ?: emptyList<Any>()
            if (badgesData !is List<*>) {
                Log.e(TAG, "Invalid badges data type in mapToUserProfile")
                return UserProfile()
            }
            
            val badges = badgesData.mapNotNull { it as? String }
                .toList()
            
            // Return the profile
            return UserProfile(
                userId = userId,
                username = username,
                avatar = avatar,
                joinDate = joinDate,
                district = district,
                politicalRank = politicalRank,
                freedomBucks = freedomBucks,
                badges = badges,
                stats = stats,
                latestAchievement = null
            )
        } catch (e: Exception) {
            // Return a default profile if there's an error
            return UserProfile()
        }
    }
} 