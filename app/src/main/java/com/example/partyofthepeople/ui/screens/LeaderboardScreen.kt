package com.example.partyofthepeople.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.partyofthepeople.leaderboard.LeaderboardEntry
import com.example.partyofthepeople.leaderboard.LeaderboardManager
import com.example.partyofthepeople.leaderboard.LeaderboardViewModel
import androidx.compose.material3.Divider

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LeaderboardScreen(
    onBackClick: () -> Unit,
    onInfoClick: () -> Unit,
    viewModel: LeaderboardViewModel = viewModel()
) {
    val leaderboardEntries by viewModel.leaderboardEntries.collectAsStateWithLifecycle()
    val userRank by viewModel.userRank.collectAsStateWithLifecycle()
    val districtLeaderboard by viewModel.districtLeaderboard.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val selectedRankingType by viewModel.selectedRankingType.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    
    var showYourDistrict by remember { mutableStateOf(false) }
    
    // Load leaderboard data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.loadLeaderboard()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557))
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("Leaderboard") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                // Add info icon to navigate to LeaderboardInfoScreen
                IconButton(onClick = onInfoClick) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Leaderboard Info",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF0A1929),
                titleContentColor = Color.White
            )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Ranking Type Selector
            Text(
                text = "RANKING CATEGORY",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LeaderboardManager.RankingType.values()) { rankingType ->
                    RankingTypeChip(
                        rankingType = rankingType,
                        selected = rankingType == selectedRankingType,
                        viewModel = viewModel,
                        onClick = {
                            viewModel.setRankingType(rankingType)
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User's Current Rank
            userRank?.let { rank ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF457B9D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "YOUR CURRENT RANKING",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        UserRankRow(
                            entry = rank,
                            _rankingType = selectedRankingType,
                            isCurrentUser = true,
                            _viewModel = viewModel
                        )
                        
                        if (rank.district.isNotEmpty() && rank.district != "Unknown") {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedButton(
                                onClick = {
                                    showYourDistrict = !showYourDistrict
                                    if (showYourDistrict) {
                                        viewModel.loadDistrictLeaderboard(rank.district)
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    text = if (showYourDistrict) "Hide District" else "Show Your District",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // District Leaderboard (if selected)
            AnimatedVisibility(
                visible = showYourDistrict && selectedDistrict != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                selectedDistrict?.let { district ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A4A73)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "DISTRICT: $district",
                                color = Color(0xFFFFD700),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (districtLeaderboard.isEmpty()) {
                                if (isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color.White)
                                    }
                                } else {
                                    Text(
                                        text = "No patriots found in your district yet.",
                                        color = Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 300.dp)
                                ) {
                                    items(districtLeaderboard) { entry ->
                                        UserRankRow(
                                            entry = entry,
                                            _rankingType = LeaderboardManager.RankingType.POWER_SCORE,
                                            isCurrentUser = entry.userId == userRank?.userId,
                                            _viewModel = viewModel,
                                            modifier = Modifier.animateItemPlacement()
                                        )
                                        
                                        Divider(
                                            color = Color.White.copy(alpha = 0.2f),
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            
            // Global Leaderboard
            Text(
                text = "GLOBAL LEADERBOARD",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Leaderboard header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rank",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.width(40.dp)
                )
                
                Text(
                    text = "Patriot",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = viewModel.getRankingValueLabel(selectedRankingType),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.width(60.dp)
                )
            }
            
            if (isLoading && leaderboardEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (leaderboardEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No leaderboard data available.\nBe the first to earn some points!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(leaderboardEntries) { entry ->
                        UserRankRow(
                            entry = entry,
                            _rankingType = selectedRankingType,
                            isCurrentUser = entry.userId == userRank?.userId,
                            _viewModel = viewModel,
                            modifier = Modifier.animateItemPlacement()
                        )
                        
                        Divider(
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RankingTypeChip(
    rankingType: LeaderboardManager.RankingType,
    selected: Boolean,
    viewModel: LeaderboardViewModel,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFF457B9D) else Color(0xFF2A4A73)
    val borderColor = if (selected) Color(0xFFFFD700) else Color.Transparent
    
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = viewModel.getRankingTypeIcon(rankingType),
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = viewModel.getRankingTypeDisplayName(rankingType),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun UserRankRow(
    entry: LeaderboardEntry,
    _rankingType: LeaderboardManager.RankingType,
    isCurrentUser: Boolean,
    _viewModel: LeaderboardViewModel,
    modifier: Modifier = Modifier
) {
    val rowAlpha by animateFloatAsState(targetValue = if (isCurrentUser) 1f else 0.9f, label = "RowAlpha")
    
    // Determine background based on boosted status and current user
    val backgroundColor = when {
        entry.isBoosted -> Color(0xFF184E77).copy(alpha = 0.7f) // Boosted profile background
        isCurrentUser -> Color(0xFF2c5f7c)                      // Current user background
        else -> Color.Transparent                               // Default background
    }
    
    // Create a border for boosted profiles
    val borderModifier = if (entry.isBoosted) {
        Modifier.border(
            width = 1.dp,
            color = Color(0xFFFFD700).copy(alpha = 0.6f),
            shape = RoundedCornerShape(4.dp)
        )
    } else {
        Modifier
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(borderModifier)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .alpha(rowAlpha)
    ) {
        // Rank
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(40.dp)
        ) {
            if (entry.rank <= 3) {
                val medalColor = when (entry.rank) {
                    1 -> Color(0xFFFFD700) // Gold
                    2 -> Color(0xFFC0C0C0) // Silver
                    3 -> Color(0xFFCD7F32) // Bronze
                    else -> Color.Transparent
                }
                
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(medalColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${entry.rank}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Text(
                    text = "#${entry.rank}",
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
        
        // User info
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.username,
                    color = Color.White,
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                
                // Show "Boosted" indicator for boosted profiles
                if (entry.isBoosted) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFFFD700).copy(alpha = 0.4f),
                                shape = RoundedCornerShape(2.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "BOOSTED",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            if (entry.district.isNotEmpty() && entry.district != "Unknown") {
                Text(
                    text = "District: ${entry.district}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }
        
        // Achievement count
        if (entry.achievementCount > 0) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆",
                    fontSize = 14.sp
                )
                
                Text(
                    text = "${entry.achievementCount}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(y = 2.dp)
                )
            }
        }
        
        // Score value
        Text(
            text = "${entry.score}",
            color = if (isCurrentUser) Color(0xFFFFD700) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.width(50.dp)
        )
    }
} 