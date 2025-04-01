package com.example.partyofthepeople.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partyofthepeople.Achievement
import kotlinx.coroutines.delay

@Composable
fun AchievementNotificationHost(
    modifier: Modifier = Modifier
) {
    var currentAchievement by remember { mutableStateOf<Achievement?>(null) }
    
    // Mock achievement for display
    LaunchedEffect(Unit) {
        delay(5000)
        currentAchievement = Achievement(
            id = "new_achievement",
            title = "First Vote Cast!",
            description = "You cast your first vote as a Freedom Fighter",
            icon = "🏆",
            dateEarned = System.currentTimeMillis()
        )
        
        delay(5000)
        currentAchievement = null
    }
    
    currentAchievement?.let { achievement ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFD700).copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆 ACHIEVEMENT UNLOCKED!",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = achievement.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Text(
                        text = achievement.description,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
} 