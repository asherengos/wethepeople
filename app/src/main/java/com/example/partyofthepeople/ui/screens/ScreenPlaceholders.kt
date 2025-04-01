package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AchievementsScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Achievements",
        description = "View all your achievements here",
        onBackClick = onBackClick
    )
}

@Composable
fun BipartisanMatchScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Bipartisan Match",
        description = "Find users with similar and different political views",
        onBackClick = onBackClick
    )
}

@Composable
fun ElectionHistoryScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Election History",
        description = "View your voting history and election results",
        onBackClick = onBackClick
    )
}

@Composable
fun ControversyRadarScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Controversy Radar",
        description = "Discover the most divisive issues and see where you stand",
        onBackClick = onBackClick
    )
}

@Composable
fun ProfileCustomizationScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Customize Profile",
        description = "Personalize your Freedom Fighter avatar and profile",
        onBackClick = onBackClick
    )
}

@Composable
fun ShopScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Lobbying Lounge",
        description = "Spend your Freedom Bucks on patriotic cosmetics and perks",
        onBackClick = onBackClick
    )
}

@Composable
fun LeaderboardScreen(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Leaderboard",
        description = "See the top Patriots in different categories",
        onBackClick = onBackClick
    )
}

@Composable
fun LeaderboardInfoScreenPlaceholder(onBackClick: () -> Unit) {
    PlaceholderScreen(
        title = "Leaderboard Info",
        description = "Learn how to climb the ranks",
        onBackClick = onBackClick
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    description: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = description,
            fontSize = 16.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Coming Soon!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
} 