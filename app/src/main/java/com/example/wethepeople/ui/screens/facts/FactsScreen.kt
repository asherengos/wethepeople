package com.example.wethepeople.ui.screens.facts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.patriot_white

@Composable
fun FactsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(patriot_dark_blue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "ACHIEVEMENTS",
                color = patriot_white,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            // Achievement progress summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = patriot_medium_blue),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PATRIOT PROGRESS",
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "14/30 Achievements Unlocked",
                        color = patriot_gold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = 0.47f,
                        modifier = Modifier.fillMaxWidth(),
                        color = patriot_gold,
                        trackColor = patriot_dark_blue
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Badges section
            Text(
                text = "BADGES",
                color = patriot_white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Left
            )
            
            // badges in a row (3 per row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AchievementBadge(
                    name = "FIRST VOTE",
                    icon = Icons.Default.Check,
                    color = patriot_gold,
                    unlocked = true
                )
                
                AchievementBadge(
                    name = "PERFECT VOTER",
                    icon = Icons.Default.Star,
                    color = patriot_gold,
                    unlocked = true
                )
                
                AchievementBadge(
                    name = "PATRIOT",
                    icon = Icons.Default.Star,
                    color = patriot_gold,
                    unlocked = false
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AchievementBadge(
                    name = "EAGLE EYE",
                    icon = Icons.Default.Star,
                    color = patriot_red_bright,
                    unlocked = true
                )
                
                AchievementBadge(
                    name = "FREEDOM FIGHTER",
                    icon = Icons.Default.Star,
                    color = patriot_red_bright,
                    unlocked = false
                )
                
                AchievementBadge(
                    name = "CONSTITUTION LOVER",
                    icon = Icons.Default.Star,
                    color = patriot_red_bright,
                    unlocked = false
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Recent achievements
            Text(
                text = "RECENT ACHIEVEMENTS",
                color = patriot_white,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Left
            )
            
            AchievementCard(
                title = "PERFECT VOTER",
                description = "Voted in 5 consecutive polls",
                points = 50,
                unlocked = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AchievementCard(
                title = "EAGLE EYE",
                description = "Spotted 10 pieces of fake news",
                points = 25,
                unlocked = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AchievementCard(
                title = "FREEDOM FIGHTER",
                description = "Participated in 25 debates",
                points = 100,
                unlocked = false,
                progress = 0.6f,
                progressText = "15/25"
            )
            
            Spacer(modifier = Modifier.height(80.dp)) // Space at bottom for navigation bar
        }
    }
}

@Composable
fun AchievementBadge(
    name: String,
    icon: ImageVector,
    color: Color,
    unlocked: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(if (unlocked) color else Color.Gray.copy(alpha = 0.5f))
                .border(2.dp, if (unlocked) patriot_gold else Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (unlocked) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = patriot_white,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = patriot_white.copy(alpha = 0.7f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = name,
            color = if (unlocked) patriot_white else patriot_white.copy(alpha = 0.5f),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun AchievementCard(
    title: String,
    description: String,
    points: Int,
    unlocked: Boolean,
    progress: Float = 1f,
    progressText: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) patriot_medium_blue else patriot_dark_blue.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Achievement icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(if (unlocked) patriot_gold else Color.Gray)
                    .border(2.dp, if (unlocked) patriot_gold else Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (unlocked) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = title,
                        tint = patriot_dark_blue,
                        modifier = Modifier.size(30.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = patriot_white.copy(alpha = 0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = if (unlocked) patriot_white else patriot_white.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Text(
                    text = description,
                    color = if (unlocked) patriot_white.copy(alpha = 0.8f) else patriot_white.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                
                if (!unlocked && progress < 1f) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp),
                            color = patriot_gold,
                            trackColor = patriot_dark_blue
                        )
                        
                        if (progressText.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = progressText,
                                color = patriot_white.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Points
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+$points",
                    color = if (unlocked) patriot_gold else patriot_white.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Text(
                    text = "POINTS",
                    color = if (unlocked) patriot_gold else patriot_white.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
} 