package com.example.wethepeople.ui.screens.eagly

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wethepeople.R
import com.example.wethepeople.data.model.CustomizationType
import com.example.wethepeople.data.model.EagleyItem
import com.example.wethepeople.data.model.ItemRarity
import com.example.wethepeople.ui.theme.*
import com.example.wethepeople.ui.viewmodel.EagleyViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.example.wethepeople.ui.icons.CustomIcons
import com.example.wethepeople.ui.components.CustomizedEagley
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.size.Size
import com.example.wethepeople.data.model.UserEagleyData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EaglyScreen(
    navController: NavController? = null,
    viewModel: EagleyViewModel = viewModel()
) {
    val userData by viewModel.userData.collectAsState()
    val currentCustomizations by viewModel.currentCustomizations.collectAsState()
    val currentTab = remember { mutableStateOf(CustomizationType.BORDER) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EAGLEY CUSTOMIZATION",
                        fontWeight = FontWeight.Bold,
                        color = patriot_white
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
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
        containerColor = patriot_dark_blue
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // User stats bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Patriot Points
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star, 
                        contentDescription = null,
                        tint = patriot_gold, 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PATRIOT POINTS: ${userData.patriotPoints}",
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                
                // Citizenship rank
                Text(
                    text = "RANK: ${userData.citizenRank}",
                    color = patriot_gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            
            // Preview section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val hatItem = viewModel.getCurrentSelection(CustomizationType.HAT)
                val accessoryItem = viewModel.getCurrentSelection(CustomizationType.ACCESSORY)

                // Log the IDs being passed to the customizer
                Log.d("EaglyScreen", "Rendering DraggableEagleyCustomizer with Hat ID: ${hatItem?.id}, Accessory ID: ${accessoryItem?.id}")

                DraggableEagleyCustomizer(
                    baseResId = viewModel.getCurrentSelection(CustomizationType.MASCOT)?.resourceId ?: R.drawable.eagley_base,
                    hatResId = hatItem?.resourceId,
                    accessoryResId = accessoryItem?.resourceId,
                    hatItemId = hatItem?.id,
                    accessoryItemId = accessoryItem?.id,
                    initialCustomizations = currentCustomizations,
                    onSave = { newCustomizations ->
                        viewModel.saveEagleyCustomization(newCustomizations)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { /* The actual save logic is now in the DraggableEagleyCustomizer's onSave callback */ },
                    enabled = false,
                    colors = ButtonDefaults.buttonColors(containerColor = patriot_red_bright),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SAVE CUSTOMIZATION")
                }
            }
            
            // Tabs for different customization types
            TabRow(
                selectedTabIndex = currentTab.value.ordinal,
                containerColor = patriot_medium_blue,
                contentColor = patriot_white,
                indicator = { tabPositions ->
                    Box(
                        Modifier
                            .tabIndicatorOffset(tabPositions[currentTab.value.ordinal])
                            .height(3.dp)
                            .background(color = patriot_red_bright)
                    )
                }
            ) {
                CustomizationType.values().forEach { type ->
                    Tab(
                        selected = currentTab.value == type,
                        onClick = { currentTab.value = type },
                        text = { Text(type.name) }
                    )
                }
            }
            
            // Display items based on selected tab
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = patriot_medium_blue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "AVAILABLE ${currentTab.value.name}S",
                        color = patriot_white,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Items grid
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(viewModel.getItemsByType(currentTab.value)) { item ->
                            CustomizationItem(
                                item = item,
                                isOwned = viewModel.hasItem(item.id),
                                isSelected = viewModel.getCurrentSelection(currentTab.value)?.id == item.id,
                                onSelect = { viewModel.setSelectedItem(item.id, currentTab.value) },
                                onPurchase = { viewModel.purchaseItem(item.id) }
                            )
                        }
                    }
                }
            }
            
            // Earn more points card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = patriot_medium_blue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "EARN MORE PATRIOT POINTS",
                        color = patriot_gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Complete missions to earn Patriot Points!",
                        color = patriot_white,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = {
                            // This is just for demo purposes - in a real app, this would link to missions
                            viewModel.addPatriotPoints(50)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = patriot_red_bright),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "GET 50 POINTS (DEMO)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            // Bottom spacer
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun CustomizationItem(
    item: EagleyItem,
    isOwned: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPurchase: () -> Unit
) {
    val borderColor = when {
        isSelected -> patriot_red_bright
        isOwned -> patriot_gold
        else -> patriot_white.copy(alpha = 0.5f)
    }
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable {
                if (isOwned) {
                    onSelect()
                } else {
                    onPurchase()
                }
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = patriot_dark_blue
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // Item image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                if (item.type == CustomizationType.BORDER) {
                    // Special case for borders: show in a circle
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(
                                width = 4.dp,
                                color = when (item.rarity) {
                                    ItemRarity.RARE -> patriot_gold
                                    ItemRarity.EPIC -> Color(0xFFE91E63) // Pink
                                    ItemRarity.LEGENDARY -> Color(0xFF9C27B0) // Purple
                                    ItemRarity.LIMITED_EDITION -> Color(0xFFFF0000) // Red
                                    else -> patriot_medium_blue
                                },
                                shape = CircleShape
                            )
                    )
                } else {
                    // Other items: show the image
                    Image(
                        painter = painterResource(id = item.resourceId),
                        contentDescription = item.name,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }
                
                // Lock icon if not owned
                if (!isOwned) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                // Selected indicator
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(patriot_red_bright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Item name
            Text(
                text = item.name,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )
            
            // Rarity
            Text(
                text = item.rarity.name,
                color = when (item.rarity) {
                    ItemRarity.COMMON -> patriot_white.copy(alpha = 0.7f)
                    ItemRarity.UNCOMMON -> Color(0xFF4CAF50) // Green
                    ItemRarity.RARE -> patriot_gold
                    ItemRarity.EPIC -> Color(0xFFE91E63) // Pink
                    ItemRarity.LEGENDARY -> Color(0xFF9C27B0) // Purple
                    ItemRarity.LIMITED_EDITION -> Color(0xFFFF0000) // Red
                },
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            // Price or equipped
            if (!isOwned) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = patriot_medium_blue,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = patriot_gold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${item.cost}",
                        color = patriot_gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            } else if (isSelected) {
                Text(
                    text = "EQUIPPED",
                    color = patriot_red_bright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            } else {
                Text(
                    text = "OWNED",
                    color = patriot_white.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun DraggableEagleyCustomizer(
    baseResId: Int,
    hatResId: Int?,
    accessoryResId: Int?,
    hatItemId: String?,
    accessoryItemId: String?,
    initialCustomizations: Map<String, Pair<Offset, Float>>,
    onSave: (Map<String, Pair<Offset, Float>>) -> Unit
) {
    val context = LocalContext.current
    // Keys for the customization map
    val hatKey = hatItemId?.let { "hat_$it" }
    val accessoryKey = accessoryItemId?.let { "accessory_$it" }

    // State for hat and accessory positions and scales, initialized from initialCustomizations
    var hatOffset by remember(hatKey, initialCustomizations) {
        mutableStateOf(initialCustomizations[hatKey]?.first ?: Offset.Zero)
    }
    var hatScale by remember(hatKey, initialCustomizations) {
        mutableStateOf(initialCustomizations[hatKey]?.second ?: 1.0f)
    }
    var accessoryOffset by remember(accessoryKey, initialCustomizations) {
        mutableStateOf(initialCustomizations[accessoryKey]?.first ?: Offset.Zero)
    }
    var accessoryScale by remember(accessoryKey, initialCustomizations) {
        mutableStateOf(initialCustomizations[accessoryKey]?.second ?: 1.0f)
    }

    // State to trigger save callback - This needs refinement to be triggered by a button press
    // For now, we'll call onSave directly when offsets change, which isn't ideal.
    // TODO: Trigger save from an explicit "Save" button press.

    Column(horizontalAlignment = Alignment.CenterHorizontally) { // Wrap in column to add button below
        Box(modifier = Modifier.size(200.dp)) {
            // Restore base Eagly image
            AsyncImage(
                model = ImageRequest.Builder(context).data(baseResId).crossfade(true).build(),
                contentDescription = "Eagly",
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = R.drawable.eagley_base) // Keep simple placeholder
            )
            

            // Draggable and Scalable hat - Use AsyncImage
            if (hatResId != null && hatKey != null) {
                 AsyncImage(
                     model = ImageRequest.Builder(context).data(hatResId).crossfade(true).build(),
                     contentDescription = "Hat",
                    modifier = Modifier
                        .size((50 * hatScale).dp) // Apply scale - adjust base size as needed
                        .offset { IntOffset(hatOffset.x.roundToInt(), hatOffset.y.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                hatOffset += dragAmount
                            }
                        },
                    placeholder = painterResource(id = R.drawable.eagley_base) // Placeholder
                )
            }
            // Draggable and Scalable accessory - Use AsyncImage
            if (accessoryResId != null && accessoryKey != null) {
                 AsyncImage(
                     model = ImageRequest.Builder(context).data(accessoryResId).crossfade(true).build(),
                     contentDescription = "Accessory",
                     modifier = Modifier
                        .size((50 * accessoryScale).dp) // Apply scale - adjust base size as needed
                        .offset { IntOffset(accessoryOffset.x.roundToInt(), accessoryOffset.y.roundToInt()) }
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                accessoryOffset += dragAmount
                            }
                        },
                    placeholder = painterResource(id = R.drawable.eagley_base) // Placeholder
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Add space before button

        // Actual Save Button within the Draggable composable
        Button(
            onClick = {
                val currentMap = buildCustomizationMap(
                    hatKey, hatOffset, hatScale,
                    accessoryKey, accessoryOffset, accessoryScale
                )
                onSave(currentMap) // Call the save callback
            },
            colors = ButtonDefaults.buttonColors(containerColor = patriot_red_bright),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("SAVE CUSTOMIZATION")
        }
    }
}

// Helper function to build the customization map
private fun buildCustomizationMap(
    hatKey: String?, hatOffset: Offset, hatScale: Float,
    accessoryKey: String?, accessoryOffset: Offset, accessoryScale: Float
): Map<String, Pair<Offset, Float>> {
    val map = mutableMapOf<String, Pair<Offset, Float>>()
    hatKey?.let { map[it] = Pair(hatOffset, hatScale) }
    accessoryKey?.let { map[it] = Pair(accessoryOffset, accessoryScale) }
    return map
} 