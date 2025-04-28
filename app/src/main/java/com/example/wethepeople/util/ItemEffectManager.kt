package com.example.wethepeople.util

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.airbnb.lottie.compose.*
import com.example.wethepeople.R
import com.example.wethepeople.data.model.ShopItem
import com.example.wethepeople.data.model.ShopItemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random
import com.example.wethepeople.ui.animation.LottieAnimationComponent

class ItemEffectManager private constructor(private val context: Context) {
    private val _activeEffects = MutableStateFlow<Map<ShopItemCategory, ShopItem>>(emptyMap())
    val activeEffects: StateFlow<Map<ShopItemCategory, ShopItem>> = _activeEffects.asStateFlow()

    private val _characterOutfit = MutableStateFlow<ShopItem?>(null)
    val characterOutfit: StateFlow<ShopItem?> = _characterOutfit.asStateFlow()

    private val _characterAccessory = MutableStateFlow<ShopItem?>(null)
    val characterAccessory: StateFlow<ShopItem?> = _characterAccessory.asStateFlow()

    private val _eaglyOutfit = MutableStateFlow<ShopItem?>(null)
    val eaglyOutfit: StateFlow<ShopItem?> = _eaglyOutfit.asStateFlow()

    private val _eaglyAccessory = MutableStateFlow<ShopItem?>(null)
    val eaglyAccessory: StateFlow<ShopItem?> = _eaglyAccessory.asStateFlow()

    fun applyEffect(item: ShopItem) {
        when (item.category) {
            ShopItemCategory.SPECIAL_EFFECT -> applySpecialEffect(item)
            ShopItemCategory.PROFILE_FRAME -> applyProfileFrame(item)
            ShopItemCategory.PROFILE_BACKGROUND -> applyBackground(item)
            ShopItemCategory.CHARACTER_OUTFIT -> applyOutfit(item)
            ShopItemCategory.CHARACTER_ACCESSORY -> applyAccessory(item)
            ShopItemCategory.EAGLY_OUTFIT -> applyEaglyOutfit(item)
            ShopItemCategory.EAGLY_ACCESSORY -> applyEaglyAccessory(item)
        }
        _activeEffects.value = _activeEffects.value + (item.category to item)
    }

    fun removeEffect(category: ShopItemCategory) {
        _activeEffects.value = _activeEffects.value - category
        
        // Also clear specific state flow for character/eagly items
        when (category) {
            ShopItemCategory.CHARACTER_OUTFIT -> _characterOutfit.value = null
            ShopItemCategory.CHARACTER_ACCESSORY -> _characterAccessory.value = null
            ShopItemCategory.EAGLY_OUTFIT -> _eaglyOutfit.value = null
            ShopItemCategory.EAGLY_ACCESSORY -> _eaglyAccessory.value = null
            else -> {} // No additional action needed
        }
    }

    private fun applySpecialEffect(item: ShopItem) {
        // Effects are handled by Composables
    }

    private fun applyProfileFrame(item: ShopItem) {
        // Frame is applied through the ProfileFrame composable
    }

    private fun applyBackground(item: ShopItem) {
        // Background is applied through the ProfileBackground composable
    }

    private fun applyOutfit(item: ShopItem) {
        _characterOutfit.value = item
    }

    private fun applyAccessory(item: ShopItem) {
        _characterAccessory.value = item
    }

    private fun applyEaglyOutfit(item: ShopItem) {
        _eaglyOutfit.value = item
    }

    private fun applyEaglyAccessory(item: ShopItem) {
        _eaglyAccessory.value = item
    }

