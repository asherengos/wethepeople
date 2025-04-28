package com.example.wethepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wethepeople.ui.theme.*
import androidx.compose.foundation.BorderStroke
import com.example.wethepeople.ui.icons.CustomIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateDetailScreen(
    navController: NavController,
    debateTitle: String,
    debateDescription: String,
    tags: List<String> = listOf("Economy", "Taxation", "Liberty")
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DEBATE HALL",
                        fontWeight = FontWeight.Bold,
                        color = patriot_white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = CustomIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = patriot_white
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = patriot_dark_blue,
                    titleContentColor = patriot_white
                )
            )
        },
        containerColor = patriot_dark_blue
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Original post (debate topic)
            item {
                DebateTopicCard(
                    title = debateTitle,
                    description = debateDescription,
                    tags = tags
                )
            }
            
            // Sorting options
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SortChip(text = "Top Comments", isSelected = true)
                    SortChip(text = "New", isSelected = false)
                    SortChip(text = "Controversial", isSelected = false)
                }
            }
            
            // Comment input
            item {
                CommentInputField()
            }
            
            // Sample comments
            item {
                CommentCard(
                    username = "PatriotCommander",
                    isOfficial = true,
                    timeAgo = "3 hours ago",
                    content = "Tax evasion is clearly an act of economic TREASON against our great nation! Those who refuse to pay their fair share are undermining the very foundations of our democracy. The punishment should fit the crime!",
                    upvotes = 230,
                    downvotes = 45,
                    replies = emptyList() // No replies for this comment
                )
            }
            
            item {
                CommentCard(
                    username = "LibertyDefender1776",
                    isOfficial = false,
                    timeAgo = "2 hours ago",
                    content = "Taxation is theft! The Founding Fathers would be APPALLED at how much the government takes from hardworking Americans today. We need to return to CONSTITUTIONAL taxation!",
                    upvotes = 157,
                    downvotes = 121,
                    replies = listOf(
                        CommentReply(
                            username = "HistoryProfessor",
                            timeAgo = "1 hour ago",
                            content = "Actually, several founding fathers supported taxation for the common good. Alexander Hamilton established our first tax system.",
                            upvotes = 84
                        ),
                        CommentReply(
                            username = "LibertyDefender1776",
                            timeAgo = "45 minutes ago",
                            content = "Hamilton was a MONARCHIST! Jefferson is the true patriot we should follow!",
                            upvotes = 62
                        )
                    )
                )
            }
            
            item {
                CommentCard(
                    username = "TaxLawyer",
                    isOfficial = false,
                    timeAgo = "1 hour ago",
                    content = "Important to distinguish between tax avoidance (legal ways to minimize tax) and tax evasion (illegal failure to pay taxes). The discussion should focus on closing loopholes, not criminalizing legitimate practices.",
                    upvotes = 95,
                    downvotes = 15,
                    replies = emptyList()
                )
            }
        }
    }
}

