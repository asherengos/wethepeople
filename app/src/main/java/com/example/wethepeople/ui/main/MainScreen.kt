package com.example.wethepeople.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_white
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_red_bright
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.runtime.remember
import kotlin.math.abs
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.navigation.NavArgs
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.wethepeople.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import com.example.wethepeople.data.model.CitizenRank
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.random.Random
import kotlin.math.sin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.alpha
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.animation.core.CubicBezierEasing
import com.example.wethepeople.ui.icons.CustomIcons
import com.example.wethepeople.ui.animation.LottieAnimationComponent
import com.example.wethepeople.ui.animation.ConstitutionAnimation
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.wethepeople.ui.theme.patriot_red
import com.example.wethepeople.ui.theme.patriot_blue
import androidx.compose.ui.platform.LocalContext
import com.example.wethepeople.data.repository.UserRepository
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import com.example.wethepeople.ui.theme.ArtySignatureFontFamily
import com.example.wethepeople.ui.theme.WeThePeopleFontFamily
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import com.example.wethepeople.data.repository.EagleyRepository
import com.example.wethepeople.data.model.EagleyItem
import com.example.wethepeople.data.model.ItemRarity
import com.example.wethepeople.ui.theme.RankSignatureFontFamily
import com.example.wethepeople.ui.components.EagleySignature
import com.example.wethepeople.ui.components.CustomizedEagley
import androidx.compose.material3.HorizontalDivider
import android.util.Log
import androidx.compose.runtime.remember

// EaseInOutQuart equivalent in Compose
private val EaseInOutQuart = CubicBezierEasing(0.77f, 0f, 0.175f, 1f)

@Composable
fun Particle(
    color: Color,
    startPosition: Offset,
    endPosition: Offset,
    duration: Int = 2000,
    size: Float = 10f
) {
    val animatable = remember { Animatable(0f) }
    
    // Cache calculations
    val animationSpec = remember {
        tween<Float>(
            durationMillis = duration,
            easing = FastOutSlowInEasing
        )
    }
    
    val sinMultiplier = remember { 4 * PI.toFloat() }
    
    LaunchedEffect(Unit) {
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }
    
    val animatedPosition = remember(animatable.value) {
        Offset(
        x = startPosition.x + (endPosition.x - startPosition.x) * animatable.value,
        y = startPosition.y + (endPosition.y - startPosition.y) * animatable.value +
                    sin(animatable.value * sinMultiplier) * 50
    )
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                alpha = 1f - animatable.value
            }
    ) {
        drawCircle(
            color = color,
            radius = size * (1f - animatable.value),
            center = animatedPosition
        )
    }
}

@Composable
fun ParticleEffect(
    color: Color,
    particleCount: Int = 15,
    duration: Int = 2000
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val particles = remember {
            List(particleCount) {
            val startX = Random.nextFloat() * 500
            val startY = Random.nextFloat() * 500
            val endX = startX + Random.nextFloat() * 400 - 200
            val endY = startY + Random.nextFloat() * 400 - 200
                val size = Random.nextFloat() * 15 + 5
                val particleDuration = duration + Random.nextInt(1000)
                
                ParticleData(
                    startPos = Offset(startX, startY),
                    endPos = Offset(endX, endY),
                    size = size,
                    duration = particleDuration
                )
            }
        }
        
        particles.forEach { data ->
            Particle(
                color = color,
                startPosition = data.startPos,
                endPosition = data.endPos,
                duration = data.duration,
                size = data.size
            )
        }
    }
}

private data class ParticleData(
    val startPos: Offset,
    val endPos: Offset,
    val size: Float,
    val duration: Int
)

