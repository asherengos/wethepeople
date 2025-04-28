package com.example.wethepeople.ui.screens.eagly

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import com.example.wethepeople.data.model.Accessory
import com.example.wethepeople.ui.viewmodel.EagleyViewModel
import com.example.wethepeople.ui.theme.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun EaglyCustomizationScreen(
    viewModel: EagleyViewModel,
    accessories: List<Accessory>,
    baseResId: Int,
    onSave: (Map<String, Pair<Offset, Float>>) -> Unit,
    onError: (String) -> Unit = {}
) {
    val customizations by viewModel.assetCustomizations.collectAsState(initial = emptyMap())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Instructions text
        Text(
            text = "DRAG ACCESSORIES TO CUSTOMIZE YOUR EAGLEY",
            color = patriot_gold,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Eagley drag area
        Box(
            modifier = Modifier
                .size(300.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Eagly base image
            SafeImage(
                resourceId = baseResId,
                contentDescription = "Eagly",
                onError = { error -> onError("Error loading Eagly: $error") }
            )
            
            // Draggable accessories with pinch and zoom
            accessories.forEach { accessory ->
                var offset by remember { mutableStateOf(customizations[accessory.id]?.first ?: Offset(0f, 0f)) }
                var scale by remember { mutableStateOf(customizations[accessory.id]?.second ?: 1f) }
                val initialSize = accessory.initialSize.dp
                
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                        .size(initialSize)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                // Update position
                                offset += pan
                                viewModel.updateAssetPosition(accessory.id, offset)
                                
                                // Update scale with some reasonable limits
                                scale = (scale * zoom).coerceIn(0.5f, 3.0f)
                                viewModel.updateAssetScale(accessory.id, scale)
                                
                                // Update customization in one call
                                viewModel.updateAssetCustomization(accessory.id, offset, scale)
                            }
                        }
                ) {
                    SafeImage(
                        resourceId = accessory.resId,
                        contentDescription = accessory.id,
                        modifier = Modifier.fillMaxSize(),
                        onError = { error -> onError("Error loading accessory ${accessory.id}: $error") }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Save button
        Button(
            onClick = { onSave(customizations) },
            colors = ButtonDefaults.buttonColors(
                containerColor = patriot_red_bright
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .height(48.dp)
                .width(200.dp)
        ) {
            Text(
                text = "SAVE CUSTOMIZATION",
                color = patriot_white,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Information about dragging
        Text(
            text = "Drag items like the shield and hat to position them exactly how you want!",
            color = patriot_white,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}

// Safe image loading helper (this goes outside the main composable)
@Composable
private fun SafeImage(
    resourceId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onError: (String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isResourceValid = remember(resourceId) {
        try {
            context.resources.getResourceName(resourceId)
            true
        } catch (e: Exception) {
            onError("Resource not found: $resourceId")
            false
        }
    }
    
    if (isResourceValid) {
        Image(
            painter = painterResource(resourceId),
            contentDescription = contentDescription,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .size(200.dp)
                .background(patriot_dark_blue),
            contentAlignment = Alignment.Center
        ) {
            Text(contentDescription, color = patriot_white)
        }
    }
} 