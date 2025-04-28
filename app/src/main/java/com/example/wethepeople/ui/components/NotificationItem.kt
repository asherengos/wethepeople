package com.example.wethepeople.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.data.model.Notification
import com.example.wethepeople.data.model.NotificationType

/**
 * Displays a single notification item in the notification center
 */
@Composable
fun NotificationItem(
    notification: Notification,
    onClick: (Notification) -> Unit,
    onDelete: (Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (notification.isRead) 0.7f else 1.0f
    val backgroundColor = if (notification.isRead) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(notification) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 1.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getNotificationColor(notification.type).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getNotificationEmoji(notification.type),
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Notification content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(alpha)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(0.8f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = notification.getFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Delete button
            IconButton(
                onClick = { onDelete(notification) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete notification",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Unread indicator
            if (!notification.isRead) {
                Spacer(modifier = Modifier.width(4.dp))
                
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

/**
 * Returns the emoji for a notification type
 */
@Composable
private fun getNotificationEmoji(type: NotificationType): String {
    return when (type) {
        NotificationType.LEVEL_UP -> "⬆️"
        NotificationType.VOTE_RESULT -> "🗳️"
        NotificationType.COMMENT_REPLY -> "💬"
        NotificationType.PROPOSAL_COMMENT -> "📝"
        NotificationType.MISSION_COMPLETED -> "🎯"
        NotificationType.PATRIOT_POINTS -> "⭐"
        NotificationType.SYSTEM -> "ℹ️"
    }
}

/**
 * Returns the color for a notification type
 */
@Composable
private fun getNotificationColor(type: NotificationType): Color {
    return when (type) {
        NotificationType.LEVEL_UP -> Color(0xFF4CAF50)
        NotificationType.VOTE_RESULT -> Color(0xFF2196F3)
        NotificationType.COMMENT_REPLY -> Color(0xFFFF9800)
        NotificationType.PROPOSAL_COMMENT -> Color(0xFF9C27B0)
        NotificationType.MISSION_COMPLETED -> Color(0xFFFFEB3B)
        NotificationType.PATRIOT_POINTS -> Color(0xFFF44336)
        NotificationType.SYSTEM -> Color(0xFF607D8B)
    }
} 