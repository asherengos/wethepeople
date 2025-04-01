package com.example.partyofthepeople.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.profile.ProfileTheme
import com.example.partyofthepeople.profile.ProfileViewModel
import com.example.partyofthepeople.profile.ProfileUiState
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCustomizationScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load profile data when screen is shown
    LaunchedEffect(userProfile) {
        viewModel.loadProfileData(userProfile)
    }
    
    // Tab selection
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Themes", "Titles", "Boost")
    
    // Success message handling
    val successMessage = uiState.successMessage
    if (successMessage != null) {
        LaunchedEffect(successMessage) {
            delay(3000)
            viewModel.clearSuccessMessage()
        }
    }
    
    // Get colors from current theme
    val primaryColor = uiState.currentTheme.primaryColor
    val secondaryColor = uiState.currentTheme.secondaryColor
    val accentColor = uiState.currentTheme.accentColor
    val backgroundColor = uiState.currentTheme.backgroundColor
    val textColor = uiState.currentTheme.textColor
    
    // Animate color changes
    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(500),
        label = "backgroundColor"
    )
    
    val animatedPrimaryColor by animateColorAsState(
        targetValue = primaryColor,
        animationSpec = tween(500),
        label = "primaryColor"
    )
    
    val animatedSecondaryColor by animateColorAsState(
        targetValue = secondaryColor,
        animationSpec = tween(500),
        label = "secondaryColor"
    )
    
    val animatedAccentColor by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(500),
        label = "accentColor"
    )
    
    val animatedTextColor by animateColorAsState(
        targetValue = textColor,
        animationSpec = tween(500),
        label = "textColor"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Customization") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = animatedPrimaryColor,
                    titleContentColor = animatedTextColor,
                    navigationIconContentColor = animatedTextColor
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(animatedBackgroundColor)
        ) {
            // Freedom Bucks indicator
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Freedom Bucks",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.freedomBucks} FB",
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Success/Error messages
            uiState.successMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            uiState.errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE63946).copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = animatedPrimaryColor,
                contentColor = animatedTextColor,
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .background(animatedAccentColor)
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) animatedAccentColor else animatedTextColor
                            )
                        }
                    )
                }
            }
            
            // Content based on selected tab
            when (selectedTab) {
                0 -> ThemesTab(
                    userProfile = userProfile,
                    viewModel = viewModel,
                    uiState = uiState
                )
                1 -> TitlesTab(
                    userProfile = userProfile,
                    viewModel = viewModel,
                    uiState = uiState
                )
                2 -> BoostTab(
                    userProfile = userProfile,
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
            
            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = accentColor)
                }
            }
        }
    }
}

