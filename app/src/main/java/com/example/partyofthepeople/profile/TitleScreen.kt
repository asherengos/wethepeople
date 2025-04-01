package com.example.partyofthepeople.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.profile.TitleManager.TitleCategory
import kotlinx.coroutines.delay

enum class TitleTab {
    EARNED, ALL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleScreen(
    navController: NavController,
    userProfile: UserProfile,
    titleViewModel: TitleViewModel = viewModel(),
    onBackClick: (() -> Unit)? = null
) {
    val uiState by titleViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(TitleTab.EARNED) }
    var showTitleDetailsDialog by remember { mutableStateOf<TitleManager.Title?>(null) }

    // Load titles when the screen is first displayed
    LaunchedEffect(key1 = userProfile.userId) {
        titleViewModel.loadUserTitles(userProfile)
    }

    // Show success/error messages
    LaunchedEffect(key1 = uiState.successMessage, key2 = uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            titleViewModel.clearSuccessMessage()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            titleViewModel.clearErrorMessage()
        }
    }

    // Show notification for newly earned title
    LaunchedEffect(key1 = uiState.newlyEarnedTitle) {
        uiState.newlyEarnedTitle?.let {
            showTitleDetailsDialog = it
            // Clear notification after showing dialog
            delay(100) // Small delay to ensure dialog shows first
            titleViewModel.clearNewTitleNotification()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Titles") },
                navigationIcon = {
                    IconButton(onClick = { onBackClick?.invoke() ?: navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Current selected title
                uiState.selectedTitle?.let { selectedTitle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "Current Title",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    selectedTitle.iconEmoji,
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    selectedTitle.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                selectedTitle.description,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } ?: run {
                    // No title selected
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "No Title Selected",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Select a title below to display it on your profile.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Showcased title (if any)
                uiState.showcasedTitle?.let { showcasedTitle ->
                    if (uiState.showcaseTimeRemainingHours > 0) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Filled.Star, contentDescription = "Showcased")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Showcased Title",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    "${showcasedTitle.iconEmoji} ${showcasedTitle.displayName}",
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    "Time remaining: ${uiState.showcaseTimeRemainingHours} hours",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // Tab row for switching between earned and all titles
                TabRow(
                    selectedTabIndex = selectedTab.ordinal
                ) {
                    TitleTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    when (tab) {
                                        TitleTab.EARNED -> "My Titles (${uiState.earnedTitles.size})"
                                        TitleTab.ALL -> "All Titles (${uiState.availableTitles.size})"
                                    }
                                )
                            }
                        )
                    }
                }

                when (selectedTab) {
                    TitleTab.EARNED -> {
                        if (uiState.earnedTitles.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "You haven't earned any titles yet. Complete actions in the app to earn titles!",
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // Categorized display of earned titles
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                val categorized = uiState.earnedTitles.groupBy { it.category }
                                
                                for ((category, titles) in categorized) {
                                    item {
                                        Text(
                                            getCategoryDisplayName(category),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                        
                                        Divider(
                                            color = Color(0xFF457B9D).copy(alpha = 0.3f)
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    
                                    items(titles) { title ->
                                        TitleItemCard(
                                            title = title,
                                            isEarned = true,
                                            isSelected = uiState.selectedTitle?.id == title.id,
                                            isShowcased = uiState.showcasedTitle?.id == title.id,
                                            onTitleClick = { showTitleDetailsDialog = title },
                                            onSetAsPrimaryClick = {
                                                titleViewModel.setPreferredTitle(userProfile, title.id)
                                            },
                                            onShowcaseClick = {
                                                titleViewModel.purchaseTitleShowcase(userProfile, title.id)
                                            },
                                            showcasePrice = 50,
                                            userFreedomBucks = uiState.freedomBucks
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                    TitleTab.ALL -> {
                        // All titles display, grouped by category
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            val categorized = uiState.availableTitles.groupBy { it.category }
                            
                            for ((category, titles) in categorized) {
                                item {
                                    Text(
                                        getCategoryDisplayName(category),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                    
                                    Divider(
                                        color = Color(0xFF457B9D).copy(alpha = 0.3f)
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                
                                items(titles) { title ->
                                    val isEarned = titleViewModel.isTitleEarned(title.id)
                                    
                                    TitleItemCard(
                                        title = title,
                                        isEarned = isEarned,
                                        isSelected = uiState.selectedTitle?.id == title.id,
                                        isShowcased = uiState.showcasedTitle?.id == title.id,
                                        onTitleClick = { showTitleDetailsDialog = title },
                                        onSetAsPrimaryClick = {
                                            if (isEarned) {
                                                titleViewModel.setPreferredTitle(userProfile, title.id)
                                            }
                                        },
                                        onShowcaseClick = {
                                            if (isEarned) {
                                                titleViewModel.purchaseTitleShowcase(userProfile, title.id)
                                            }
                                        },
                                        showcasePrice = 50,
                                        userFreedomBucks = uiState.freedomBucks
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Title details dialog
    showTitleDetailsDialog?.let { title ->
        TitleDetailsDialog(
            title = title,
            isEarned = titleViewModel.isTitleEarned(title.id),
            isSelected = uiState.selectedTitle?.id == title.id,
            isShowcased = uiState.showcasedTitle?.id == title.id,
            onDismiss = { showTitleDetailsDialog = null },
            onSetAsPrimary = {
                if (titleViewModel.isTitleEarned(title.id)) {
                    titleViewModel.setPreferredTitle(userProfile, title.id)
                }
            },
            onShowcase = {
                if (titleViewModel.isTitleEarned(title.id)) {
                    titleViewModel.purchaseTitleShowcase(userProfile, title.id)
                }
            },
            showcasePrice = 50,
            userFreedomBucks = uiState.freedomBucks
        )
    }
}

@Composable
fun TitleItemCard(
    title: TitleManager.Title,
    isEarned: Boolean,
    isSelected: Boolean,
    isShowcased: Boolean,
    onTitleClick: () -> Unit,
    onSetAsPrimaryClick: () -> Unit,
    onShowcaseClick: () -> Unit,
    showcasePrice: Int,
    userFreedomBucks: Int
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isShowcased -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        isShowcased -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTitleClick() },
        border = BorderStroke(
            width = if (isSelected || isShowcased) 2.dp else 1.dp,
            color = borderColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title emoji
            Text(
                text = title.iconEmoji,
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            
            // Title info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title.displayName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "(Current)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    if (isShowcased) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "Showcased",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Text(
                    text = title.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getCategoryDisplayName(title.category),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = getRarityText(title.rarity),
                        style = MaterialTheme.typography.bodySmall,
                        color = getRarityColor(title.rarity)
                    )
                }
            }
            
            // Status indicator
            if (isEarned) {
                Row {
                    if (!isSelected) {
                        OutlinedButton(
                            onClick = onSetAsPrimaryClick,
                            modifier = Modifier.padding(end = 4.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Set Primary", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Locked",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TitleDetailsDialog(
    title: TitleManager.Title,
    isEarned: Boolean,
    isSelected: Boolean,
    isShowcased: Boolean,
    onDismiss: () -> Unit,
    onSetAsPrimary: () -> Unit,
    onShowcase: () -> Unit,
    showcasePrice: Int,
    userFreedomBucks: Int
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    title.iconEmoji,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    title.displayName,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column {
                Text(title.description)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Category: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(getCategoryDisplayName(title.category))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Rarity: ",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        getRarityText(title.rarity),
                        color = getRarityColor(title.rarity)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isEarned) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Earned",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "You have earned this title!",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Text(
                        "You haven't earned this title yet. Complete actions in the app to earn it!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        },
        confirmButton = {
            Column {
                if (isEarned) {
                    if (!isSelected) {
                        Button(
                            onClick = {
                                onSetAsPrimary()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Set as Primary Title")
                        }
                    }
                    
                    if (!isShowcased && userFreedomBucks >= showcasePrice) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Button(
                            onClick = {
                                onShowcase()
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text("Showcase for 7 days (${showcasePrice}FB)")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Close")
                }
            }
        }
    )
}

@Composable
fun getCategoryDisplayName(category: TitleCategory): String {
    return when (category) {
        TitleCategory.PARTICIPATION -> "Participation"
        TitleCategory.ACHIEVEMENT -> "Achievement"
        TitleCategory.COMMUNITY -> "Community"
        TitleCategory.PREMIUM -> "Premium"
        TitleCategory.HUMOROUS -> "Humorous"
    }
}

@Composable
fun getRarityText(rarity: TitleManager.TitleRarity): String {
    return when (rarity) {
        TitleManager.TitleRarity.COMMON -> "Common"
        TitleManager.TitleRarity.UNCOMMON -> "Uncommon"
        TitleManager.TitleRarity.RARE -> "Rare"
        TitleManager.TitleRarity.EPIC -> "Epic"
        TitleManager.TitleRarity.LEGENDARY -> "Legendary"
    }
}

@Composable
fun getRarityColor(rarity: TitleManager.TitleRarity): Color {
    return when (rarity) {
        TitleManager.TitleRarity.COMMON -> Color.Gray
        TitleManager.TitleRarity.UNCOMMON -> Color.Green
        TitleManager.TitleRarity.RARE -> Color.Blue
        TitleManager.TitleRarity.EPIC -> Color(0xFF9C27B0) // Purple
        TitleManager.TitleRarity.LEGENDARY -> Color(0xFFFFD700) // Gold
    }
} 