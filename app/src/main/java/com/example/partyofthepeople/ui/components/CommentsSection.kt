package com.example.partyofthepeople.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.partyofthepeople.ui.utils.PatrioticCommentGenerator
import com.example.partyofthepeople.ui.utils.CommentRewardManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.partyofthepeople.ui.theme.ComposeColors
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import kotlinx.coroutines.tasks.await

private const val TAG = "CommentsSection"

/**
 * Data class for comments displayed in the comments section
 */
data class Comment(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String,
    val text: String,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

class CommentRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    private val rewardManager = CommentRewardManager()
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()
    
    fun getCommentsForPost(postId: String, onCommentsFetched: (List<Comment>) -> Unit) {
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                
                val commentList = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    Comment(
                        id = doc.id,
                        postId = data["postId"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        username = data["username"] as? String ?: "Anonymous",
                        text = data["text"] as? String ?: "",
                        likes = (data["likes"] as? Number)?.toInt() ?: 0,
                        dislikes = (data["dislikes"] as? Number)?.toInt() ?: 0,
                        timestamp = data["timestamp"] as? Long ?: 0
                    )
                } ?: emptyList()
                
                _comments.value = commentList
                onCommentsFetched(commentList)
            }
    }
    
    fun addComment(comment: Comment, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Create a map of data to store
        val commentData = mapOf(
            "postId" to comment.postId,
            "userId" to comment.userId,
            "username" to comment.username,
            "text" to comment.text,
            "likes" to comment.likes,
            "dislikes" to comment.dislikes,
            "timestamp" to comment.timestamp
        )
        
        db.collection("comments")
            .add(commentData)
            .addOnSuccessListener { _documentRef -> 
                // Award Freedom Bucks for posting a comment
                scope.launch {
                    if (comment.userId.isNotEmpty() && comment.userId != "anonymous") {
                        rewardManager.awardCommentPosting(comment.userId)
                    }
                    onSuccess()
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
    
    fun voteOnComment(commentId: String, isUpvote: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        
        // Get current comment doc
        db.collection("comments").document(commentId).get()
            .addOnSuccessListener { document ->
                val data = document.data ?: return@addOnSuccessListener
                val votesData = data["userVotes"] as? Map<*, *>
                val userVotes = when {
                    votesData is Map<*, *> -> votesData.mapKeys { it.key.toString() }
                        .mapValues { (_, value) -> value as? Boolean ?: false }
                        .toMutableMap()
                    else -> mutableMapOf<String, Boolean>()
                }
                val previousVote = userVotes[userId]
                
                if (previousVote == isUpvote) {
                    // Remove vote if clicking same button
                    userVotes.remove(userId)
                } else {
                    // Add or change vote
                    userVotes[userId] = isUpvote
                }
                
                // Count votes
                val likes = userVotes.count { it.value }
                val dislikes = userVotes.count { !it.value }
                
                // Update comment
                db.collection("comments").document(commentId)
                    .update(
                        mapOf(
                            "userVotes" to userVotes,
                            "likes" to likes,
                            "dislikes" to dislikes
                        )
                    )
                    .addOnSuccessListener {
                        // Process rewards for upvotes/downvotes
                        scope.launch {
                            rewardManager.processVoteOnComment(commentId, isUpvote)
                        }
                    }
            }
    }
}

/**
 * A section to display and add comments
 */
@Composable
fun CommentsSection(
    comments: List<Comment>,
    onLikeClick: (Comment) -> Unit,
    onDislikeClick: (Comment) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(comments) { comment ->
            CommentCard(
                comment = comment,
                onLikeClick = { onLikeClick(comment) },
                onDislikeClick = { onDislikeClick(comment) }
            )
        }
    }
}

@Composable
fun CommentCard(
    comment: Comment,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Username and timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.username,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = formatTimestamp(comment.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Comment text
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Like/Dislike buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onLikeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Like (${comment.likes})")
                }
                
                Button(
                    onClick = onDislikeClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Dislike (${comment.dislikes})")
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// Mock data for preview
private fun getMockComments(): List<Comment> {
    return listOf(
        Comment(
            username = "PatriotUser1776",
            text = "I strongly support this proposal! It's about time we addressed this issue.",
            likes = 42
        ),
        Comment(
            username = "ConstitutionFan",
            text = "While I understand the sentiment, I worry about the implementation costs.",
            likes = 28
        ),
        Comment(
            username = "LibertyOrDeath",
            text = "Has anyone considered the long-term implications? This could set a concerning precedent.",
            likes = 15
        ),
        Comment(
            username = "FreedomEagle",
            text = "Excellent proposal! I've been waiting for someone to suggest this for years now.",
            likes = 37
        )
    )
}

@Composable
fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (selected) Color(0xFF4CAF50) else Color(0xFF2A4A73)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

// Handle vote
suspend fun handleVote(commentId: String, userId: String, isUpvote: Boolean) {
    withContext(Dispatchers.IO) {
        try {
            val db = FirebaseFirestore.getInstance()
            // Get current comment doc
            val document = db.collection("comments").document(commentId).get().await()
            val data = document.data ?: return@withContext
            val votesData = data["userVotes"] as? Map<*, *>
            val userVotes = when {
                votesData is Map<*, *> -> votesData.mapKeys { it.key.toString() }
                    .mapValues { (_, value) -> value as? Boolean ?: false }
                    .toMutableMap()
                else -> mutableMapOf<String, Boolean>()
            }
            val previousVote = userVotes[userId]
            
            if (previousVote == isUpvote) {
                // Remove vote if clicking same button
                userVotes.remove(userId)
            } else {
                // Add or change vote
                userVotes[userId] = isUpvote
            }
            
            // Count votes
            val likes = userVotes.count { it.value }
            val dislikes = userVotes.count { !it.value }
            
            // Update comment
            db.collection("comments").document(commentId)
                .update(
                    mapOf(
                        "userVotes" to userVotes,
                        "likes" to likes,
                        "dislikes" to dislikes
                    )
                )
                .await()
                
            // Process rewards for upvotes/downvotes
            val rewardManager = CommentRewardManager()
            rewardManager.processVoteOnComment(commentId, isUpvote)
        } catch (e: Exception) {
            Log.e(TAG, "Error handling vote", e)
        }
    }
} 