@Composable
fun RankUpCelebration(
    newRank: CitizenRank,
    onDismiss: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var showParticles by remember { mutableStateOf(false) } // Start false
    val rotation = rememberInfiniteRotation()
    
    // Defer particle effects
    LaunchedEffect(Unit) {
        delay(300)
        showParticles = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + scaleIn(
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut() + scaleOut(
            animationSpec = tween(300)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable { 
                    isVisible = false
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            if (showParticles) {
                ParticleEffect(
                    color = Color(newRank.color),
                    particleCount = 50,
                    duration = 2500
                )
                ParticleEffect(
                    color = patriot_gold,
                    particleCount = 30,
                    duration = 3000
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 80.sp,
                        color = Color(newRank.color),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "CONGRATULATIONS, PATRIOT!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = patriot_gold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "You've achieved the rank of",
                    fontSize = 16.sp,
                    color = patriot_white
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = newRank.icon,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = newRank.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(newRank.color)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = newRank.description,
                    fontSize = 14.sp,
                    color = patriot_white.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "Tap anywhere to continue",
                    fontSize = 12.sp,
                    color = patriot_white.copy(alpha = 0.7f)
                )
            }
        }
    }
    
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(300)
            showParticles = false
        }
    }
}

// Helper for breathing animation
@Composable
private fun rememberBreathingAnimation(initialValue: Float = 1f, targetValue: Float = 1.1f): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    return infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "breathingScale"
    ).value
}

@Composable
fun LazyLoad(
    content: @Composable () -> Unit
) {
    var shouldLoad by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100) // Small delay for initial layout
        shouldLoad = true
    }
    
    if (shouldLoad) {
        content()
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val userStats by viewModel.userStats.collectAsState()
    val eagleyData by viewModel.eagleyData.collectAsState()
    val activities by viewModel.recentActivities.collectAsState()
    val missions by viewModel.dailyMissions.collectAsState()
    val showRankUpCelebration by viewModel.showRankUpCelebration.collectAsState()
    
    // Observe navigation events
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    
    // Handle navigation
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { destination ->
            navController.navigate(destination)
            viewModel.onNavigationHandled()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(patriot_dark_blue)
    ) {
        // Log recomposition of the entire MainScreen Column
        Log.d("MainScreen", "Recomposing MainScreen Column")
        
        // Essential UI elements load immediately
        Header()
        
        // Heavy components load lazily
        // COMMENT OUT LazyLoad wrapper
        // LazyLoad {
            // Remember the lambda to prevent it from causing recomposition
            val stableOnCustomizeClick = remember { { viewModel.onCustomizeClick() } }
            ProfileCard(
                userStats = userStats,
                eagleyData = eagleyData,
                onCustomizeClick = stableOnCustomizeClick // Pass the stable lambda
            )
        // }
        
        LazyLoad {
            RecentActivitySection(activities = activities)
        }
        
        LazyLoad {
            DailyMissionsSection(missions = missions)
        }
        
        // Defer particle effects and celebrations
        LazyLoad {
        if (showRankUpCelebration) {
            RankUpCelebration(
                    newRank = viewModel.currentRank,
                    onDismiss = remember { { viewModel.dismissRankUpCelebration() } }
            )
            }
        }
    }
}