@Composable
fun ThemesTab(
    userProfile: UserProfile,
    viewModel: ProfileViewModel,
    uiState: ProfileUiState
) {
    val currentThemeId = uiState.currentTheme.id
    val ownedThemeIds = uiState.ownedThemes.map { it.id }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CURRENT THEME",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Current theme display
        ThemeCard(
            theme = uiState.currentTheme,
            isOwned = true,
            isSelected = true,
            onClick = { /* Already selected */ }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "YOUR THEMES",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Owned themes
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.ownedThemes) { theme ->
                ThemeCard(
                    theme = theme,
                    isOwned = true,
                    isSelected = theme.id == currentThemeId,
                    onClick = {
                        viewModel.applyTheme(userProfile, theme.id)
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "PREMIUM THEMES",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Premium themes
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(uiState.availableThemes) { theme ->
                if (!ownedThemeIds.contains(theme.id)) {
                    PremiumThemeCard(
                        theme = theme,
                        _isOwned = ownedThemeIds.contains(theme.id),
                        onPurchase = {
                            viewModel.purchaseTheme(userProfile, theme.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: ProfileTheme,
    isOwned: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.primaryColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, theme.accentColor)
        } else {
            null
        },
        modifier = Modifier
            .height(120.dp)
            .width(160.dp)
            .clickable(enabled = isOwned, onClick = onClick)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = theme.name,
                    color = theme.textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Theme color samples
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    ColorSample(color = theme.primaryColor)
                    ColorSample(color = theme.secondaryColor)
                    ColorSample(color = theme.accentColor)
                }
                
                if (isSelected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = theme.accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active",
                            color = theme.accentColor,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            // Owned/Premium indicator
            if (!isOwned) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumThemeCard(
    theme: ProfileTheme,
    _isOwned: Boolean,
    onPurchase: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.primaryColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Theme preview
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(theme.backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(theme.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "T",
                            color = theme.textColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Color samples
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ColorSample(color = theme.primaryColor, size = 12.dp)
                        ColorSample(color = theme.secondaryColor, size = 12.dp)
                        ColorSample(color = theme.accentColor, size = 12.dp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Theme info and purchase button
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = theme.name,
                    color = theme.textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = theme.description,
                    color = theme.textColor.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Purchase button
                Button(
                    onClick = onPurchase,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = theme.accentColor,
                        contentColor = if (theme.accentColor == Color.White) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Price",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "${theme.price} FB")
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSample(color: Color, size: Dp = 24.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
    )
}

@Composable
fun TitlesTab(
    userProfile: UserProfile,
    viewModel: ProfileViewModel,
    uiState: ProfileUiState
) {
    val titles = uiState.availableTitles
    val selectedTitle = uiState.selectedTitle
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "SELECT A PREFERRED TITLE",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (titles.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1D3557).copy(alpha = 0.8f))
            ) {
                Text(
                    text = "You haven't earned any titles yet.\nKeep participating to earn titles!",
                    color = uiState.currentTheme.textColor,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(titles) { title ->
                    TitleCard(
                        title = title,
                        isSelected = title == selectedTitle,
                        theme = uiState.currentTheme,
                        onClick = {
                            viewModel.setPreferredTitle(userProfile, title)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TitleCard(
    title: String,
    isSelected: Boolean,
    theme: ProfileTheme,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1D3557).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isSelected) {
            BorderStroke(2.dp, theme.accentColor)
        } else {
            null
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                color = theme.textColor,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = theme.accentColor
                )
            }
        }
    }
}

@Composable
fun BoostTab(
    userProfile: UserProfile,
    viewModel: ProfileViewModel,
    uiState: ProfileUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PROFILE BOOST",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E7D32).copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "🚀 Profile Boost",
                    color = uiState.currentTheme.textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Stand out in the leaderboard with a special highlight for 24 hours!",
                    color = uiState.currentTheme.textColor,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (uiState.isProfileBoosted) {
                    // Boost active
                    val hours = TimeUnit.MINUTES.toHours(uiState.boostTimeRemainingMinutes.toLong())
                    val minutes = uiState.boostTimeRemainingMinutes % 60
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Boost Active!",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Time remaining: ${hours}h ${minutes}m",
                                color = uiState.currentTheme.textColor
                            )
                        }
                    }
                } else {
                    // Purchase boost
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Price: 50 Freedom Bucks",
                            color = Color(0xFFFFD700),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                viewModel.purchaseProfileBoost(userProfile)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFD700),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            enabled = uiState.freedomBucks >= 50
                        ) {
                            Text(
                                text = "Purchase Boost",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        if (uiState.freedomBucks < 50) {
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Not enough Freedom Bucks. Keep participating to earn more!",
                                color = Color(0xFFE63946),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Benefit cards
        Text(
            text = "BOOST BENEFITS",
            color = uiState.currentTheme.textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BoostBenefitCard(
                icon = "🏆",
                title = "Stand Out",
                description = "Your profile will be highlighted in leaderboards",
                theme = uiState.currentTheme,
                modifier = Modifier.weight(1f)
            )
            
            BoostBenefitCard(
                icon = "👀",
                title = "More Visibility",
                description = "Attract more attention from other patriots",
                theme = uiState.currentTheme,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BoostBenefitCard(
                icon = "⚡",
                title = "Show Dedication",
                description = "Demonstrate your commitment to the cause",
                theme = uiState.currentTheme,
                modifier = Modifier.weight(1f)
            )
            
            BoostBenefitCard(
                icon = "🔍",
                title = "Featured",
                description = "Increased chances of being discovered",
                theme = uiState.currentTheme,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun BoostBenefitCard(
    icon: String,
    title: String,
    description: String,
    theme: ProfileTheme,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = theme.primaryColor.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.height(120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                color = theme.textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                color = theme.textColor.copy(alpha = 0.7f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
} 