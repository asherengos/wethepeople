package com.example.wethepeople.ui.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import com.example.wethepeople.R
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.patriot_white
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    // States for animations
    var visible by remember { mutableStateOf(false) }
    var showProgressIndicator by remember { mutableStateOf(false) }
    
    // Start animations after composition
    LaunchedEffect(key1 = true) {
        visible = true
        delay(500)
        showProgressIndicator = true
        delay(2000)
        navController.navigate(Screen.Voting.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    
    // Background gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundTransition")
    val gradientPosition = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientAnimation"
    )
    
    // Scale animation for the title
    val titleScale = animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "titleScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        patriot_dark_blue,
                        patriot_medium_blue.copy(alpha = 0.8f),
                        patriot_dark_blue
                    ),
                    startY = gradientPosition.value,
                    endY = gradientPosition.value + 1000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Logo or image placeholder
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1000)) + 
                        slideInVertically(animationSpec = tween(1000), initialOffsetY = { -50 })
            ) {
                // Note: Replace with your actual logo resource ID
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation), // placeholder, replace with your logo
                    contentDescription = "Logo",
                    colorFilter = ColorFilter.tint(patriot_gold),
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 24.dp)
                )
            }
            
            // Title with scale animation
            Text(
                text = "PARTY OF THE PEOPLE",
                style = MaterialTheme.typography.headlineLarge,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .scale(titleScale.value)
                    .padding(bottom = 8.dp)
            )
            
            // Tagline
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(1500))
            ) {
                Text(
                    text = "FREEDOM THROUGH VOTING",
                    style = MaterialTheme.typography.bodyLarge,
                    color = patriot_red_bright,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }
            
            // Progress indicator
            AnimatedVisibility(visible = showProgressIndicator) {
                CircularProgressIndicator(
                    color = patriot_gold,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        
        // Version or copyright info at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "© 2023 Freedom Fighters",
                color = patriot_white.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
} 