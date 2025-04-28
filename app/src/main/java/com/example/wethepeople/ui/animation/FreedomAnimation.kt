package com.example.wethepeople.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.wethepeople.R
import kotlinx.coroutines.delay

/**
 * A composable that displays a freedom animation when triggered.
 * This can be used to celebrate patriotic actions in the app.
 * 
 * @param isActive Whether the animation should be shown
 * @param onAnimationComplete Callback for when the animation finishes
 * @param animationType The type of freedom animation to display
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun FreedomAnimation(
    isActive: Boolean,
    onAnimationComplete: () -> Unit,
    animationType: FreedomAnimationType = FreedomAnimationType.CONSTITUTION,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(isActive) }
    
    LaunchedEffect(isActive) {
        if (isActive) {
            isVisible = true
            delay(3500) // Animation duration
            isVisible = false
            delay(300) // Exit animation duration
            onAnimationComplete()
        }
    }
    
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (animationType) {
                FreedomAnimationType.CONSTITUTION -> ConstitutionAnimation()
                FreedomAnimationType.EAGLE -> EagleAnimation()
            }
        }
    }
}

/**
 * The type of freedom animation to display
 */
enum class FreedomAnimationType {
    CONSTITUTION,
    EAGLE
}

@Composable
private fun EagleAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.eagle_fly))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.fillMaxSize()
    )
} 