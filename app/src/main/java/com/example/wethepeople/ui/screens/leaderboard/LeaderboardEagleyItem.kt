package com.example.wethepeople.ui.screens.leaderboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.R
import com.example.wethepeople.ui.theme.*

/**
 * Data class to hold leaderboard entry information
 */
data class LeaderboardEagleyEntry(
    val rank: Int,
    val username: String,
    val score: Int,
    val eagleyBaseResourceId: Int = R.drawable.eagley_base,
    val eagleyHatResourceId: Int? = null,
    val userRank: String = "PATRIOT",
    val isCurrentUser: Boolean = false
)

/**
 * Efficient eagley rendering for leaderboard items
 * This uses direct resource IDs rather than repository lookups for performance
 */
@Composable
fun LeaderboardEagleyItem(
    entry: LeaderboardEagleyEntry,
    isTopThree: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        entry.isCurrentUser -> Color(0x22FFCC00) // Highlight current user
        isTopThree -> Color(0x110066CC) // Subtle highlight for top 3
        else -> Color.Transparent
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (entry.isCurrentUser) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank indicator
            RankIndicator(rank = entry.rank, isTopThree = isTopThree)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Eagley avatar with hat if present
            EagleyAvatar(eagleyHat = entry.eagleyHatResourceId)
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Username and points
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = entry.username,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Medium,
                        fontSize = if (isTopThree) 18.sp else 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = "${entry.score} Patriot Points",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = if (isTopThree) 14.sp else 12.sp,
                        color = Color.Gray
                    )
                )
            }
            
            // Trophy for top 3
            if (isTopThree) {
                Image(
                    painter = painterResource(
                        id = when (entry.rank) {
                            1 -> R.drawable.ic_trophy_gold
                            2 -> R.drawable.ic_trophy_silver
                            3 -> R.drawable.ic_trophy_bronze
                            else -> R.drawable.ic_trophy_bronze
                        }
                    ),
                    contentDescription = "Trophy",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun RankIndicator(
    rank: Int,
    isTopThree: Boolean
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(
                when {
                    rank == 1 -> Color(0xFFFFD700) // Gold
                    rank == 2 -> Color(0xFFC0C0C0) // Silver
                    rank == 3 -> Color(0xFFCD7F32) // Bronze
                    else -> patriot_blue.copy(alpha = 0.8f)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isTopThree) Color.Black else Color.White
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EagleyAvatar(
    eagleyHat: Int?
) {
    Box(
        modifier = Modifier
            .size(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Base Eagley image
        Image(
            painter = painterResource(id = R.drawable.eagley_base),
            contentDescription = "Eagley",
            modifier = Modifier
                .size(48.dp)
                .offset(y = 6.dp)
                .clip(CircleShape)
        )
        
        // Hat on top if present
        eagleyHat?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "Eagley Hat",
                modifier = Modifier
                    .size(32.dp)
                    .offset(y = (-24).dp)
                    .align(Alignment.TopCenter)
            )
        }
    }
}

/**
 * Preview example of leaderboard with multiple eagleys
 */
@Composable
fun LeaderboardList(entries: List<LeaderboardEagleyEntry>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(patriot_dark_blue)
            .padding(vertical = 8.dp)
    ) {
        // Header
        Text(
            text = "PATRIOT LEADERBOARD",
            color = patriot_white,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        // Top patriots section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = patriot_red_bright
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOP PATRIOTS",
                    color = patriot_white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // Top 3 patriots in horizontal layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    entries.take(3).forEach { entry ->
                        TopPatriotItem(entry = entry)
                    }
                }
            }
        }
        
        // Full leaderboard list
        entries.forEach { entry ->
            LeaderboardEagleyItem(entry = entry)
        }
    }
}

@Composable
fun TopPatriotItem(entry: LeaderboardEagleyEntry) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(patriot_dark_blue)
                .border(
                    width = 2.dp,
                    color = when (entry.rank) {
                        1 -> patriot_gold
                        2 -> Color(0xFFBDBCBC) // Silver
                        3 -> Color(0xFFCD7F32) // Bronze
                        else -> patriot_white
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Base Eagley image
            Image(
                painter = painterResource(id = entry.eagleyBaseResourceId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp)
                    .offset(y = 8.dp),
                contentScale = ContentScale.Fit
            )
            
            // Optional hat overlay with optimized position
            entry.eagleyHatResourceId?.let { hatResourceId ->
                Image(
                    painter = painterResource(id = hatResourceId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(0.60f)
                        .offset(y = (-24).dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = entry.username,
            color = patriot_white,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        
        Text(
            text = entry.score.toString(),
            color = when (entry.rank) {
                1 -> patriot_gold
                2 -> Color(0xFFBDBCBC) // Silver
                3 -> Color(0xFFCD7F32) // Bronze
                else -> patriot_white
            },
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
} 