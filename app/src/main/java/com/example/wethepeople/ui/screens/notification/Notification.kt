package com.example.wethepeople.ui.screens.notification

/**
 * Represents a notification in the app
 */
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.SYSTEM,
    val actionId: String? = null
)

/**
 * Types of notifications that can appear in the app
 */
enum class NotificationType {
    POLL,       // Notifications about polls and voting
    MISSION,    // Notifications about missions and achievements
    SOCIAL,     // Notifications about social interactions
    SYSTEM      // System notifications about app features and updates
} 