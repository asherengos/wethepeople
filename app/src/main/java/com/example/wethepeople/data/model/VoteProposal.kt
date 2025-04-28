package com.example.wethepeople.data.model

/**
 * Represents a user-submitted vote proposal.
 */
data class VoteProposal(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val options: List<String>,
    val supportingEvidence: String = "",
    val authorId: String,
    val authorName: String = "",
    val authorRank: String = "",
    val status: String, // "pending_moderation", "approved", "rejected"
    val rejectReason: String = "",
    val createdAt: Long,
    val approvedAt: Long = 0,
    val votesCount: Int = 0,
    // Moderation related fields
    val moderationScore: Float = 0f, // AI-generated risk score (0-1)
    val flagsCount: Int = 0, // Number of user flags
    val flagReasons: Map<String, Int> = mapOf(), // Counts of flag reasons
    val automaticReviewTriggered: Boolean = false, // Whether automatic review was triggered
    val moderationNotes: String = "", // Notes from moderators
    val appealStatus: String = "" // empty, "appealed", "appeal_approved", "appeal_rejected"
) {
    /**
     * Returns a display-friendly status string.
     */
    fun getDisplayStatus(): String {
        return when (status) {
            "pending_moderation" -> "Under Review"
            "approved" -> "Active"
            "rejected" -> "Rejected"
            else -> "Unknown"
        }
    }

    /**
     * Returns time elapsed since creation in a readable format.
     */
    fun getTimeAgo(): String {
        val currentTime = System.currentTimeMillis()
        val elapsedMillis = currentTime - createdAt
        
        val seconds = elapsedMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "$days day${if (days > 1) "s" else ""} ago"
            hours > 0 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            minutes > 0 -> "$minutes minute${if (minutes > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }

    /**
     * Checks if the proposal requires human moderation based on flags or AI score.
     */
    fun requiresHumanModeration(): Boolean {
        return flagsCount >= 3 || moderationScore >= 0.7f || automaticReviewTriggered
    }
} 