@Composable
private fun StatsRow(userStats: UserStats) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Patriot Points
        StatBox(
            label = "PATRIOT\nPOINTS",
            value = "${userStats.patriotPoints}",
            icon = "⭐",
            color = patriot_gold
        )
        
        // Vertical Divider
        Box(
            modifier = Modifier
                .height(50.dp)
                .width(1.dp)
                .background(patriot_white.copy(alpha = 0.2f))
        )
        
        // Freedom Bucks
        StatBox(
            label = "FREEDOM\nCOINS",
            value = "${userStats.freedomBucks}",
            icon = "💰",
            color = patriot_gold
        )
        
        // Vertical Divider
        Box(
            modifier = Modifier
                .height(50.dp)
                .width(1.dp)
                .background(patriot_white.copy(alpha = 0.2f))
        )
        
        // Citizen Rank
        StatBox(
            label = "CITIZEN\nRANK",
            value = userStats.citizenRank.title,
            icon = userStats.citizenRank.icon,
            color = Color(userStats.citizenRank.color)
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    icon: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = label,
            color = patriot_white.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 4.dp)
            )
            AnimatedCounter(
                count = value.filter { it.isDigit() }.toIntOrNull() ?: 0,
                color = color,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// Custom Star Shape
class StarShape(private val points: Int = 5) : Shape {

    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val radius = size.minDimension / 2f
        val angle = (2.0 * PI) / (points * 2)
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val innerRadius = radius * 0.4f // Adjust for pointiness

        var currentAngle = -PI / 2.0 // Start at the top point

        path.moveTo(
            centerX + (radius * cos(currentAngle)).toFloat(),
            centerY + (radius * sin(currentAngle)).toFloat()
        )

        for (i in 1 until points * 2) {
            val currentRadius = if (i % 2 == 0) radius else innerRadius
            currentAngle += angle
            path.lineTo(
                centerX + (currentRadius * cos(currentAngle)).toFloat(),
                centerY + (currentRadius * sin(currentAngle)).toFloat()
            )
        }
        path.close()
        return Outline.Generic(path)
    }
}

@Composable
private fun ProfileCard(
    userStats: UserStats,
    eagleyData: EagleyDisplayData,
    onCustomizeClick: () -> Unit
) {
    // Log recomposition of the entire ProfileCard
    // Log.d("MainScreen", "Recomposing ProfileCard with Data: $eagleyData") <-- REMOVE LOG HERE
    // val breathingScale = rememberBreathingAnimation(1f, 1.02f) // <-- COMMENT OUT AGAIN

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF7F5EC) // Lighter off-white for more realistic ID color
        ),
        shape = RoundedCornerShape(12.dp), // Slightly rounder corners
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp), // More elevation
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.3f)) // Slightly thicker border alpha
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(patriot_dark_blue) // Header background
                        .padding(horizontal = 16.dp, vertical = 8.dp), // More vertical padding
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FEDERAL VOTER ID",
                        fontFamily = WeThePeopleFontFamily, // Use app font
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp, // Slightly larger
                        color = patriot_gold,
                        letterSpacing = 1.sp // Add letter spacing
                    )
                    Text(
                        text = "🇺🇸",
                        fontSize = 18.sp // Slightly larger flag
                    )
                }

                // Main ID Content Section with Backdrop
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Faint Seal Backdrop Image
                    Image(
                        painter = painterResource(id = R.drawable.wethepeople_seal),
                        contentDescription = "Seal Backdrop",
                        modifier = Modifier
                            .matchParentSize() // Let it fill the Box
                            .padding(vertical = 12.dp) // Adjust padding
                            .alpha(0.12f), // Increased alpha
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp), // Adjusted padding
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left Side: Eagley Photo with proper ID-style border and background
                        Box(
                            modifier = Modifier
                                .size(110.dp) // Increased size
                                .background(Color.White, RoundedCornerShape(6.dp)) // Slightly rounder photo corners
                                .border(1.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp)), // Darker photo border
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp) // Increased inner size
                                    .clip(RoundedCornerShape(5.dp)) // Match rounded corners
                                    .border(1.dp, Color.Black.copy(alpha = 0.15f), RoundedCornerShape(5.dp)), // Inner darker border/shadow effect
                                contentAlignment = Alignment.Center
                            ) {
                                // Re-enable CustomizedEagley with our optimized version:
                                // COMMENT OUT CustomizedEagley <-- REMOVE COMMENT MARKERS
                                CustomizedEagley(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(3.dp)),
                                    baseResourceId = eagleyData.baseResourceId,
                                    hatResourceId = eagleyData.hatResourceId,
                                    accessoryResourceId = eagleyData.accessoryResourceId,
                                    hatItemId = eagleyData.hatItemId,
                                    accessoryItemId = eagleyData.accessoryItemId,
                                    customizations = eagleyData.customizations
                                )
                                // <-- REMOVE COMMENT MARKERS
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Right Side: Details
                        Column(modifier = Modifier.weight(1f)) {
                            IdDetailRow("IDENTIFIER:", "Patriot")
                            IdDetailRow("RANK:", "${userStats.citizenRank.icon} ${userStats.citizenRank.title}", contentColor = Color(userStats.citizenRank.color))
                            IdDetailRow("LEVEL:", "1") // Placeholder level
                            IdDetailRow("ISSUED:", "04/19/2025") // Placeholder date
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Patriot Points & Freedom Coins integrated
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                StatItem(label = "POINTS", value = userStats.patriotPoints.toString(), icon = "⭐")
                                StatItem(label = "COINS", value = userStats.freedomBucks.toString(), icon = "💰")
                            }
                        }
                    }
                }
                
                // Decorative Divider Line
                HorizontalDivider(
                    color = patriot_red.copy(alpha = 0.3f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Signature Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
                        .height(65.dp), // Slightly taller signature box
                    contentAlignment = Alignment.CenterStart 
                ) {
                    // Dashed border background
                    Box(modifier = Modifier
                        .matchParentSize()
                        .drawBehind {
                            val stroke = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f)) // Adjusted dash effect
                            drawRoundRect(
                                color = Color.Gray.copy(alpha = 0.4f), // Darker dash border
                                style = stroke,
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                    )
                    
                    // Signature Row (Now contains Eagley Talon RANK)
                    EagleySignature(
                        rank = userStats.citizenRank.title,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 16.dp) // More padding
                    )
                }
            }

            // "Starburst" Sticker Button - smaller and repositioned
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 50.dp, end = 14.dp) // Adjust position slightly
                    // .scale(breathingScale) // <-- COMMENT OUT AGAIN
                    .clickable(onClick = onCustomizeClick)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.customize_id_button),
                    contentDescription = "Customize ID Button",
                    modifier = Modifier.size(70.dp), // Smaller button
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

