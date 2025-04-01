package com.example.partyofthepeople

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partyofthepeople.ui.screens.ProfileScreen2
import com.example.partyofthepeople.ui.screens.AchievementsScreen
import com.example.partyofthepeople.ui.screens.BipartisanMatchScreen
import com.example.partyofthepeople.ui.screens.ElectionHistoryScreen
import com.example.partyofthepeople.ui.screens.ControversyRadarScreen

enum class Screen {
    MAIN, BATTLEGROUND, DEBATE, PROFILE
}

@Composable
fun FreedomFightersApp(
    userProfile: UserProfile?,
    onGuestLogin: () -> Unit,
    onGoogleLogin: () -> Unit,
    onLeaderboardClick: () -> Unit
) {
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(Screen.MAIN) }
    var patriotPoints by remember { mutableStateOf(150) }
    var freedomBucks by remember { mutableStateOf(250) }

    LaunchedEffect(true) {
        delay(3000)
        showSplash = false
    }

    when {
        showSplash -> SplashScreen()
        userProfile == null -> LoginScreen(
            onGuestLogin = onGuestLogin,
            onGoogleLogin = onGoogleLogin
        )
        else -> {
            when (currentScreen) {
                Screen.MAIN -> MainScreen(
                    onDebateClick = { currentScreen = Screen.DEBATE },
                    onBattlegroundClick = { currentScreen = Screen.BATTLEGROUND },
                    onProfileClick = { currentScreen = Screen.PROFILE },
                    patriotPoints = patriotPoints,
                    freedomBucks = freedomBucks,
                    onIRSRaidClick = { /* TODO: Implement IRS raid */ }
                )
                Screen.BATTLEGROUND -> BattlegroundScreen(
                    onBackClick = { currentScreen = Screen.MAIN }
                )
                Screen.DEBATE -> DebateScreen(
                    onBackClick = { currentScreen = Screen.MAIN }
                )
                Screen.PROFILE -> ProfileScreen2(
                    profile = userProfile,
                    onBackClick = { currentScreen = Screen.MAIN },
                    onViewAllAchievements = { /* TODO */ },
                    onViewBipartisanMatch = { /* TODO */ },
                    onViewElectionHistory = { /* TODO */ },
                    onViewControversyRadar = { /* TODO */ },
                    onCustomizeProfile = { /* TODO */ },
                    onManageTitles = { /* TODO */ }
                )
            }
        }
    }
}

// Placeholder components for missing screens
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🦅 FREEDOM FIGHTERS 🦅",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Loading democracy...",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LoginScreen(
    onGuestLogin: () -> Unit,
    onGoogleLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "FREEDOM FIGHTERS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Button(onClick = onGoogleLogin) {
                Text("Sign in with Google")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(onClick = onGuestLogin) {
                Text("Continue as Guest")
            }
        }
    }
}
