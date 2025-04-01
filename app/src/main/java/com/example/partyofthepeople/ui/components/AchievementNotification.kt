package com.example.partyofthepeople.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.partyofthepeople.Achievement
import kotlinx.coroutines.delay

/**
 * A notification component that displays when an achievement is unlocked
 */
@Composable
fun AchievementNotification(
    achievement: Achievement,
    onDismiss: () -> Unit
) {
    var showNotification by remember { mutableStateOf(true) }
    
    // Auto-dismiss after 5 seconds
    LaunchedEffect(achievement) {
        delay(5000)
        showNotification = false
        delay(300) // Allow exit animation to play
        onDismiss()
    }
    
    AnimatedVisibility(
        visible = showNotification,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1D3557))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Achievement icon with pulsating animation
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFF457B9D), CircleShape)
                        .clip(CircleShape)
                ) {
                    // Gold star animation in the background
                    PulsatingBackground()
                    
                    // Achievement icon
                    if (achievement.icon.isNotEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = achievement.icon,
                                fontSize = 24.sp
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🏆",
                                fontSize = 24.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "ACHIEVEMENT UNLOCKED!",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = achievement.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = achievement.description,
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

/**
 * Creates a pulsating golden glow behind the achievement icon
 */
@Composable
private fun PulsatingBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "Pulsating")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Scale"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = (scale - 0.8f) / 0.4f
            }
            .background(Color(0xFFFFD700), CircleShape)
    )
}

/**
 * A component to show achievements as they are unlocked
 */
@Composable
fun AchievementNotificationHost(
    achievements: List<Achievement>,
    onAchievementDismissed: (Achievement) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (achievements.isNotEmpty()) {
            AchievementNotification(
                achievement = achievements.first(),
                onDismiss = { onAchievementDismissed(achievements.first()) }
            )
        }
    }
} 