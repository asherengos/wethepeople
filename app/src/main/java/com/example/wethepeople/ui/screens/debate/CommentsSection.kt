package com.example.wethepeople.ui.screens.debate

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wethepeople.data.model.Comment
import com.example.wethepeople.data.model.CommentIdentifier
import com.example.wethepeople.ui.components.CommentItem
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Displays a section of comments with emoji-based identification system
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsSection(
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onLikeComment: (Comment) -> Unit,
    onDislikeComment: (Comment) -> Unit,
    onFlagComment: (Comment) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var replyToComment by remember { mutableStateOf<Comment?>(null) }
    var commentText by remember { mutableStateOf("") }
    
    Column(modifier = modifier.fillMaxWidth()) {
        // Section header
        Text(
            text = "Discussion",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
        )
        
        // Comments list
        if (comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Be the first to start the discussion",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(comments) { comment ->
                    val isReply = comment.parentId != null
                    CommentItem(
                        comment = comment,
                        onReply = { replyToComment = it },
                        onLike = onLikeComment,
                        onDislike = onDislikeComment,
                        onFlag = onFlagComment,
                        isReply = isReply,
                        useCompactLayout = isReply
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Reply notice if replying to a comment
        replyToComment?.let { comment ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Replying to ${comment.getDisplayName(useFullName = false)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                TextButton(
                    onClick = { replyToComment = null },
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        
        // Comment input box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Add a comment...") },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.surface
                    ),
                    singleLine = false,
                    maxLines = 3
                )
                
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            coroutineScope.launch {
                                onAddComment(commentText)
                                commentText = ""
                                replyToComment = null
                            }
                        }
                    },
                    enabled = commentText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (commentText.isBlank()) 
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) 
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Preview function to simulate adding a comment with emoji identity
 */
fun addComment(
    comments: MutableList<Comment>, 
    content: String, 
    parentId: String? = null,
    userRank: String = "Patriot",
    userLevel: Int = Random.nextInt(1, 20)
): List<Comment> {
    // Generate a unique emoji ID for this user or reuse existing
    val emojiId = CommentIdentifier.generateUniqueEmojiId()
    
    val newComment = Comment(
        content = content,
        userEmojiId = emojiId,
        userRank = userRank,
        userLevel = userLevel,
        parentId = parentId
    )
    
    return comments.toMutableList().apply { add(0, newComment) }
} 