package com.example.wethepeople.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.wethepeople.R

/**
 * Reusable Composable that displays the user's customized Eagley with their selected items
 * Optimized to load resources asynchronously and reduce main thread work
 */
@Composable
fun CustomizedEagley(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    isForID: Boolean = false, // Default to false (customization view) unless specified
    baseResourceId: Int,
    hatResourceId: Int?,
    accessoryResourceId: Int?,
    hatItemId: String?,
    accessoryItemId: String?,
    customizations: Map<String, Pair<androidx.compose.ui.geometry.Offset, Float>> = emptyMap()
) {
    // Use LocalContext for image loading
    val context = LocalContext.current
    
    // Get customization data for accessories - Use specific values as remember keys
    val hatCustomizationData = remember(customizations, hatItemId) {
        hatItemId?.let { customizations["hat_$it"] }
    }
    
    val accessoryCustomizationData = remember(customizations, accessoryItemId) {
        accessoryItemId?.let { customizations["accessory_$it"] }
    }
    
    // Pre-calculate hat and accessory modifiers with customization data
    // Use specific offset/scale values (or null) as remember keys for stability
    val hatOffset = hatCustomizationData?.first
    val hatScale = hatCustomizationData?.second
    val hatModifier = remember(hatItemId, isForID, hatOffset, hatScale) { // Use hatOffset & hatScale as keys
        if (hatOffset != null && hatScale != null && !isForID) {
            // Apply custom position and scale from customization data
            Modifier
                .fillMaxSize(0.55f * hatScale)  // Apply customized scale
                .offset(x = hatOffset.x.dp, y = hatOffset.y.dp)  // Apply customized position
        } else {
            // Use default positioning
            calculateHatModifier(hatItemId, isForID)
        }
    }
    
    val accessoryOffset = accessoryCustomizationData?.first
    val accessoryScale = accessoryCustomizationData?.second
    val accessoryModifier = remember(accessoryItemId, isForID, accessoryOffset, accessoryScale) { // Use accessoryOffset & accessoryScale as keys
        if (accessoryOffset != null && accessoryScale != null && !isForID) {
            // Apply custom position and scale from customization data
            Modifier
                .fillMaxSize(0.38f * accessoryScale)  // Apply customized scale
                .offset(x = accessoryOffset.x.dp, y = accessoryOffset.y.dp)  // Apply customized position
        } else {
            Modifier
                .fillMaxSize(0.38f)
                .offset(y = 0.dp)
        }
    }
    
    // Create optimized image requests
    val baseRequest = remember(baseResourceId) {
        ImageRequest.Builder(context)
            .data(baseResourceId)
            .crossfade(true)
            .size(Size.ORIGINAL) // Preserve original size for better quality
            .memoryCacheKey("base_$baseResourceId") // Cache with unique key
            .diskCacheKey("base_$baseResourceId")
            .build()
    }
    
    val hatRequest = remember(hatResourceId) {
        hatResourceId?.let {
            ImageRequest.Builder(context)
                .data(it)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .memoryCacheKey("hat_$it")
                .diskCacheKey("hat_$it")
                .build()
        }
    }
    
    val accessoryRequest = remember(accessoryResourceId) {
        accessoryResourceId?.let {
            ImageRequest.Builder(context)
                .data(it)
                .crossfade(true)
                .size(Size.ORIGINAL)
                .memoryCacheKey("accessory_$it")
                .diskCacheKey("accessory_$it")
                .build()
        }
    }
    
    Box(
        modifier = modifier,
        contentAlignment = contentAlignment
    ) {
        // Base Eagley - loaded asynchronously with placeholder
        AsyncImage(
            model = baseRequest,
            contentDescription = "Eagley",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = R.drawable.eagley_base), // Use a simpler placeholder image
            onLoading = { /* Could add loading state here */ },
            onError = { /* Could handle error state here */ }
        )
        
        // Display hat using passed resource ID and item ID for positioning
        hatRequest?.let { request ->
            AsyncImage(
                model = request,
                contentDescription = "Hat", 
                modifier = hatModifier,
                contentScale = ContentScale.Fit,
                onLoading = { /* Could add loading state here */ },
                onError = { /* Could handle error state here */ }
            )
        }
        
        // Display accessory using passed resource ID
        accessoryRequest?.let { request ->
            AsyncImage(
                model = request,
                contentDescription = "Accessory", 
                modifier = accessoryModifier,
                contentScale = ContentScale.Fit,
                onLoading = { /* Could add loading state here */ },
                onError = { /* Could handle error state here */ }
            )
        }
    }
}

/**
 * Helper function to calculate hat modifier based on hat type and context
 * Extracted to reduce recalculations during recomposition
 */
private fun calculateHatModifier(hatItemId: String?, isForID: Boolean): Modifier {
    return when {
        isForID && hatItemId?.contains("freedom") == true -> {
            Modifier
                .fillMaxSize(0.45f)
                .offset(y = (-4).dp)
        }
        !isForID && hatItemId?.contains("freedom") == true -> {
            Modifier
                .fillMaxSize(0.65f)
                .offset(y = (-45).dp)
        }
        isForID && hatItemId?.contains("top_hat") == true -> {
            Modifier
                .fillMaxSize(0.4f)
                .offset(y = (-2).dp)
        }
        !isForID && hatItemId?.contains("top_hat") == true -> {
            Modifier
                .fillMaxSize(0.58f)
                .offset(y = (-34).dp)
        }
        isForID && hatItemId?.contains("cowboy") == true -> {
            Modifier
                .fillMaxSize(0.4f)
                .offset(y = 0.dp)
        }
        !isForID && hatItemId?.contains("cowboy") == true -> {
            Modifier
                .fillMaxSize(0.55f)
                .offset(y = (-22).dp)
        }
        isForID && hatItemId?.contains("pirate") == true -> {
            Modifier
                .fillMaxSize(0.4f)
                .offset(y = 0.dp)
        }
        !isForID && hatItemId?.contains("pirate") == true -> {
            Modifier
                .fillMaxSize(0.55f)
                .offset(y = (-24).dp)
        }
        isForID && hatItemId?.contains("clown") == true -> {
            Modifier
                .fillMaxSize(0.4f)
                .offset(y = 0.dp)
        }
        !isForID && hatItemId?.contains("clown") == true -> {
            Modifier
                .fillMaxSize(0.55f)
                .offset(y = (-22).dp)
        }
        isForID && hatItemId?.contains("rasta") == true -> {
            Modifier
                .fillMaxSize(0.38f)
                .offset(y = 2.dp)
        }
        !isForID && hatItemId?.contains("rasta") == true -> {
            Modifier
                .fillMaxSize(0.55f)
                .offset(y = (-18).dp)
        }
        // Default positioning if ID doesn't match known types
        isForID -> Modifier
            .fillMaxSize(0.38f)
            .offset(y = 2.dp)
        else -> Modifier // Default for customization screen
            .fillMaxSize(0.55f)
            .offset(y = (-18).dp)
    }
} 