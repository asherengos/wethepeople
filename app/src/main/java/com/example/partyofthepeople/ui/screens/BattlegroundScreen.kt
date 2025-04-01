package com.example.partyofthepeople.ui.screens

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFFADD8E6),
            fontSize = 12.sp
        )
    }
}

@Composable
fun ResourceButton(
    text: String,
    cost: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A90E2)
        ),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(text = text, color = Color.White)
            Text(text = cost, fontSize = 12.sp, color = Color(0xFFFFD700))
        }
    }
}

@Composable
fun BattlegroundScreen(onBackClick: () -> Unit) {
    var selectedState by remember { mutableStateOf<String?>(null) }
    var isLandscape by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    
    // Listen for orientation changes
    LaunchedEffect(configuration.orientation) {
        isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF1D3557))
    ) {
        // Top bar
        TopAppBar(
            title = { Text("BATTLEGROUND MAP 🦅") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF2A4A73),
                titleContentColor = Color(0xFFFFD700)
            ),
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        )

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Account for TopAppBar
        ) {
            // Map container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF457B9D))
            ) {
                USMap(
                    modifier = Modifier.fillMaxSize(),
                    onStateSelected = { state ->
                        selectedState = state
                    }
                )
            }

            // State details panel
            AnimatedVisibility(
                visible = selectedState != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                selectedState?.let { state ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A4A73))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = state,
                                color = Color(0xFFFFD700),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // State stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                StatItem("Freedom Fighters", "1,776")
                                StatItem("Active Proposals", "42")
                                StatItem("Laws Passed", "13")
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Resource deployment buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ResourceButton(
                                    text = "Support Vote",
                                    cost = "20 FB",
                                    onClick = { /* TODO */ }
                                )
                                ResourceButton(
                                    text = "Super Vote",
                                    cost = "50 FB",
                                    onClick = { /* TODO */ }
                                )
                                ResourceButton(
                                    text = "Vote Shield",
                                    cost = "100 FB",
                                    onClick = { /* TODO */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 