@Composable
fun DebateTopicCard(
    title: String,
    description: String,
    tags: List<String>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = patriot_medium_blue),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red circle avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(patriot_red_bright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CustomIcons.Debate,
                        contentDescription = "Debate",
                        tint = patriot_white,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Title and hot tag
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = patriot_white,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        Surface(
                            color = patriot_red_bright,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "HOT",
                                color = patriot_white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            // Description
            Text(
                text = description,
                color = patriot_white.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
            )
            
            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                tags.forEach { tag ->
                    val tagColor = when (tag) {
                        "Economy" -> Color(0xFF1A6B3D) // Green
                        "Taxation" -> Color(0xFF795548) // Brown
                        "Liberty" -> Color(0xFFE65100) // Orange
                        else -> patriot_medium_blue
                    }
                    
                    Surface(
                        color = tagColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = patriot_white,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "1203 views",
                    color = patriot_white.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    text = "128 comments",
                    color = patriot_white.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Text(
                    text = "2 hours ago",
                    color = patriot_white.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SortChip(
    text: String,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) patriot_medium_blue else Color.Transparent
    val borderColor = if (isSelected) Color.Transparent else patriot_white.copy(alpha = 0.3f)
    val textColor = if (isSelected) patriot_white else patriot_white.copy(alpha = 0.7f)
    
    Surface(
        modifier = Modifier.border(
            width = 1.dp,
            color = borderColor,
            shape = RoundedCornerShape(16.dp)
        ),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputField() {
    var commentText by remember { mutableStateOf("") }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = patriot_dark_blue.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, patriot_medium_blue)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "LET YOUR VOICE BE HEARD!",
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            TextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Share your patriotic opinion...") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = patriot_dark_blue,
                    cursorColor = patriot_white,
                    focusedIndicatorColor = patriot_red_bright,
                    unfocusedIndicatorColor = patriot_medium_blue
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(
                onClick = { /* Handle comment submission */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = patriot_red_bright
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("POST COMMENT")
            }
        }
    }
}

@Composable
fun CommentCard(
    username: String,
    isOfficial: Boolean,
    timeAgo: String,
    content: String,
    upvotes: Int,
    downvotes: Int = 0,
    replies: List<CommentReply> = emptyList()
) {
    var userVote by remember { mutableStateOf(0) } // -1 for downvote, 0 for none, 1 for upvote
    var showReplies by remember { mutableStateOf(true) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = patriot_dark_blue.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Comment header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(patriot_red_bright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = username.first().toString(),
                        color = patriot_white,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Username and badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = username,
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    if (isOfficial) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            color = patriot_red_bright,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "OFFICIAL",
                                color = patriot_white,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Time
                Text(
                    text = timeAgo,
                    color = patriot_white.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            
            // Comment content
            Text(
                text = content,
                color = patriot_white,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            // Vote buttons
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { userVote = if (userVote == 1) 0 else 1 },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Upvote",
                        tint = if (userVote == 1) patriot_red_bright else patriot_white.copy(alpha = 0.6f)
                    )
                }
                
                Text(
                    text = (upvotes - downvotes + (if (userVote == 1) 1 else if (userVote == -1) -1 else 0)).toString(),
                    color = when {
                        userVote == 1 -> patriot_red_bright
                        userVote == -1 -> Color.Blue
                        else -> patriot_white
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                
                IconButton(
                    onClick = { userVote = if (userVote == -1) 0 else -1 },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Downvote",
                        tint = if (userVote == -1) Color.Blue else patriot_white.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Reply button
                TextButton(
                    onClick = { /* Show reply field */ }
                ) {
                    Text(
                        text = "REPLY",
                        color = patriot_white.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Replies section
            if (replies.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    TextButton(
                        onClick = { showReplies = !showReplies }
                    ) {
                        Text(
                            text = if (showReplies) "HIDE REPLIES (${replies.size})" else "SHOW REPLIES (${replies.size})",
                            color = patriot_white.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
                
                if (showReplies) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .border(
                                width = 1.dp,
                                color = patriot_medium_blue.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(12.dp)
                    ) {
                        replies.forEach { reply ->
                            ReplyItem(reply = reply)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyItem(reply: CommentReply) {
    var userVote by remember { mutableStateOf(0) }
    
    Column {
        // Reply header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = reply.username,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Time
            Text(
                text = reply.timeAgo,
                color = patriot_white.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
        
        // Reply content
        Text(
            text = reply.content,
            color = patriot_white.copy(alpha = 0.9f),
            fontSize = 13.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Vote buttons
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { userVote = if (userVote == 1) 0 else 1 },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropUp,
                    contentDescription = "Upvote",
                    tint = if (userVote == 1) patriot_red_bright else patriot_white.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = (reply.upvotes + (if (userVote == 1) 1 else 0)).toString(),
                color = if (userVote == 1) patriot_red_bright else patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Reply button
            TextButton(
                onClick = { /* Show reply field */ },
                modifier = Modifier.height(28.dp),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "REPLY",
                    color = patriot_white.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

data class CommentReply(
    val username: String,
    val timeAgo: String,
    val content: String,
    val upvotes: Int
) 