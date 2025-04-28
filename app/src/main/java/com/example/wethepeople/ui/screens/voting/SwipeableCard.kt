package com.example.wethepeople.ui.screens.voting

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.sign
import kotlin.math.abs
import androidx.compose.ui.draw.rotate

//Thresholds, etc
private const val _velocityThreshold = 500f

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    onSwipeComplete: (Boolean) -> Unit,
    onCardClick: () -> Unit,
    content: @Composable (swipeProgress: Float, rotation: Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isDragging by remember { mutableStateOf(false) }
    
    // Screen width for calculating swipe threshold
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }
    
    // Animation values
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    
    // Constants for swipe behavior
    val swipeThreshold = screenWidth * 0.4f
    val rotationFactor = 15f / screenWidth
    
    // Calculate swipe progress relative to threshold (-1f to 1f)
    val swipeProgress = remember(offsetX.value, swipeThreshold) {
        (offsetX.value / swipeThreshold).coerceIn(-1f, 1f)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .offset { IntOffset(offsetX.value.toInt(), offsetY.value.toInt()) }
            .graphicsLayer(
                rotationZ = rotation.value
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        scope.launch {
                            // Check if swipe exceeds threshold
                            val isSwipedEnough = offsetX.value.absoluteValue > swipeThreshold
                            
                            if (isSwipedEnough) {
                                // Animate off screen
                                val targetX = screenWidth * 1.5f * offsetX.value.sign
                                val targetRotation = rotationFactor * targetX
                                
                                launch { offsetX.animateTo(targetX, tween(300)) }
                                launch { 
                                    rotation.animateTo(targetRotation, tween(300))
                                    // Signal swipe completion after animation
                                    onSwipeComplete(offsetX.value > 0)
                                }
                            } else {
                                // Snap back
                                launch { offsetX.animateTo(0f, spring()) }
                                launch { offsetY.animateTo(0f, spring()) }
                                launch { rotation.animateTo(0f, spring()) }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        val originalX = offsetX.targetValue
                        val originalY = offsetY.targetValue
                        
                        scope.launch {
                            offsetX.snapTo(originalX + dragAmount.x)
                            offsetY.snapTo(originalY + dragAmount.y)
                            rotation.snapTo(offsetX.value * rotationFactor)
                        }
                        
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { 
                        if (!isDragging) {
                            onCardClick()
                        }
                    }
                )
            }
    ) {
        content(swipeProgress, rotation.value)
    }
} 