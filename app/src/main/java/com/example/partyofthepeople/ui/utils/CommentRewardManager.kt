package com.example.partyofthepeople.ui.utils

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * Manages the reward system for comment activity, including:
 * - Rewards for posting comments
 * - Rewards for receiving upvotes on comments
 * - Tracking user engagement for leaderboard purposes
 */
class CommentRewardManager {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "CommentRewardManager"
    
    companion object {
        // Reward amounts in Freedom Bucks
        const val COMMENT_POSTING_REWARD = 3
        const val UPVOTE_RECEIVED_REWARD = 2
        const val COMMENT_WITH_10_UPVOTES_BONUS = 10
        const val COMMENT_WITH_50_UPVOTES_BONUS = 25
        const val COMMENT_WITH_100_UPVOTES_BONUS = 50
    }
    
    /**
     * Award Freedom Bucks to a user for posting a comment
     */
    suspend fun awardCommentPosting(userId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            
            // Update freedomBucks and participation stats
            userRef.update(
                mapOf(
                    "freedomBucks" to FieldValue.increment(COMMENT_POSTING_REWARD.toLong()),
                    "stats.participation" to FieldValue.increment(2)
                )
            ).await()
            
            // Update powerScore which factors into leaderboard rankings
            val userSnapshot = userRef.get().await()
            if (userSnapshot.exists()) {
                val stats = userSnapshot.get("stats") as? Map<String, Any> ?: return
                val participation = (stats["participation"] as? Number)?.toInt() ?: 0
                val positiveInteractions = (stats["positiveInteractions"] as? Number)?.toInt() ?: 0
                val votesCast = (stats["votesCast"] as? Number)?.toInt() ?: 0
                val lawsPassed = (stats["lawsPassed"] as? Number)?.toInt() ?: 0
                val streakDays = (stats["streakDays"] as? Number)?.toInt() ?: 0
                
                // Power score calculation
                val powerScore = participation + 
                                 (positiveInteractions * 2) + 
                                 (votesCast * 3) + 
                                 (lawsPassed * 10) + 
                                 (streakDays * 5)
                
                userRef.update("stats.powerScore", powerScore).await()
            }
            
            Log.d(TAG, "Awarded $COMMENT_POSTING_REWARD Freedom Bucks to user $userId for posting a comment")
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding comment posting: ${e.message}")
        }
    }
    
    /**
     * Award Freedom Bucks to a user for receiving an upvote on their comment
     * @param commentId ID of the upvoted comment
     * @param isUpvote True if it's an upvote, false if it's a downvote
     */
    suspend fun processVoteOnComment(commentId: String, isUpvote: Boolean) {
        try {
            val commentRef = db.collection("comments").document(commentId)
            val commentSnapshot = commentRef.get().await()
            
            if (!commentSnapshot.exists()) {
                Log.d(TAG, "Comment not found: $commentId")
                return
            }
            
            val comment = commentSnapshot.data ?: return
            val commentUserId = comment["userId"] as? String ?: return
            val currentUser = Firebase.auth.currentUser?.uid
            
            // Don't reward users for upvoting their own comments
            if (commentUserId == currentUser) {
                Log.d(TAG, "User upvoted their own comment, no reward")
                return
            }
            
            val userRef = db.collection("users").document(commentUserId)
            
            // Only reward for upvotes, penalize for downvotes
            if (isUpvote) {
                // Award Freedom Bucks for receiving an upvote
                userRef.update(
                    mapOf(
                        "freedomBucks" to FieldValue.increment(UPVOTE_RECEIVED_REWARD.toLong()),
                        "stats.positiveInteractions" to FieldValue.increment(1)
                    )
                ).await()
                
                // Check for milestone bonuses
                val likes = (comment["likes"] as? Number)?.toInt() ?: 0
                if (likes == 10) {
                    awardMilestoneBonus(commentUserId, commentId, 10)
                } else if (likes == 50) {
                    awardMilestoneBonus(commentUserId, commentId, 50)
                } else if (likes == 100) {
                    awardMilestoneBonus(commentUserId, commentId, 100)
                }
                
                Log.d(TAG, "Awarded $UPVOTE_RECEIVED_REWARD Freedom Bucks to user $commentUserId for receiving an upvote")
            } else {
                // No penalty for downvotes currently, but could be implemented here
                // Could decrease positive interactions, but for now we'll just track it
                Log.d(TAG, "User $commentUserId received a downvote, no penalty applied")
            }
            
            // Update powerScore after interaction change
            updateUserPowerScore(commentUserId)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error processing vote on comment: ${e.message}")
        }
    }
    
    /**
     * Award a milestone bonus for comments that reach a certain number of upvotes
     */
    private suspend fun awardMilestoneBonus(userId: String, commentId: String, upvotes: Int) {
        try {
            val userRef = db.collection("users").document(userId)
            
            val bonusAmount = when (upvotes) {
                10 -> COMMENT_WITH_10_UPVOTES_BONUS
                50 -> COMMENT_WITH_50_UPVOTES_BONUS
                100 -> COMMENT_WITH_100_UPVOTES_BONUS
                else -> 0
            }
            
            if (bonusAmount > 0) {
                // Award bonus Freedom Bucks
                userRef.update("freedomBucks", FieldValue.increment(bonusAmount.toLong())).await()
                
                // Record that this comment milestone has been rewarded to prevent duplicate rewards
                db.collection("commentMilestones").document("$commentId-$upvotes").set(
                    mapOf(
                        "userId" to userId,
                        "commentId" to commentId,
                        "upvotes" to upvotes,
                        "bonusAmount" to bonusAmount,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()
                
                Log.d(TAG, "Awarded $bonusAmount Freedom Bucks to user $userId for comment reaching $upvotes upvotes")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error awarding milestone bonus: ${e.message}")
        }
    }
    
    /**
     * Update a user's power score based on their latest stats
     */
    private suspend fun updateUserPowerScore(userId: String) {
        try {
            val userRef = db.collection("users").document(userId)
            val userSnapshot = userRef.get().await()
            
            if (userSnapshot.exists()) {
                val stats = userSnapshot.get("stats") as? Map<String, Any> ?: return
                val participation = (stats["participation"] as? Number)?.toInt() ?: 0
                val positiveInteractions = (stats["positiveInteractions"] as? Number)?.toInt() ?: 0
                val votesCast = (stats["votesCast"] as? Number)?.toInt() ?: 0
                val lawsPassed = (stats["lawsPassed"] as? Number)?.toInt() ?: 0
                val streakDays = (stats["streakDays"] as? Number)?.toInt() ?: 0
                
                // Power score calculation
                val powerScore = participation + 
                                 (positiveInteractions * 2) + 
                                 (votesCast * 3) + 
                                 (lawsPassed * 10) + 
                                 (streakDays * 5)
                
                userRef.update("stats.powerScore", powerScore).await()
                Log.d(TAG, "Updated power score for user $userId to $powerScore")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user power score: ${e.message}")
        }
    }
} 