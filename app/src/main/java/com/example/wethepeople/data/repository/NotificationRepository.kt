package com.example.wethepeople.data.repository

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wethepeople.MainActivity
import com.example.wethepeople.R
import com.example.wethepeople.data.model.Notification
import com.example.wethepeople.data.model.NotificationType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

/**
 * Repository for managing notifications in the "We The People" app
 */
class NotificationRepository private constructor(
    private val context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
    
    private val notificationId = AtomicInteger(0)
    
    init {
        createNotificationChannel()
        loadNotifications()
    }
    
    /**
     * Create and add a level up notification
     */
    fun createLevelUpNotification(newLevel: Int, newRank: String) {
        val notification = Notification(
            type = NotificationType.LEVEL_UP,
            title = "Leveled Up to $newRank!",
            message = "Congratulations! You've reached Level $newLevel and earned the rank of $newRank. Keep participating to unlock more!",
            deepLink = "wethepeople://profile",
            level = newLevel
        )
        
        addNotification(notification)
        sendPushNotification(notification)
    }
    
    /**
     * Create and add a vote result notification
     */
    fun createVoteResultNotification(
        proposalId: String,
        proposalTitle: String,
        result: String
    ) {
        val notification = Notification(
            type = NotificationType.VOTE_RESULT,
            title = "Vote Results: $proposalTitle",
            message = "The results are in! $result",
            deepLink = "wethepeople://voting/results?proposalId=$proposalId",
            relatedItemId = proposalId
        )
        
        addNotification(notification)
        sendPushNotification(notification)
    }
    
    /**
     * Create and add a comment reply notification
     */
    fun createCommentReplyNotification(
        commentId: String,
        proposalId: String,
        proposalTitle: String,
        senderEmojiId: String
    ) {
        val notification = Notification(
            type = NotificationType.COMMENT_REPLY,
            title = "New Reply on $proposalTitle",
            message = "$senderEmojiId replied to your comment",
            deepLink = "wethepeople://voting/detail?proposalId=$proposalId&commentId=$commentId",
            relatedItemId = commentId,
            senderEmojiId = senderEmojiId
        )
        
        addNotification(notification)
        sendPushNotification(notification)
    }
    
    /**
     * Create and add a proposal comment notification
     */
    fun createProposalCommentNotification(
        proposalId: String,
        proposalTitle: String,
        senderEmojiId: String
    ) {
        val notification = Notification(
            type = NotificationType.PROPOSAL_COMMENT,
            title = "New Comment on Your Proposal",
            message = "$senderEmojiId commented on your proposal \"$proposalTitle\"",
            deepLink = "wethepeople://voting/detail?proposalId=$proposalId",
            relatedItemId = proposalId,
            senderEmojiId = senderEmojiId
        )
        
        addNotification(notification)
        sendPushNotification(notification)
    }
    
    /**
     * Create and add a mission completed notification
     */
    fun createMissionCompletedNotification(
        missionId: String,
        missionTitle: String,
        pointsEarned: Int
    ) {
        val notification = Notification(
            type = NotificationType.MISSION_COMPLETED,
            title = "Mission Completed!",
            message = "You've completed \"$missionTitle\" and earned $pointsEarned Patriot Points!",
            deepLink = "wethepeople://missions?missionId=$missionId",
            relatedItemId = missionId
        )
        
        addNotification(notification)
        sendPushNotification(notification)
    }
    
    /**
     * Add a notification to the repository
     */
    private fun addNotification(notification: Notification) {
        coroutineScope.launch {
            val currentList = _notifications.value.toMutableList()
            currentList.add(0, notification)  // Add new notifications at the top
            _notifications.value = currentList
            
            // Update unread count
            _unreadCount.value = _notifications.value.count { !it.isRead }
            
            // TODO: Save notification to database
        }
    }
    
    /**
     * Mark a notification as read
     */
    fun markAsRead(notificationId: String) {
        coroutineScope.launch {
            val currentList = _notifications.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == notificationId }
            
            if (index != -1) {
                val notification = currentList[index]
                currentList[index] = notification.copy(isRead = true)
                _notifications.value = currentList
                
                // Update unread count
                _unreadCount.value = _notifications.value.count { !it.isRead }
                
                // TODO: Update notification in database
            }
        }
    }
    
    /**
     * Mark all notifications as read
     */
    fun markAllAsRead() {
        coroutineScope.launch {
            val updatedList = _notifications.value.map { 
                it.copy(isRead = true) 
            }
            _notifications.value = updatedList
            _unreadCount.value = 0
            
            // TODO: Update notifications in database
        }
    }
    
    /**
     * Delete a notification
     */
    fun deleteNotification(notificationId: String) {
        coroutineScope.launch {
            val currentList = _notifications.value.toMutableList()
            val removedNotification = currentList.find { it.id == notificationId }
            
            currentList.removeIf { it.id == notificationId }
            _notifications.value = currentList
            
            // Update unread count if the removed notification was unread
            if (removedNotification != null && !removedNotification.isRead) {
                _unreadCount.value = _unreadCount.value - 1
            }
            
            // TODO: Delete notification from database
        }
    }
    
    /**
     * Clear all notifications
     */
    fun clearAllNotifications() {
        coroutineScope.launch {
            _notifications.value = emptyList()
            _unreadCount.value = 0
            
            // TODO: Delete all notifications from database
        }
    }
    
    /**
     * Load notifications from local storage
     */
    private fun loadNotifications() {
        coroutineScope.launch {
            // TODO: Load notifications from database
            // For now, add some mock notifications
            addMockNotifications()
        }
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private fun createNotificationChannel() {
        // This potentially does I/O operations, so do it in background
        coroutineScope.launch(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "We The People Notifications"
                val descriptionText = "Notifications from We The People app"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * Send a push notification to the system tray
     */
    private fun sendPushNotification(notification: Notification) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            // Add deep link data if available
            notification.deepLink?.let { deepLink ->
                data = android.net.Uri.parse(deepLink)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(notification.getIconResource())
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId.incrementAndGet(), builder.build())
            } catch (e: SecurityException) {
                // Handle missing notification permission
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Add mock notifications for testing
     */
    private fun addMockNotifications() {
        val mockNotifications = listOf(
            Notification(
                type = NotificationType.LEVEL_UP,
                title = "Leveled Up to Patriot!",
                message = "Congratulations! You've reached Level 5 and earned the rank of Patriot. Keep participating to unlock more!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 2, // 2 hours ago
                deepLink = "wethepeople://profile",
                level = 5
            ),
            Notification(
                type = NotificationType.VOTE_RESULT,
                title = "Vote Results: Infrastructure Bill",
                message = "The results are in! 65% of citizens voted in favor of the Infrastructure Bill.",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24, // 1 day ago
                deepLink = "wethepeople://voting/results?proposalId=123",
                relatedItemId = "123"
            ),
            Notification(
                type = NotificationType.COMMENT_REPLY,
                title = "New Reply on Education Reform",
                message = "🦅 🗽 ⭐ replied to your comment",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2, // 2 days ago
                deepLink = "wethepeople://voting/detail?proposalId=456&commentId=789",
                relatedItemId = "789",
                senderEmojiId = "🦅 🗽 ⭐",
                isRead = true
            ),
            Notification(
                type = NotificationType.PROPOSAL_COMMENT,
                title = "New Comment on Your Proposal",
                message = "📜 🔔 🎖️ commented on your proposal \"Healthcare Initiative\"",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 3, // 3 days ago
                deepLink = "wethepeople://voting/detail?proposalId=321",
                relatedItemId = "321",
                senderEmojiId = "📜 🔔 🎖️",
                isRead = true
            ),
            Notification(
                type = NotificationType.MISSION_COMPLETED,
                title = "Mission Completed!",
                message = "You've completed \"Vote on 5 Proposals\" and earned 50 Patriot Points!",
                timestamp = System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 4, // 4 days ago
                deepLink = "wethepeople://missions?missionId=vote5",
                relatedItemId = "vote5",
                isRead = true
            )
        )
        
        _notifications.value = mockNotifications
        _unreadCount.value = mockNotifications.count { !it.isRead }
    }
    
    companion object {
        private const val CHANNEL_ID = "we_the_people_channel"
        
        @Volatile
        private var INSTANCE: NotificationRepository? = null
        
        fun getInstance(context: Context): NotificationRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NotificationRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
} 