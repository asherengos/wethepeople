package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partyofthepeople.ui.utils.CommentRewardManager

/**
 * Screen that explains the Leaderboard system and how users can earn Freedom Bucks
 * through comment activity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardInfoScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard & Rewards") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0A1929),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1D3557))
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "HOW TO EARN FREEDOM BUCKS",
                color = Color(0xFFFFD700),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Your patriotic engagement earns you Freedom Bucks and increases your standing!",
                color = Color.White,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Reward section header
            Text(
                text = "COMMENT REWARDS",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Posting comments reward
            RewardCard(
                title = "POST A COMMENT",
                description = "Share your patriotic thoughts on proposals",
                rewardAmount = CommentRewardManager.COMMENT_POSTING_REWARD,
                iconText = "💬"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Receiving upvotes reward
            RewardCard(
                title = "RECEIVE AN UPVOTE",
                description = "When fellow patriots appreciate your comments",
                rewardAmount = CommentRewardManager.UPVOTE_RECEIVED_REWARD,
                iconText = "👍"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Milestone rewards
            Text(
                text = "MILESTONE BONUSES",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 10 upvotes milestone
            RewardCard(
                title = "10 UPVOTES ON A COMMENT",
                description = "Your comment resonated with fellow patriots",
                rewardAmount = CommentRewardManager.COMMENT_WITH_10_UPVOTES_BONUS,
                iconText = "🏅"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 50 upvotes milestone
            RewardCard(
                title = "50 UPVOTES ON A COMMENT",
                description = "Your wisdom is widely appreciated",
                rewardAmount = CommentRewardManager.COMMENT_WITH_50_UPVOTES_BONUS,
                iconText = "🥈"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 100 upvotes milestone
            RewardCard(
                title = "100 UPVOTES ON A COMMENT",
                description = "Your comment has made history!",
                rewardAmount = CommentRewardManager.COMMENT_WITH_100_UPVOTES_BONUS,
                iconText = "🥇"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Leaderboard section
            Text(
                text = "LEADERBOARD RANKINGS",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Power Score ranking
            RankingTypeCard(
                title = "POWER SCORE ⚡",
                description = "Overall measure of your influence and patriotic engagement",
                details = "Combines participation, voting activity, comment interactions, and more"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Comment Activity ranking
            RankingTypeCard(
                title = "COMMENT ACTIVITY 💬",
                description = "Measures your active participation in discussions",
                details = "Based on positive interactions through comments and received upvotes"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Participation ranking
            RankingTypeCard(
                title = "PARTICIPATION 🏆",
                description = "Tracks your overall engagement with the platform",
                details = "All activities contribute to this score, including voting and daily logins"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Streak Days ranking
            RankingTypeCard(
                title = "STREAK DAYS 🔥",
                description = "Rewards your consistent daily engagement",
                details = "Login every day to increase your streak and climb this leaderboard"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Freedom Bucks usage
            Text(
                text = "USING FREEDOM BUCKS",
                color = Color(0xFFFFD700),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usage options
            UsageCard(
                title = "PROFILE BOOST",
                description = "Stand out in leaderboards and get more visibility",
                cost = "50 FB"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            UsageCard(
                title = "CUSTOM THEMES",
                description = "Personalize your profile with patriotic themes",
                cost = "100-300 FB"
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            UsageCard(
                title = "TITLE SHOWCASE",
                description = "Highlight your earned titles with special effects",
                cost = "50 FB"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Final call to action
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF457B9D)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "START EARNING TODAY!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Comment on proposals, vote on laws, and engage with fellow patriots to earn Freedom Bucks and climb the leaderboards!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RewardCard(
    title: String,
    description: String,
    rewardAmount: Int,
    iconText: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A4A73)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = iconText,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .wrapContentSize(Alignment.Center)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            
            // Reward amount
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Freedom Bucks",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "+$rewardAmount",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun RankingTypeCard(
    title: String,
    description: String,
    details: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF203A56)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                color = Color(0xFFFFD700),
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = details,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun UsageCard(
    title: String,
    description: String,
    cost: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF203A56)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = description,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }
            
            // Cost
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF457B9D)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = cost,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
} 