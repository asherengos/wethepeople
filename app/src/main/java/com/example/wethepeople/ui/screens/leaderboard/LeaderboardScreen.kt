package com.example.wethepeople.ui.screens.leaderboard

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wethepeople.R
import com.example.wethepeople.data.model.LeaderboardEntry as ModelLeaderboardEntry
import com.example.wethepeople.ui.theme.*
import com.example.wethepeople.ui.viewmodel.LeaderboardViewModel

// Define missing color values
val patriot_blue = Color(0xFF0A3161)
val patriot_red = Color(0xFFB31942)
val patriot_white = Color.White
val patriot_gold = Color(0xFFFFD700)
val patriot_silver = Color(0xFFC0C0C0)
val patriot_bronze = Color(0xFFCD7F32)
val patriot_gray = Color(0xFF888888)
val patriot_highlight = Color(0xFF2196F3)

// Define enum here since we removed the other file
enum class LeaderboardFilter {
    ALL,
    FRIENDS,
    LOCAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val entries by viewModel.leaderboardEntries.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = patriot_blue,
                    titleContentColor = patriot_white,
                    navigationIconContentColor = patriot_white
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Leaderboard content
            Box(modifier = Modifier.fillMaxSize()) {
                if (entries.isEmpty()) {
                    Text(
                        text = "No patriots found!",
                modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                } else {
                    LeaderboardContent(
                        entries = entries,
                        currentUserId = viewModel.getCurrentUserId(),
                        modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
    }
}

@Composable
fun LeaderboardContent(
    entries: List<ModelLeaderboardEntry>,
    currentUserId: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Header
        item {
            LeaderboardHeader()
        }
        
        // Entries
        items(entries) { entry ->
            LeaderboardEntryItem(
                entry = entry,
                currentUserId = currentUserId
                )
            }
        }
}

@Composable
fun LeaderboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
            text = "Rank",
            modifier = Modifier.width(50.dp),
                fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = patriot_blue
        )
        
        Text(
            text = "Patriot",
                    modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = patriot_blue
        )
        
        Text(
            text = "Score",
            modifier = Modifier.width(70.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = patriot_blue,
            textAlign = TextAlign.End
        )
    }
    
    Divider(
        color = patriot_gray.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(top = 4.dp)
                    )
}

@Composable
fun LeaderboardEntryItem(
    entry: ModelLeaderboardEntry,
    currentUserId: String
) {
    val rank = (entry.patriotPoints / 100).coerceAtLeast(1) // Derive rank from points
    val isTopThree = rank <= 3
    val isCurrentUser = entry.userId == currentUserId
    
    val backgroundColor = when {
        isCurrentUser -> patriot_blue.copy(alpha = 0.1f)
        isTopThree -> patriot_highlight.copy(alpha = 0.05f)
        else -> Color.Transparent
    }
    
    val rankBgColor = when (rank) {
        1 -> patriot_gold
        2 -> patriot_silver
        3 -> patriot_bronze
        else -> patriot_gray.copy(alpha = 0.1f)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(rankBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                color = if (isTopThree) Color.White else patriot_blue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Avatar and username
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar or placeholder
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = patriot_gray
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Username with badges
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = entry.username,
                        fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp,
                        color = patriot_blue
                    )
                    
                    if (isCurrentUser) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                            text = "(YOU)",
                            fontSize = 12.sp,
                            color = patriot_red
                    )
                }
            }

                // Citizen rank
                Text(
                    text = entry.citizenRank,
                    fontSize = 12.sp,
                    color = patriot_gray
                    )
            }
        }
        
        // Score
        Text(
            text = entry.patriotPoints.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = patriot_blue,
            modifier = Modifier.width(70.dp),
            textAlign = TextAlign.End
        )
    }
} 