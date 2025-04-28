package com.example.wethepeople.data.model

import java.util.*

/**
 * Represents a user comment in the application.
 * Comments use anonymous emoji-based identification rather than usernames.
 */
data class Comment(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val userEmojiId: String,
    val userRank: String,
    val userLevel: Int,
    val parentId: String? = null,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val flags: Int = 0,
    val isEdited: Boolean = false,
    val isPinned: Boolean = false,
    val isRemoved: Boolean = false,
    val isFlagged: Boolean = false
) {
    /**
     * Creates a display name for the comment author
     */
    fun getDisplayName(useFullName: Boolean = true): String {
        return if (useFullName) {
            CommentIdentifier.createDisplayName(userEmojiId, userRank, userLevel)
        } else {
            CommentIdentifier.createShortDisplayName(userEmojiId)
        }
    }
    
    /**
     * Gets a formatted time string from the timestamp
     */
    fun getFormattedTimestamp(): String {
        val now = System.currentTimeMillis()
        val diffInMillis = now - timestamp
        
        // Convert to appropriate time units
        val seconds = diffInMillis / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> "$days d"
            hours > 0 -> "$hours h"
            minutes > 0 -> "$minutes m"
            else -> "just now"
        }
    }
} 