// Helper composable for ID details
@Composable
private fun IdDetailRow(label: String, value: String, contentColor: Color = Color.Black) {
    Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
            text = "$label ",
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = value,
                    fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = contentColor
        )
    }
}

// Helper composable for stats shown on the ID
@Composable
private fun StatItem(label: String, value: String, icon: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 12.sp, modifier = Modifier.padding(end = 4.dp))
        Text(
            text = "$label: $value",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = modifier.height(58.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon?.invoke()
            Text(
                text = text,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = if (icon != null) Modifier.padding(start = 8.dp) else Modifier
            )
        }
    }
}

@Composable
private fun ActionCenterSection(
    onVoteClick: () -> Unit,
    onDebateClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onProposeVoteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.95f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background animated constitution scroll with low opacity
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.06f)
            ) {
                // LottieAnimationComponent(
                //     animationRes = R.raw.constitution_scroll,
                //     modifier = Modifier.fillMaxSize()
                // )
            }
            
            // Foreground content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "ACTION CENTER",
                    color = patriot_white,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Vote Now Button - Patriotic Red with Ballot Icon
                    ActionButton(
                        text = "VOTE NOW",
                        color = Color(0xFFE63946), // Vibrant patriotic red
                        onClick = onVoteClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_ballot),
                                contentDescription = null,
                                tint = patriot_white,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    
                    // Propose Vote Button - Light Blue with Icon
                    ActionButton(
                        text = "PROPOSE VOTE",
                        color = Color(0xFF457B9D), // Patriotic blue with lighter shade
                        onClick = onProposeVoteClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = patriot_white,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    
                    // Debate Button - Deep Blue with Discussion Icon
                    ActionButton(
                        text = "DEBATE",
                        color = Color(0xFF1D3557), // Deep patriotic blue
                        onClick = onDebateClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_debate),
                                contentDescription = null,
                                tint = patriot_white,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                    
                    // Leaderboard Button - Gold with Trophy Icon
                    ActionButton(
                        text = "LEADERBOARDS",
                        color = Color(0xFFD4AF37), // Patriotic gold
                        onClick = onLeaderboardClick,
                        modifier = Modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_trophy),
                                contentDescription = null,
                                tint = patriot_white,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendingDebatesSection(onDebateClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background animated flag with low opacity
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.08f)
            ) {
                // LottieAnimationComponent(
                //     animationRes = R.raw.american_flag_wave,
                //     modifier = Modifier.fillMaxSize()
                // )
            }
            
            // Foreground content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "TRENDING NOW!",
                    color = patriot_gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                // First debate topic
                DebateTopic(
                    title = "Tax Evasion: Treason or Super-Treason?",
                    participants = "121 CITIZEN SOLDIERS",
                    timeAgo = "2 hours ago",
                    onClick = onDebateClick
                )
                
                Divider(
                    color = patriot_dark_blue.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                
                // Second debate topic
                DebateTopic(
                    title = "Should Voting Be MANDATORY for All Citizens?",
                    participants = "97 CITIZEN SOLDIERS",
                    timeAgo = "5 hours ago",
                    onClick = onDebateClick
                )
            }
        }
    }
}

