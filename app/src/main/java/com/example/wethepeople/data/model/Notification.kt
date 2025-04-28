package com.example.wethepeople.data.model

import java.util.UUID

/**
 * Represents a notification in the "We The People" app
 */
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val deepLink: String? = null,  // Format: "wethepeople://screen/[screenName]?[params]"
    val relatedItemId: String? = null,  // ID of the related item (proposal, comment, etc.)
    val senderEmojiId: String? = null,  // Emoji ID of the sender (for comment notifications)
    val level: Int? = null  // New level (for level up notifications)
) {
    /**
     * Returns a formatted timestamp string
     */
    fun getFormattedTime(): String {
        val now = System.currentTimeMillis()
        val diffInMillis = now - timestamp
        
        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> if (days == 1L) "Yesterday" else "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "Just now"
        }
    }
    
    /**
     * Returns a small icon resource based on the notification type
     */
    fun getIconResource(): Int {
        return type.iconResource
    }
    
    /**
     * Returns the background color for the notification icon
     */
    fun getIconBackgroundColor(): Int {
        return type.iconBackgroundColor
    }
}

/**
 * Enum representing different types of notifications
 */
enum class NotificationType(val iconResource: Int, val iconBackgroundColor: Int) {
    LEVEL_UP(com.example.wethepeople.R.drawable.ic_level_up, android.graphics.Color.parseColor("#4CAF50")),
    VOTE_RESULT(com.example.wethepeople.R.drawable.ic_vote, android.graphics.Color.parseColor("#2196F3")),
    COMMENT_REPLY(com.example.wethepeople.R.drawable.ic_comment, android.graphics.Color.parseColor("#FF9800")),
    PROPOSAL_COMMENT(com.example.wethepeople.R.drawable.ic_proposal, android.graphics.Color.parseColor("#9C27B0")),
    MISSION_COMPLETED(com.example.wethepeople.R.drawable.ic_mission, android.graphics.Color.parseColor("#FFEB3B")),
    PATRIOT_POINTS(com.example.wethepeople.R.drawable.ic_patriot_points, android.graphics.Color.parseColor("#F44336")),
    SYSTEM(com.example.wethepeople.R.drawable.ic_system, android.graphics.Color.parseColor("#607D8B"))
} 