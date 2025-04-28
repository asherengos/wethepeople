package com.example.wethepeople.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.data.model.Notification
import com.example.wethepeople.data.model.NotificationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        // Load mock notifications for testing
        loadMockNotifications()
        updateUnreadCount()
    }

    fun markAsRead(notificationId: String) {
        _notifications.update { currentList ->
            currentList.map { notification ->
                if (notification.id == notificationId && !notification.isRead) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }
        updateUnreadCount()
    }

    fun markAllAsRead() {
        _notifications.update { currentList ->
            currentList.map { notification ->
                if (!notification.isRead) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }
        updateUnreadCount()
    }

    fun deleteNotification(notificationId: String) {
        _notifications.update { currentList ->
            currentList.filter { it.id != notificationId }
        }
        updateUnreadCount()
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
        updateUnreadCount()
    }

    fun addNotification(
        title: String,
        message: String,
        type: NotificationType = NotificationType.SYSTEM,
        relatedItemId: String? = null
    ) {
        val newNotification = Notification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            type = type,
            relatedItemId = relatedItemId
        )

        _notifications.update { currentList ->
            val updatedList = currentList.toMutableList()
            updatedList.add(0, newNotification) // Add to beginning of list (newest first)
            updatedList
        }
        updateUnreadCount()
    }

    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }

    private fun loadMockNotifications() {
        val mockNotifications = listOf(
            Notification(
                id = UUID.randomUUID().toString(),
                title = "New Poll Available",
                message = "A new poll on climate policy is available for you to vote on",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                isRead = false,
                type = NotificationType.VOTE_RESULT,
                relatedItemId = "poll_123"
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                title = "Mission Completed!",
                message = "You've completed the 'Participate in 5 Polls' mission",
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                isRead = true,
                type = NotificationType.MISSION_COMPLETED
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                title = "Friend Request",
                message = "John Doe wants to connect with you",
                timestamp = System.currentTimeMillis() - 172800000, // 2 days ago
                isRead = false,
                type = NotificationType.COMMENT_REPLY,
                relatedItemId = "user_456"
            ),
            Notification(
                id = UUID.randomUUID().toString(),
                title = "App Update",
                message = "We've added new features to improve your experience",
                timestamp = System.currentTimeMillis() - 259200000, // 3 days ago
                isRead = true,
                type = NotificationType.SYSTEM
            )
        )
        
        _notifications.value = mockNotifications
    }

    fun addMockNotification() {
        addNotification(
            title = "Test Notification",
            message = "This is a test notification added at ${System.currentTimeMillis()}",
            type = NotificationType.SYSTEM
        )
    }
} 