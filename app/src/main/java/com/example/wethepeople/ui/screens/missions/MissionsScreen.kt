package com.example.wethepeople.ui.screens.missions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wethepeople.data.model.*
import com.example.wethepeople.ui.theme.*
import com.example.wethepeople.ui.viewmodel.MissionsViewModel
import kotlinx.coroutines.delay
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    viewModel: MissionsViewModel = viewModel(),
    onBackClick: () -> Unit,
    appNavController: NavHostController? = null
) {
    val dailyMissions by viewModel.dailyMissions.collectAsState()
    val weeklyMissions by viewModel.weeklyMissions.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val currentExp by viewModel.currentExp.collectAsState()
    val expToNextLevel by viewModel.expToNextLevel.collectAsState()
    
    // Determine which tab is selected
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Missions", "Patriot Path")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patriot Missions") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = patriot_medium_blue,
                    titleContentColor = patriot_white
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(patriot_dark_blue)
                .padding(padding)
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = patriot_medium_blue,
                contentColor = patriot_white,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = patriot_gold
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) patriot_gold else patriot_white
                            )
                        }
                    )
                }
            }
            
            when (selectedTab) {
                0 -> MissionsTab(
                    dailyMissions = dailyMissions,
                    weeklyMissions = weeklyMissions,
                    currentLevel = currentLevel,
                    currentExp = currentExp,
                    expToNextLevel = expToNextLevel
                )
                1 -> PatriotPathTab(currentLevel = currentLevel)
            }
        }
    }
}

@Composable
fun MissionsTab(
    dailyMissions: List<Mission>,
    weeklyMissions: List<Mission>,
    currentLevel: Int,
    currentExp: Int,
    expToNextLevel: Int
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                LevelProgressCard(
                    currentLevel = currentLevel,
                    currentExp = currentExp,
                    expToNextLevel = expToNextLevel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Text(
                    text = "Daily Missions",
                    style = MaterialTheme.typography.titleLarge,
                color = patriot_white,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

        items(
            items = dailyMissions,
            key = { it.id }
        ) { mission ->
                MissionCard(
                    mission = mission,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                Text(
                    text = "Weekly Missions",
                    style = MaterialTheme.typography.titleLarge,
                color = patriot_white,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

        items(
            items = weeklyMissions,
            key = { it.id }
        ) { mission ->
                MissionCard(
                    mission = mission,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

@Composable
fun PatriotPathTab(currentLevel: Int) {
    val scrollState = rememberScrollState()
    
    // Define rewards at different levels - customization items for Eagley
    val rewards = listOf(
        LevelReward(5, "🧢", "Patriot Cap", Color(0xFF1D3557), "A classic red cap for your Eagley"),
        LevelReward(10, "🕶️", "Freedom Shades", Color(0xFF457B9D), "Cool shades for a cool Eagley"),
        LevelReward(15, "🎽", "Star-Spangled Vest", Color(0xFFE63946), "Patriotic vest with stars and stripes"),
        LevelReward(20, "🎖️", "Veteran Medal", Color(0xFFD4AF37), "Honor your Eagley's service"),
        LevelReward(25, "🎩", "Uncle Sam Hat", Color(0xFF1D3557), "The iconic stars and stripes hat"),
        LevelReward(30, "🦺", "Constitution Armor", Color(0xFFD4AF37), "Protect your Eagley with liberty"),
        LevelReward(40, "🧣", "Liberty Scarf", Color(0xFFE63946), "A symbol of patriotic fashion"),
        LevelReward(50, "👑", "Freedom Crown", Color(0xFFD4AF37), "The ultimate symbol of patriotism"),
        LevelReward(75, "⚔️", "Founding Sword", Color(0xFF1D3557), "Defend democracy in style"),
        LevelReward(100, "🛡️", "Eagle Shield", Color(0xFFD4AF37), "The pinnacle of Eagley customization")
    )
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Path header with instructions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(patriot_medium_blue)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EAGLEY CUSTOMIZATION PATH",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = patriot_white
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Level up to unlock exclusive items!",
                    fontSize = 14.sp,
                    color = patriot_white.copy(alpha = 0.8f)
                )
            }
        }
        
        // Current level indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(patriot_medium_blue)
                        .border(3.dp, patriot_gold, CircleShape)
                ) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "LVL",
                            fontSize = 10.sp,
                            color = patriot_white
                        )
                        Text(
                            text = "$currentLevel",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = patriot_gold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "Current Level: $currentLevel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = patriot_white
                    )
                    val nextReward = rewards.find { it.level > currentLevel }
                    nextReward?.let {
                        Text(
                            text = "Next reward at level ${it.level}: ${it.description}",
                            fontSize = 14.sp,
                            color = patriot_white.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
        
        // Horizontal scrollable path with rewards
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(patriot_dark_blue)
        ) {
            // Scrollable content
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 80.dp)
                    .height(200.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Path with rewards
                val pathColor = patriot_red_bright
                val reachedColor = patriot_gold
                
                Box(
                    modifier = Modifier.height(8.dp)
                ) {
                    // Path line - goes horizontally
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width((rewards.last().level * 12).dp) // Width based on max level
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(reachedColor, pathColor),
                                    startX = 0f,
                                    endX = (rewards.last().level * 12f) * (currentLevel.coerceAtMost(rewards.last().level).toFloat() / rewards.last().level.toFloat())
                                )
                            )
                    )
                    
                    // Position rewards along the path
                    rewards.forEach { reward ->
                        val isUnlocked = currentLevel >= reward.level
                        val xPosition = reward.level.toFloat() * 12f
                        
                        // Reward node
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = xPosition.dp - 30.dp,
                                    y = if (reward.level % 2 == 0) (-80).dp else 20.dp
                                )
                                .size(60.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(if (isUnlocked) reward.color else patriot_medium_blue.copy(alpha = 0.7f))
                                .border(
                                    width = 2.dp,
                                    color = if (isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.3f),
                                    shape = CircleShape
                                )
                                .clickable(enabled = isUnlocked) {
                                    // Future implementation: Handle clicking on unlocked items
                                }
                        ) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = reward.icon,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Lvl ${reward.level}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = patriot_white
                                )
                            }
                        }
                        
                        // Reward description
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = xPosition.dp - 60.dp,
                                    y = if (reward.level % 2 == 0) (-140).dp else 80.dp
                                )
                                .width(120.dp)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = reward.description,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isUnlocked) patriot_white else patriot_white.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // Level markers on path
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = xPosition.dp - 8.dp,
                                    y = -4.dp
                                )
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isUnlocked) patriot_gold else patriot_medium_blue)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
            }
            
            // Instruction overlay at the bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, patriot_dark_blue),
                            startY = 0f,
                            endY = 100f
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = patriot_white
                    )
                    Text(
                        text = "Scroll to see more rewards",
                        color = patriot_white,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = patriot_white
                    )
                }
            }
        }
    }
}

