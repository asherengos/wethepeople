package com.example.wethepeople.ui.screens.shop

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wethepeople.data.model.ShopItem
import com.example.wethepeople.data.model.ShopItemCategory
import com.example.wethepeople.ui.viewmodel.ShopViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.patriot_white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ShopViewModel = viewModel(factory = ShopViewModel.Factory(context))
    var currentCoins by remember { mutableStateOf(100) }
    
    // Track current tab selection
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Unlocked Items", "Premium Shop")
    
    // Mock unlocked items based on level (in a real app, this would come from the user's actual level)
    val userLevel = 15
    val unlockedItems = listOf(
        CustomizationItem("Patriot Cap", "🧢", "A classic red cap for your Eagley", "Unlocked at Level 5", true),
        CustomizationItem("Freedom Shades", "🕶️", "Cool shades for a cool Eagley", "Unlocked at Level 10", true),
        CustomizationItem("Star-Spangled Vest", "🎽", "Patriotic vest with stars and stripes", "Unlocked at Level 15", true),
        CustomizationItem("Veteran Medal", "🎖️", "Honor your Eagley's service", "Unlocked at Level 20", false),
        CustomizationItem("Uncle Sam Hat", "🎩", "The iconic stars and stripes hat", "Unlocked at Level 25", false)
    )
    
    // Premium items that can be purchased with Freedom Coins
    val premiumItems = listOf(
        PremiumItem("Golden Talon", "💅", "Gold-plated talons for your Eagley", 300),
        PremiumItem("Declaration Scroll", "📜", "Historical document accessory", 500),
        PremiumItem("Liberty Torch", "🔦", "Light the way to freedom", 750),
        PremiumItem("Patriot Aura", "✨", "Sparkling aura effect for your Eagley", 1000),
        PremiumItem("Independence Day Fireworks", "🎆", "Special animation effect", 1500)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eagley Customization") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            // Current Balance
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = patriot_medium_blue.copy(alpha = 0.9f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Level display
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(patriot_red_bright)
                                .border(2.dp, patriot_gold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$userLevel",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = patriot_white
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = "Level $userLevel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = patriot_white
                        )
                    }
                    
                    // Coin balance
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                            text = "$currentCoins",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = patriot_gold
                        )
                    }
                }
            }
            
            // Tab selection
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
            
            // Tab content
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (selectedTab) {
                    0 -> UnlockedItemsTab(unlockedItems, userLevel)
                    1 -> PremiumShopTab(premiumItems, currentCoins) { itemPrice ->
                        // Handle purchase
                        if (currentCoins >= itemPrice) {
                            currentCoins -= itemPrice
                            // In a real app, update the user's inventory
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnlockedItemsTab(items: List<CustomizationItem>, userLevel: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            val isUnlocked = item.isUnlocked
            val canEquip = isUnlocked
            var isEquipped by remember { mutableStateOf(false) }
            
            CustomizationItemCard(
                item = item,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                onEquipClick = {
                    if (canEquip) {
                        isEquipped = !isEquipped
                    }
                }
            )
        }
    }
}

@Composable
fun PremiumShopTab(items: List<PremiumItem>, currentCoins: Int, onPurchase: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            val canPurchase = currentCoins >= item.price
            
            PremiumItemCard(
                item = item,
                canPurchase = canPurchase,
                onPurchaseClick = { onPurchase(item.price) }
            )
        }
    }
}

@Composable
fun CustomizationItemCard(
    item: CustomizationItem,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    onEquipClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) patriot_medium_blue else patriot_medium_blue.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Item icon/image centered
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .background(if (isUnlocked) patriot_dark_blue else patriot_dark_blue.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 24.sp
                )
            }
            
            // Item details at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) patriot_white else patriot_white.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = item.unlockRequirement,
                    fontSize = 10.sp,
                    color = if (isUnlocked) patriot_gold else patriot_white.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Button(
                    onClick = onEquipClick,
                    enabled = isUnlocked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEquipped) patriot_gold else patriot_red_bright,
                        disabledContainerColor = patriot_dark_blue.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                        Text(
                        text = if (isEquipped) "EQUIPPED" else "EQUIP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = patriot_white
                    )
                }
            }
            
            // Lock icon for locked items
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = patriot_white,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumItemCard(
    item: PremiumItem,
    canPurchase: Boolean,
    onPurchaseClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            // Premium indicator at top right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(patriot_gold, RoundedCornerShape(4.dp))
                    .padding(2.dp)
            ) {
                Text(
                    text = "PREMIUM",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = patriot_dark_blue
                )
            }
            
            // Item icon/image centered
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .background(patriot_dark_blue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.icon,
                    fontSize = 24.sp
                )
            }
            
            // Item details at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = patriot_white,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Button(
                    onClick = onPurchaseClick,
                    enabled = canPurchase,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = patriot_red_bright,
                        disabledContainerColor = patriot_dark_blue.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "💰 ${item.price}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = patriot_white
                        )
                    }
                }
            }
        }
    }
}

data class CustomizationItem(
    val name: String,
    val icon: String,
    val description: String,
    val unlockRequirement: String,
    val isUnlocked: Boolean
)

data class PremiumItem(
    val name: String,
    val icon: String,
    val description: String,
    val price: Int
) 