@Composable
private fun DailyMissionsSection(missions: List<Mission>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.85f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background animated fireworks removed
            
            // Foreground content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "DAILY MISSIONS",
                    color = patriot_gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    missions.forEach { mission ->
                        MissionItem(
                            title = mission.title,
                            reward = "${mission.reward} PP",
                            progress = mission.progress
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentActivitySection(activities: List<Activity>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Background animated eagle with low opacity
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.08f)
            ) {
                // LottieAnimationComponent(
                //     animationRes = R.raw.eagle_fly,
                //     modifier = Modifier.fillMaxSize()
                // )
            }
            
            // Foreground content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    color = patriot_gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activities.forEach { activity ->
                        ActivityItem(activity.text)
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedCounter(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle()
) {
    var oldCount by remember { mutableStateOf(count) }
    var animatedCount by remember { mutableStateOf(count.toFloat()) }
    
    LaunchedEffect(count) {
        if (oldCount != count) {
            animate(
                initialValue = oldCount.toFloat(),
                targetValue = count.toFloat(),
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                )
            ) { value, _ ->
                animatedCount = value
            }
            oldCount = count
        }
    }
    
    Text(
        text = animatedCount.toInt().toString(),
        color = color,
        style = style,
        modifier = modifier
    )
}

@Composable
fun MissionItem(title: String, reward: String, progress: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = patriot_white, fontSize = 13.sp)
            Text(text = reward, color = patriot_gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = patriot_red_bright,
            trackColor = patriot_white.copy(alpha = 0.2f)
        )
    }
}

@Composable
fun ActivityItem(text: String) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)) + 
               slideInHorizontally(
                   animationSpec = tween(300),
                   initialOffsetX = { -40 }
               ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(patriot_red_bright, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = text, color = patriot_white, fontSize = 12.sp)
        }
    }
}

@Composable
fun DebateTopic(
    title: String, 
    participants: String, 
    timeAgo: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Red circle avatar
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(patriot_red_bright, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = CustomIcons.Debate,
                contentDescription = "Debate",
                tint = patriot_white,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = title,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = participants,
                    color = patriot_white.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
                Text(
                    text = " • $timeAgo",
                    color = patriot_white.copy(alpha = 0.6f),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun rememberInfiniteRotation(): Float {
    val transition = rememberInfiniteTransition(label = "starRotation")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "starRotation"
    ).value
}

@Composable
fun Header() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(patriot_dark_blue)
            .padding(16.dp)
    ) {
        Text(
            text = "We The People",
            color = patriot_gold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}