data class LevelReward(
    val level: Int,
    val icon: String,
    val description: String,
    val color: Color,
    val tooltip: String = ""
)

@Composable
fun LevelProgressCard(
    currentLevel: Int,
    currentExp: Int,
    expToNextLevel: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Level $currentLevel",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = patriot_white
                )
                Text(
                    text = "$currentExp / $expToNextLevel XP",
                    style = MaterialTheme.typography.bodyLarge,
                    color = patriot_white.copy(alpha = 0.9f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = currentExp.toFloat() / expToNextLevel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = patriot_gold,
                trackColor = patriot_white.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun MissionCard(
    mission: Mission,
    modifier: Modifier = Modifier
) {
    var showRewardAnimation by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (showRewardAnimation) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "missionCardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                enabled = mission.isCompleted && !showRewardAnimation,
                onClick = { 
                    showRewardAnimation = true
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mission.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = patriot_white
                )
                Text(
                    text = "${mission.points} XP",
                    style = MaterialTheme.typography.bodyLarge,
                    color = patriot_gold
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = mission.description,
                style = MaterialTheme.typography.bodyMedium,
                color = patriot_white.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinearProgressIndicator(
                    progress = mission.progress.toFloat() / mission.target,
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp),
                    color = if (mission.isCompleted) 
                        patriot_gold 
                    else 
                        patriot_red_bright,
                    trackColor = patriot_dark_blue.copy(alpha = 0.3f)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "${mission.progress}/${mission.target}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = patriot_white.copy(alpha = 0.9f)
                )
            }
        }
    }

    LaunchedEffect(showRewardAnimation) {
        if (showRewardAnimation) {
            delay(1000) // Reduced from 2000 to 1000 for better responsiveness
            showRewardAnimation = false
        }
    }
}