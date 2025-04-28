package com.example.wethepeople.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.data.model.Comment

/**
 * Displays a single comment with emoji-based identification.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentItem(
    comment: Comment,
    onReply: (Comment) -> Unit,
    onLike: (Comment) -> Unit,
    onDislike: (Comment) -> Unit,
    onFlag: (Comment) -> Unit,
    modifier: Modifier = Modifier,
    isReply: Boolean = false,
    useCompactLayout: Boolean = false
) {
    val backgroundColor = if (isReply) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isReply) 16.dp else 0.dp,
                top = 4.dp,
                bottom = 4.dp
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Comment header with emoji identifier
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // User identity (emoji + rank if not compact)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Emoji identifier in a circle
                    Box(
                        modifier = Modifier
                            .size(if (useCompactLayout) 24.dp else 30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = comment.userEmojiId.split(" ")[0],  // Show first emoji only in circle
                            fontSize = if (useCompactLayout) 12.sp else 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Show full emoji ID + rank if not compact
                    if (!useCompactLayout) {
                        Text(
                            text = comment.getDisplayName(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    } else {
                        // In compact mode show just the emojis
                        Text(
                            text = comment.userEmojiId,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Timestamp
                Text(
                    text = comment.getFormattedTimestamp(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Comment content
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Reply button
                TextButton(
                    onClick = { onReply(comment) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Reply",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reply",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                // Vote buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like button
                    IconButton(
                        onClick = { onLike(comment) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Like",
                            modifier = Modifier.size(16.dp),
                            tint = if (comment.likes > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    
                    Text(
                        text = "${comment.likes}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    
                    // Dislike button
                    IconButton(
                        onClick = { onDislike(comment) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Dislike",
                            modifier = Modifier.size(16.dp),
                            tint = if (comment.dislikes > 0) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    
                    Text(
                        text = "${comment.dislikes}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                    
                    // Flag button - subtle, only for reporting
                    IconButton(
                        onClick = { onFlag(comment) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Flag",
                            modifier = Modifier.size(16.dp),
                            tint = if (comment.isFlagged) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
} 