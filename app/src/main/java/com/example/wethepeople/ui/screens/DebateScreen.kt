package com.example.wethepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.wethepeople.ui.theme.*
import androidx.compose.foundation.clickable
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.main.BottomNavItem
import com.example.wethepeople.ui.icons.CustomIcons
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateScreen(
    navController: NavController
) {
    val filters = listOf("All Topics", "Hot Topics", "Economy", "Foreign Policy", "Healthcare", "Civil Rights")
    var selectedFilter by remember { mutableStateOf("Hot Topics") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BATTLEFIELD OF IDEAS",
                            fontWeight = FontWeight.Bold,
                            color = patriot_white
                        )
                        Text(
                            text = "ONE VOTE. ONE MILLION VOICES",
                            fontSize = 12.sp,
                            color = patriot_white.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = CustomIcons.ArrowBack,
                            contentDescription = "Back",
                            tint = patriot_white
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = patriot_dark_blue,
                    titleContentColor = patriot_white
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.shadow(8.dp)
            ) {
                BottomAppBar(
                    containerColor = patriot_dark_blue,
                    contentColor = patriot_white,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Profile,
                        BottomNavItem.PatriotMissions,
                        BottomNavItem.Shop
                    )
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { 
                                Icon(
                                    imageVector = item.icon, 
                                    contentDescription = item.label,
                                    tint = patriot_white,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                ) 
                            },
                            label = { 
                                Text(
                                    text = item.label,
                                    color = if (isSelected) 
                                        patriot_white else patriot_white.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                ) 
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = patriot_white,
                                unselectedIconColor = patriot_white.copy(alpha = 0.6f),
                                indicatorColor = patriot_medium_blue
                            )
                        )
                    }
                }
            }
        },
        containerColor = patriot_dark_blue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filters
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(patriot_dark_blue)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column {
                    Text(
                        text = "FILTER BATTLEFIELDS",
                        color = patriot_white,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filters) { filter ->
                            val isSelected = filter == selectedFilter
                            FilterChip(
                                filter = filter,
                                isSelected = isSelected,
                                onClick = { selectedFilter = filter }
                            )
                        }
                    }
                }
            }
            
            // Combined scrollable list with both sections
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // HOT TOPICS header
                item {
                    Text(
                        text = "HOT TOPICS",
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Hot Topics debates
                item {
                    DebateCard(
                        title = "Tax Evasion: Treason or Super-Treason?",
                        description = "Join the heated discussion on tax avoidance and whether it should be classified as a capital offense. DO YOUR DUTY!",
                        viewCount = 1203,
                        commentCount = 128,
                        timeAgo = "2 hours ago",
                        isHot = true,
                        tags = listOf("Economy", "Taxation", "Liberty"),
                        onClick = {
                            val encodedTitle = URLEncoder.encode("Tax Evasion: Treason or Super-Treason?", "UTF-8")
                            val encodedDescription = URLEncoder.encode("Join the heated discussion on tax avoidance and whether it should be classified as a capital offense. DO YOUR DUTY!", "UTF-8")
                            navController.navigate("debate_detail/$encodedTitle/$encodedDescription")
                        }
                    )
                }
                
                item {
                    DebateCard(
                        title = "Should Voting Be MANDATORY for All Citizens?",
                        description = "Freedom isn't free, citizen! Should all Americans be required to vote or face consequences?",
                        viewCount = 876,
                        commentCount = 94,
                        timeAgo = "5 hours ago",
                        isHot = true,
                        tags = listOf("Elections", "Liberty", "Civic Duty"),
                        onClick = {
                            val encodedTitle = URLEncoder.encode("Should Voting Be MANDATORY for All Citizens?", "UTF-8")
                            val encodedDescription = URLEncoder.encode("Freedom isn't free, citizen! Should all Americans be required to vote or face consequences?", "UTF-8")
                            navController.navigate("debate_detail/$encodedTitle/$encodedDescription")
                        }
                    )
                }
                
                // ALL CURRENT LEGISLATION header
                item {
                    Text(
                        text = "ALL CURRENT LEGISLATION",
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )
                }
                
                // Legislation debates
                item {
                    DebateCard(
                        title = "Debate: Education Reform - More History or More Science?",
                        description = "What should our schools prioritize: teaching American history or STEM subjects?",
                        viewCount = 562,
                        commentCount = 73,
                        timeAgo = "9 hours ago",
                        isHot = false,
                        tags = listOf("Education", "Curriculum", "STEM"),
                        onClick = {
                            val encodedTitle = URLEncoder.encode("Debate: Education Reform - More History or More Science?", "UTF-8")
                            val encodedDescription = URLEncoder.encode("What should our schools prioritize: teaching American history or STEM subjects?", "UTF-8")
                            navController.navigate("debate_detail/$encodedTitle/$encodedDescription")
                        }
                    )
                }
                
                item {
                    DebateCard(
                        title = "Federal Reserve: Necessary Evil or ENEMY OF THE PEOPLE?",
                        description = "Is the Federal Reserve a crucial institution or a corrupt banking cartel? Let your voice be heard!",
                        viewCount = 743,
                        commentCount = 86,
                        timeAgo = "7 hours ago",
                        isHot = false,
                        tags = listOf("Economy", "Finance", "Conspiracy"),
                        onClick = {
                            val encodedTitle = URLEncoder.encode("Federal Reserve: Necessary Evil or ENEMY OF THE PEOPLE?", "UTF-8")
                            val encodedDescription = URLEncoder.encode("Is the Federal Reserve a crucial institution or a corrupt banking cartel? Let your voice be heard!", "UTF-8")
                            navController.navigate("debate_detail/$encodedTitle/$encodedDescription")
                        }
                    )
                }
                
                item {
                    DebateCard(
                        title = "Pineapple on Pizza: Civil Right or War Crime?",
                        description = "The most divisive issue of our time. Where do YOU stand, citizen?",
                        viewCount = 1547,
                        commentCount = 342,
                        timeAgo = "1 day ago",
                        isHot = false,
                        tags = listOf("Culture", "Food", "Civil War"),
                        onClick = {
                            val encodedTitle = URLEncoder.encode("Pineapple on Pizza: Civil Right or War Crime?", "UTF-8")
                            val encodedDescription = URLEncoder.encode("The most divisive issue of our time. Where do YOU stand, citizen?", "UTF-8")
                            navController.navigate("debate_detail/$encodedTitle/$encodedDescription")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    filter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        when (filter) {
            "Hot Topics" -> patriot_red_bright
            "Economy" -> Color(0xFF1A6B3D) // Green
            "Foreign Policy" -> Color(0xFF3F51B5) // Blue
            else -> patriot_medium_blue
        }
    } else {
        Color.Transparent
    }
    
    val textColor = if (isSelected) patriot_white else patriot_white.copy(alpha = 0.7f)
    val borderColor = if (isSelected) Color.Transparent else patriot_white.copy(alpha = 0.3f)
    
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        color = backgroundColor
    ) {
        Text(
            text = filter,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun DebateCard(
    title: String,
    description: String,
    viewCount: Int,
    commentCount: Int,
    timeAgo: String,
    isHot: Boolean,
    tags: List<String>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with icon and title
            Row(
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
                
                // Title and hot tag
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = patriot_white,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        
                        if (isHot) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = patriot_red_bright,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    text = "HOT",
                                    color = patriot_white,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Description
            Text(
                text = description,
                color = patriot_white.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
            )
            
            // Tags
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(tags) { tag ->
                    val tagColor = when (tag) {
                        "Economy" -> Color(0xFF1A6B3D) // Green
                        "Taxation" -> Color(0xFF795548) // Brown
                        "Liberty" -> Color(0xFFE65100) // Orange
                        "Elections" -> Color(0xFF3F51B5) // Blue
                        "Civic Duty" -> Color(0xFF9C27B0) // Purple
                        "Finance" -> Color(0xFF00796B) // Teal
                        "Conspiracy" -> Color(0xFF616161) // Gray
                        "Food" -> Color(0xFFEF6C00) // Orange
                        "Civil War" -> Color(0xFFD32F2F) // Red
                        "Culture" -> Color(0xFF8E24AA) // Purple
                        else -> patriot_medium_blue
                    }
                    
                    Surface(
                        color = tagColor.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = tag,
                            color = patriot_white,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "$viewCount views",
                        color = patriot_white.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$commentCount comments",
                        color = patriot_white.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = timeAgo,
                        color = patriot_white.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
            
            // Join debate button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = patriot_dark_blue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "JOIN THE DEBATE!",
                    fontWeight = FontWeight.Bold,
                    color = patriot_white
                )
            }
        }
    }
} 