    @Composable
    fun AmericanFlagWaveEffect(modifier: Modifier = Modifier) {
        var showFallback by remember { mutableStateOf(false) }
        
        if (!showFallback) {
            LottieAnimationComponent(
                animationRes = R.raw.american_flag_wave,
                modifier = modifier.fillMaxWidth().height(200.dp)
            )
        } else {
            // Fallback if animation fails
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF3C3B6E)),
                contentAlignment = Alignment.Center
            ) {
                Text("🇺🇸", fontSize = 48.sp)
            }
        }
    }

    @Composable
    fun EagleFlyEffect(modifier: Modifier = Modifier) {
        var showFallback by remember { mutableStateOf(false) }
        
        if (!showFallback) {
            LottieAnimationComponent(
                animationRes = R.raw.eagle_fly,
                modifier = modifier.fillMaxWidth().height(200.dp)
            )
        } else {
            // Fallback if animation fails
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF5F5DC)),
                contentAlignment = Alignment.Center
            ) {
                Text("🦅", fontSize = 48.sp)
            }
        }
    }

    @Composable
    fun ConstitutionEffect(modifier: Modifier = Modifier) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            var showFallback by remember { mutableStateOf(false) }
            
            if (!showFallback) {
                LottieAnimationComponent(
                    animationRes = R.raw.constitution_scroll,
                    modifier = Modifier.size(300.dp),
                    iterations = 1
                )
            } else {
                // Fallback if animation fails
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color(0xFFF5F5DC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📜", fontSize = 48.sp)
                }
            }
        }
    }

    @Composable
    fun ProfileFrame(content: @Composable () -> Unit, item: ShopItem) {
        val frameStyle = when (item.id) {
            "frame_constitution" -> FrameStyle(
                color = Color(0xFFFFD700), // Gold
                width = 6.dp,
                cornerRadius = 12.dp
            )
            "frame_liberty_bell" -> FrameStyle(
                color = Color(0xFF8B4513), // Bronze
                width = 4.dp, 
                cornerRadius = 8.dp
            )
            "frame_stars_stripes" -> FrameStyle(
                colors = listOf(Color(0xFFB22234), Color(0xFF3C3B6E), Color(0xFFFFFFFF)),
                width = 5.dp,
                cornerRadius = 10.dp
            )
            else -> FrameStyle(
                color = Color.Gray,
                width = 3.dp,
                cornerRadius = 8.dp
            )
        }
        
        Box(
            modifier = Modifier
                .padding(4.dp)
                .then(
                    if (frameStyle.colors != null) {
                        Modifier.border(
                            width = frameStyle.width,
                            brush = Brush.linearGradient(frameStyle.colors),
                            shape = RoundedCornerShape(frameStyle.cornerRadius)
                        )
                    } else {
                        Modifier.border(
                            width = frameStyle.width,
                            color = frameStyle.color,
                            shape = RoundedCornerShape(frameStyle.cornerRadius)
                        )
                    }
                )
                .clip(RoundedCornerShape(frameStyle.cornerRadius))
        ) {
            content()
        }
    }

    @Composable
    fun ProfileBackground(content: @Composable () -> Unit, item: ShopItem) {
        Box(
            modifier = Modifier
                .background(
                    when (item.id) {
                        "bg_independence_hall" -> Color(0xFFF5F5DC) // Beige
                        "bg_mount_rushmore" -> Color(0xFFB8B8B8) // Stone Gray
                        "bg_capitol" -> Color(0xFFE0F7FA) // Light Blue
                        "bg_white_house" -> Color(0xFFFAFAFA) // Off White
                        else -> Color.White
                    }
                )
        ) {
            // Load background image if available
            item.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize().alpha(0.6f)
                )
            }
            content()
        }
    }

    @Composable
    fun CharacterWithOutfitAndAccessory(
        baseCharacterImageUrl: String,
        modifier: Modifier = Modifier
    ) {
        val outfit = _characterOutfit.collectAsState().value
        val accessory = _characterAccessory.collectAsState().value
        
        Box(modifier = modifier) {
            // Base character
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(baseCharacterImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Character",
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize()
            )
            
            // Outfit layer
            outfit?.imageUrl?.let { outfitUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(outfitUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = outfit.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            }
            
            // Accessory layer
            accessory?.imageUrl?.let { accessoryUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(accessoryUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = accessory.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }

    @Composable
    fun EaglyWithOutfitAndAccessory(
        baseEaglyImageUrl: String,
        modifier: Modifier = Modifier
    ) {
        val outfit = _eaglyOutfit.collectAsState().value
        val accessory = _eaglyAccessory.collectAsState().value
        
        Box(modifier = modifier) {
            // Base eagly
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(baseEaglyImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Eagly",
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize()
            )
            
            // Outfit layer
            outfit?.imageUrl?.let { outfitUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(outfitUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = outfit?.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            }
            
            // Accessory layer
            accessory?.imageUrl?.let { accessoryUrl ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(accessoryUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = accessory?.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ItemEffectManager? = null

        fun getInstance(context: Context): ItemEffectManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ItemEffectManager(context).also { INSTANCE = it }
            }
        }

        // Data class for frame styling
        data class FrameStyle(
            val color: Color = Color.Gray,
            val colors: List<Color>? = null,
            val width: androidx.compose.ui.unit.Dp = 3.dp,
            val cornerRadius: androidx.compose.ui.unit.Dp = 8.dp
        )
    }
} 