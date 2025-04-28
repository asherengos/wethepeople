package com.example.wethepeople.ui.screens.patriot

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.theme.*
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.wethepeople.R
import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import kotlin.math.roundToInt
import androidx.hilt.navigation.compose.hiltViewModel

data class PatriotReward(
    val level: Int,
    val title: String,
    val description: String,
    val iconResId: Int,
    val isUnlocked: Boolean = false
)

@Composable
fun PatriotPathScreen(
    viewModel: PatriotPathViewModel = hiltViewModel()
) {
    val scrollState = rememberLazyListState()
    
    // Collect states
    val currentLevel by viewModel.currentLevel.collectAsState()
    val currentXp by viewModel.currentXp.collectAsState()
    val xpToNextLevel by viewModel.xpToNextLevel.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(patriot_dark_blue)
    ) {
        // Background eagle watermark
        Image(
            painter = painterResource(id = R.drawable.wethepeople_seal),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.05f)
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center),
                color = patriot_gold
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "PATRIOT PATH",
                        style = MaterialTheme.typography.headlineMedium,
                        color = patriot_gold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // Current Level Info
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = patriot_medium_blue)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CURRENT LEVEL",
                            color = patriot_white,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentLevel.toString(),
                            color = patriot_gold,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { currentXp.toFloat() / xpToNextLevel.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = patriot_gold,
                            trackColor = patriot_white.copy(alpha = 0.2f)
                        )
                        Text(
                            text = "$currentXp/$xpToNextLevel XP to Level ${currentLevel + 1}",
                            color = patriot_white.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Vertical Path with Rewards
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    reverseLayout = true  // Start from bottom
                ) {
                    items(rewards.size) { index ->
                        val reward = rewards[index]
                        PatriotRewardNode(
                            reward = reward,
                            isFirst = index == rewards.size - 1,
                            isLast = index == 0
                        )
                    }
                }
            }

            // Swipe up hint overlay
            AnimatedSwipeHint(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun AnimatedSwipeHint(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "swipe")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrow"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Swipe up",
            tint = patriot_white.copy(alpha = 0.6f),
            modifier = Modifier
                .size(32.dp)
                .offset(y = -arrowOffset.dp)
        )
        Text(
            text = "Swipe up to explore",
            color = patriot_white.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun PatriotRewardNode(
    reward: PatriotReward,
    isFirst: Boolean,
    isLast: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Connection line above
        if (!isFirst) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(
                        color = if (reward.isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        // Reward node
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (reward.isUnlocked) 
                    patriot_gold.copy(alpha = 0.2f) 
                else 
                    patriot_medium_blue.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
                width = 2.dp,
                color = if (reward.isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.2f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Level circle
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (reward.isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = reward.level.toString(),
                        color = if (reward.isUnlocked) Color.Black else patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Reward info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = reward.title,
                        color = if (reward.isUnlocked) patriot_gold else patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = reward.description,
                        color = patriot_white.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                // Reward icon/status
                Icon(
                    imageVector = if (reward.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                    contentDescription = if (reward.isUnlocked) "Unlocked" else "Locked",
                    tint = if (reward.isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Connection line below
        if (!isLast) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(
                        color = if (reward.isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
} 