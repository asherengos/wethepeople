package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.partyofthepeople.Achievement
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    achievements: List<Achievement>,
    onBackClick: () -> Unit
) {
    val visibleAchievements = achievements
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557))
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Achievements") },
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
        
        // Header text
        Text(
            text = "YOUR PATRIOTIC ACHIEVEMENTS",
            color = Color(0xFFFFD700),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        
        // Achievement stats
        AchievementStats(
            totalAchievements = achievements.size,
            unlockedAchievements = visibleAchievements.size
        )
        
        // Grid of achievement icons
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(achievements) { achievement ->
                AchievementGridItem(
                    achievement = achievement,
                    onClick = { selectedAchievement = achievement }
                )
            }
        }
    }
    
    // Achievement detail dialog
    selectedAchievement?.let { achievement ->
        AchievementDetailDialog(
            achievement = achievement,
            onDismiss = { selectedAchievement = null }
        )
    }
}

@Composable
fun AchievementStats(
    totalAchievements: Int,
    unlockedAchievements: Int
) {
    val completionPercentage = if (totalAchievements > 0) {
        (unlockedAchievements.toFloat() / totalAchievements.toFloat()) * 100f
    } else 0f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF457B9D)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$unlockedAchievements/$totalAchievements Achievements Unlocked",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = unlockedAchievements.toFloat() / totalAchievements.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFFE63946),
                trackColor = Color(0xFF457B9D)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = String.format("%.1f%% Complete", completionPercentage),
                color = Color(0xFFFFD700),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AchievementGridItem(
    achievement: Achievement,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    if (achievement.dateEarned > 0) Color(0xFF457B9D)
                    else Color.Gray.copy(alpha = 0.5f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (achievement.dateEarned > 0 && achievement.icon.isNotEmpty()) {
                // Unlocked achievement with icon
                AsyncImage(
                    model = achievement.icon,
                    contentDescription = achievement.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else if (achievement.dateEarned > 0) {
                // Unlocked achievement without icon
                Text(
                    text = "🏆",
                    fontSize = 32.sp
                )
            } else {
                // Locked achievement
                Text(
                    text = "?",
                    fontSize = 32.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = if (achievement.dateEarned > 0) achievement.title else "Locked",
            color = if (achievement.dateEarned > 0) Color.White else Color.Gray,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
fun AchievementDetailDialog(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1D3557),
        title = {
            if (achievement.dateEarned > 0) {
                Text(
                    text = achievement.title,
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Locked Achievement",
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Achievement icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (achievement.dateEarned > 0) Color(0xFF457B9D)
                            else Color.Gray.copy(alpha = 0.5f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (achievement.dateEarned > 0 && achievement.icon.isNotEmpty()) {
                        AsyncImage(
                            model = achievement.icon,
                            contentDescription = achievement.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (achievement.dateEarned > 0) {
                        Text(
                            text = "🏆",
                            fontSize = 48.sp
                        )
                    } else {
                        Text(
                            text = "?",
                            fontSize = 48.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Achievement description
                Text(
                    text = if (achievement.dateEarned > 0) achievement.description 
                          else "Keep participating to unlock this achievement!",
                    color = if (achievement.dateEarned > 0) Color.White else Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                if (achievement.dateEarned > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Date earned
                    val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                    val dateEarned = Date(achievement.dateEarned)
                    
                    Text(
                        text = "Unlocked on: ${dateFormat.format(dateEarned)}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457B9D))
            ) {
                Text("Close")
            }
        }
    )
} 