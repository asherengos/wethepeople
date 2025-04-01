package com.example.partyofthepeople.bipartisan

import android.util.Log
import com.example.partyofthepeople.BipartisanMatch
import com.example.partyofthepeople.SimilarityType
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.VoteRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Manages bipartisan match calculations between users
 */
class BipartisanMatchManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "BipartisanManager"
    
    /**
     * Calculate match percentages between current user and others
     */
    suspend fun calculateBipartisanMatches(currentUserProfile: UserProfile): List<BipartisanMatch> {
        if (currentUserProfile.voteHistory.isEmpty()) {
            Log.d(TAG, "User has no vote history, skipping match calculation")
            return emptyList()
        }
        
        try {
            // Get users who have cast votes
            val userDocs = db.collection("users")
                .whereGreaterThan("stats.votesCast", 0)
                .limit(50) // Limit to 50 users for performance
                .get()
                .await()
                
            val matches = mutableListOf<BipartisanMatch>()
                
            for (doc in userDocs.documents) {
                val userId = doc.id
                
                // Skip current user
                if (userId == currentUserProfile.userId) continue
                
                val username = doc.getString("username") ?: continue
                val avatar = doc.getString("avatar") ?: ""
                
                // Get their vote history with type safety
                val voteHistoryData = doc.get("voteHistory") ?: continue
                if (voteHistoryData !is List<*>) {
                    Log.e(TAG, "Invalid vote history data type for user $username")
                    continue
                }
                
                val voteHistory = voteHistoryData.mapNotNull { item ->
                    if (item !is Map<*, *>) null
                    else item.mapKeys { it.key.toString() }
                }
                
                if (voteHistory.isEmpty()) continue
                
                // Convert to VoteRecord objects
                val otherUserVotes = voteHistory.mapNotNull { voteMap ->
                    try {
                        VoteRecord(
                            proposalId = voteMap["proposalId"] as? String ?: "",
                            proposalTitle = voteMap["proposalTitle"] as? String ?: "",
                            supported = voteMap["supported"] as? Boolean ?: false,
                            timestamp = (voteMap["timestamp"] as? Number)?.toLong() ?: 0L,
                            category = voteMap["category"] as? String ?: "General"
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing vote record: ${e.message}")
                        null
                    }
                }
                
                if (otherUserVotes.isEmpty()) continue
                
                // Calculate match percentage
                val matchResults = calculateMatchPercentage(currentUserProfile.voteHistory, otherUserVotes)
                
                matches.add(BipartisanMatch(
                    userId = userId,
                    username = username,
                    avatar = avatar,
                    matchPercentage = matchResults.first,
                    similarityType = SimilarityType.MIXED
                ))
            }
            
            // Sort by match percentage (highest first)
            return matches.sortedByDescending { it.matchPercentage }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating bipartisan matches: ${e.message}")
            return emptyList()
        }
    }
    
    /**
     * Calculate match percentage between two users' vote histories
     * Returns a Pair of (percentage, commonVoteCount)
     */
    private fun calculateMatchPercentage(
        currentUserVotes: List<VoteRecord>,
        otherUserVotes: List<VoteRecord>
    ): Pair<Int, Int> {
        // Find votes on same proposals
        val currentUserVoteMap = currentUserVotes.associateBy { it.proposalId }
        
        var matchCount = 0
        var totalCommonVotes = 0
        
        for (otherVote in otherUserVotes) {
            val currentUserVote = currentUserVoteMap[otherVote.proposalId] ?: continue
            
            totalCommonVotes++
            
            // If they voted the same way, it's a match
            if (currentUserVote.supported == otherVote.supported) {
                matchCount++
            }
        }
        
        // Calculate percentage
        val percentage = if (totalCommonVotes > 0) {
            (matchCount * 100) / totalCommonVotes
        } else {
            0
        }
        
        return Pair(percentage, totalCommonVotes)
    }
    
    /**
     * Records a user's vote for potential bipartisan matching
     */
    fun recordVote(userId: String, voteRecord: VoteRecord) {
        // Placeholder for Firebase implementation
    }
    
    /**
     * Update stored match percentages for a user
     */
    suspend fun updateStoredMatchPercentages(userId: String, matches: List<BipartisanMatch>) {
        try {
            // Convert to a map for storage
            val matchMap = matches.associate { it.userId to it.matchPercentage }
            
            // Update in Firestore
            db.collection("users").document(userId)
                .update("matchPercentages", matchMap)
                .await()
                
            Log.d(TAG, "Updated match percentages for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating match percentages: ${e.message}")
        }
    }
} 