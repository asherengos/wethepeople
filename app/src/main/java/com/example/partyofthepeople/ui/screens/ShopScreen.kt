package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.partyofthepeople.*
import com.example.partyofthepeople.ui.theme.ComposeColors
import com.example.partyofthepeople.ui.theme.ExtendedIcons
import com.example.partyofthepeople.viewmodels.PurchaseStatus
import com.example.partyofthepeople.viewmodels.ShopViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.partyofthepeople.shop.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onBackClick: () -> Unit,
    viewModel: ShopViewModel = viewModel(),
    userProfile: UserProfile? = null
) {
    val powerItems by viewModel.powerItems.collectAsState()
    val cosmeticItems by viewModel.cosmeticItems.collectAsState()
    val weaponItems by viewModel.weaponItems.collectAsState()
    val limitedItems by viewModel.limitedItems.collectAsState()
    val purchaseStatus by viewModel.purchaseStatus.collectAsState()
    
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Power", "Cosmetic", "Weapons", "Limited")
    
    // Handle purchase status
    LaunchedEffect(purchaseStatus) {
        if (purchaseStatus is PurchaseStatus.Success) {
            // Auto-clear success status after a delay
            delay(3000)
            viewModel.clearPurchaseStatus()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Loot Democracy",
                        color = ComposeColors.NeonWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = ComposeColors.NeonWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ComposeColors.PatriotBlue
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF1D3557))
        ) {
            // User balance header
            ShopHeader(userProfile = userProfile)
            
            // Tab row
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = ComposeColors.PatriotBlue,
                contentColor = ComposeColors.NeonWhite
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title,
                                color = if (selectedTabIndex == index) 
                                    ComposeColors.NeonWhite 
                                else 
                                    ComposeColors.NeonWhite.copy(alpha = 0.7f)
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Star
                                    1 -> Icons.Default.Face
                                    2 -> ExtendedIcons.Build
                                    else -> ExtendedIcons.Timer
                                },
                                contentDescription = title,
                                tint = if (selectedTabIndex == index) 
                                    ComposeColors.NeonWhite 
                                else 
                                    ComposeColors.NeonWhite.copy(alpha = 0.7f)
                            )
                        }
                    )
                }
            }
            
            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> ItemGrid(
                    items = powerItems,
                    onItemClick = { viewModel.purchaseItem(it.id) }
                )
                1 -> ItemGrid(
                    items = cosmeticItems,
                    onItemClick = { viewModel.purchaseItem(it.id) }
                )
                2 -> ItemGrid(
                    items = weaponItems,
                    onItemClick = { viewModel.purchaseItem(it.id) }
                )
                3 -> LimitedItemsList(
                    items = limitedItems,
                    onItemClick = { viewModel.purchaseItem(it.id) }
                )
            }
            
            // Purchase status
            purchaseStatus?.let { status ->
                when (status) {
                    is PurchaseStatus.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = ComposeColors.NeonWhite)
                        }
                    }
                    is PurchaseStatus.Success -> {
                        PurchaseSuccessBar(message = status.message)
                    }
                    is PurchaseStatus.Error -> {
                        PurchaseErrorBar(
                            message = status.message,
                            onDismiss = { viewModel.clearPurchaseStatus() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopHeader(userProfile: UserProfile?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D2545))
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome, ${userProfile?.username ?: "Patriot"}!",
            color = ComposeColors.NeonWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Patriot Points (premium currency)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color(0xFFB71C1C),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Patriot Points",
                    tint = Color.Yellow,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${userProfile?.patriotPoints ?: 0} PP",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Patriot Points",
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .clickable { /* TODO: Add PP purchase flow */ }
                )
            }
            
            // Freedom Bucks (free currency)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = Color(0xFF1565C0),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = ExtendedIcons.AttachMoney,
                    contentDescription = "Freedom Bucks",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${userProfile?.freedomBucks ?: 0} FB",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // User rank or special discount (satirical message)
        Text(
            text = "Your 'Tax Evader' rank grants you 10% off bribes!",
            color = Color.Yellow,
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ItemGrid(
    items: List<ShopItem>,
    onItemClick: (ShopItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ShopItemCard(
                item = item,
                onPurchase = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun LimitedItemsList(
    items: List<ShopItem>,
    onItemClick: (ShopItem) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            LimitedShopItemCard(
                item = item,
                onPurchase = { onItemClick(item) }
            )
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onPurchase),
        colors = CardDefaults.cardColors(
            containerColor = when (item.category) {
                ShopItemCategory.POWER -> Color(0xFF512DA8).copy(alpha = 0.8f)
                ShopItemCategory.COSMETIC -> Color(0xFF00796B).copy(alpha = 0.8f)
                ShopItemCategory.WEAPON -> Color(0xFFC62828).copy(alpha = 0.8f)
                ShopItemCategory.LIMITED -> Color(0xFFFF6F00).copy(alpha = 0.8f)
                ShopItemCategory.BOOST -> Color(0xFF2E7D32).copy(alpha = 0.8f)
                ShopItemCategory.TITLE -> Color(0xFF1565C0).copy(alpha = 0.8f)
                ShopItemCategory.CONSUMABLE -> Color(0xFF6A1B9A).copy(alpha = 0.8f)
                ShopItemCategory.GENERAL -> Color(0xFF455A64).copy(alpha = 0.8f)
                ShopItemCategory.PROFILE -> Color(0xFF00796B).copy(alpha = 0.8f)
                ShopItemCategory.POWER_UPS -> Color(0xFF512DA8).copy(alpha = 0.8f)
                ShopItemCategory.CONSUMABLES -> Color(0xFF6A1B9A).copy(alpha = 0.8f)
                ShopItemCategory.SPECIAL -> Color(0xFFFF6F00).copy(alpha = 0.8f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Item icon placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.effectType) {
                        ShopItemEffect.NONE -> Icons.Default.Block
                        ShopItemEffect.SUPER_VOTE -> ExtendedIcons.HowToVote
                        ShopItemEffect.IMPEACHMENT_SHIELD -> ExtendedIcons.Shield
                        ShopItemEffect.PROFILE_THEME -> ExtendedIcons.Palette
                        ShopItemEffect.PROFILE_BADGE -> ExtendedIcons.EmojiEvents
                        ShopItemEffect.PROFILE_TITLE -> ExtendedIcons.Title
                        ShopItemEffect.AUDIT_USER -> ExtendedIcons.AttachMoney
                        ShopItemEffect.DEBATE_BOMB -> ExtendedIcons.Forum
                        ShopItemEffect.TROLL_FARM -> ExtendedIcons.People
                        ShopItemEffect.MEDIA_BLACKOUT -> ExtendedIcons.Block
                        ShopItemEffect.EXECUTIVE_ORDER -> Icons.Default.Create
                        ShopItemEffect.FILIBUSTER_PASS -> ExtendedIcons.Mic
                        ShopItemEffect.BUNDLE -> ExtendedIcons.Redeem
                        ShopItemEffect.BOOST_VOTES -> ExtendedIcons.HowToVote
                        ShopItemEffect.BOOST_INFLUENCE -> ExtendedIcons.TrendingUp
                        ShopItemEffect.BOOST_VISIBILITY -> ExtendedIcons.Visibility
                        ShopItemEffect.BOOST_EARNINGS -> ExtendedIcons.AttachMoney
                        ShopItemEffect.BOOST_EXPERIENCE -> ExtendedIcons.Star
                        ShopItemEffect.BOOST_REPUTATION -> ExtendedIcons.ThumbUp
                        ShopItemEffect.UNLOCK_TITLE -> ExtendedIcons.Title
                        ShopItemEffect.UNLOCK_THEME -> ExtendedIcons.Palette
                        ShopItemEffect.UNLOCK_BADGE -> ExtendedIcons.EmojiEvents
                        ShopItemEffect.UNLOCK_EMOTE -> ExtendedIcons.EmojiEmotions
                        ShopItemEffect.UNLOCK_FRAME -> ExtendedIcons.CropOriginal
                        ShopItemEffect.UNLOCK_BACKGROUND -> ExtendedIcons.Wallpaper
                    },
                    contentDescription = item.name,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Item name
            Text(
                text = item.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Item description
            Text(
                text = item.description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.heightIn(min = 36.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Price
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = when (item.currency) {
                            CurrencyType.PATRIOT_POINTS -> Color(0xFFB71C1C)
                            CurrencyType.FREEDOM_BUCKS -> Color(0xFF1565C0)
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = when (item.currency) {
                        CurrencyType.PATRIOT_POINTS -> Icons.Default.Star
                        CurrencyType.FREEDOM_BUCKS -> ExtendedIcons.AttachMoney
                    },
                    contentDescription = "Currency",
                    tint = when (item.currency) {
                        CurrencyType.PATRIOT_POINTS -> Color.Yellow
                        CurrencyType.FREEDOM_BUCKS -> Color.White
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${item.price} ${
                        when (item.currency) {
                            CurrencyType.PATRIOT_POINTS -> "PP"
                            CurrencyType.FREEDOM_BUCKS -> "FB"
                        }
                    }",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            // Discount if applicable
            if (item.discountPercentage > 0) {
                Text(
                    text = "${item.discountPercentage}% OFF!",
                    color = Color.Yellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Out of stock warning
            if (item.stockRemaining == 0) {
                Text(
                    text = "OUT OF STOCK",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else if (item.stockRemaining in 1..5) {
                Text(
                    text = "Only ${item.stockRemaining} left!",
                    color = Color.Yellow,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LimitedShopItemCard(
    item: ShopItem,
    onPurchase: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onPurchase),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6F00).copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Item icon placeholder
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.effectType) {
                        ShopItemEffect.NONE -> Icons.Default.Block
                        ShopItemEffect.SUPER_VOTE -> ExtendedIcons.HowToVote
                        ShopItemEffect.IMPEACHMENT_SHIELD -> ExtendedIcons.Shield
                        ShopItemEffect.PROFILE_THEME -> ExtendedIcons.Palette
                        ShopItemEffect.PROFILE_BADGE -> ExtendedIcons.EmojiEvents
                        ShopItemEffect.PROFILE_TITLE -> ExtendedIcons.Title
                        ShopItemEffect.AUDIT_USER -> ExtendedIcons.AttachMoney
                        ShopItemEffect.DEBATE_BOMB -> ExtendedIcons.Forum
                        ShopItemEffect.TROLL_FARM -> ExtendedIcons.People
                        ShopItemEffect.MEDIA_BLACKOUT -> ExtendedIcons.Block
                        ShopItemEffect.EXECUTIVE_ORDER -> Icons.Default.Create
                        ShopItemEffect.FILIBUSTER_PASS -> ExtendedIcons.Mic
                        ShopItemEffect.BUNDLE -> ExtendedIcons.Redeem
                        ShopItemEffect.BOOST_VOTES -> ExtendedIcons.HowToVote
                        ShopItemEffect.BOOST_INFLUENCE -> ExtendedIcons.TrendingUp
                        ShopItemEffect.BOOST_VISIBILITY -> ExtendedIcons.Visibility
                        ShopItemEffect.BOOST_EARNINGS -> ExtendedIcons.AttachMoney
                        ShopItemEffect.BOOST_EXPERIENCE -> ExtendedIcons.Star
                        ShopItemEffect.BOOST_REPUTATION -> ExtendedIcons.ThumbUp
                        ShopItemEffect.UNLOCK_TITLE -> ExtendedIcons.Title
                        ShopItemEffect.UNLOCK_THEME -> ExtendedIcons.Palette
                        ShopItemEffect.UNLOCK_BADGE -> ExtendedIcons.EmojiEvents
                        ShopItemEffect.UNLOCK_EMOTE -> ExtendedIcons.EmojiEmotions
                        ShopItemEffect.UNLOCK_FRAME -> ExtendedIcons.CropOriginal
                        ShopItemEffect.UNLOCK_BACKGROUND -> ExtendedIcons.Wallpaper
                    },
                    contentDescription = item.name,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Time remaining indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.Red.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = ExtendedIcons.Timer,
                        contentDescription = "Limited Time",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Calculate time remaining
                    val timeRemaining = if (item.limitedTimeUntil != null) {
                        val timeLeft = item.limitedTimeUntil - System.currentTimeMillis()
                        if (timeLeft > 0) {
                            val hours = timeLeft / (1000 * 60 * 60)
                            val minutes = (timeLeft % (1000 * 60 * 60)) / (1000 * 60)
                            "${hours}h ${minutes}m left"
                        } else {
                            "Expired"
                        }
                    } else {
                        "Limited Time"
                    }
                    Text(
                        text = timeRemaining,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Item name
                Text(
                    text = item.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Item description
                Text(
                    text = item.description,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Price
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                color = when (item.currency) {
                                    CurrencyType.PATRIOT_POINTS -> Color(0xFFB71C1C)
                                    CurrencyType.FREEDOM_BUCKS -> Color(0xFF1565C0)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = when (item.currency) {
                                CurrencyType.PATRIOT_POINTS -> Icons.Default.Star
                                CurrencyType.FREEDOM_BUCKS -> ExtendedIcons.AttachMoney
                            },
                            contentDescription = "Currency",
                            tint = when (item.currency) {
                                CurrencyType.PATRIOT_POINTS -> Color.Yellow
                                CurrencyType.FREEDOM_BUCKS -> Color.White
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.price} ${
                                when (item.currency) {
                                    CurrencyType.PATRIOT_POINTS -> "PP"
                                    CurrencyType.FREEDOM_BUCKS -> "FB"
                                }
                            }",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Stock remaining
                    if (item.stockRemaining in 1..10) {
                        Text(
                            text = "Only ${item.stockRemaining} left!",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PurchaseSuccessBar(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF388E3C))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PurchaseErrorBar(
    message: String,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD32F2F))
            .padding(16.dp)
    ) {
        Text(
            text = message,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        
        IconButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = Color.White
            )
        }
    }
} 