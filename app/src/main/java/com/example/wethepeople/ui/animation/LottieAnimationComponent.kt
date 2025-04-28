package com.example.wethepeople.ui.animation

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import android.util.Log

/**
 * A reusable Lottie animation component that can load and display Lottie animations
 * from raw resources with customizable iterations.
 *
 * @param animationRes Raw resource ID for the Lottie animation JSON file
 * @param modifier Modifier for customizing the layout
 * @param iterations Number of times to play the animation (use LottieConstants.IterateForever for endless loop)
 */
@Composable
fun LottieAnimationComponent(
    @RawRes animationRes: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever
) {
    // Flag to track if the Lottie animation failed
    var isLottieError by remember { mutableStateOf(false) }
    
    // Composition result handling
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val composition = compositionResult.value
    
    // Monitor for composition errors
    LaunchedEffect(compositionResult.isComplete) {
        if (compositionResult.isComplete && composition == null) {
            Log.e("LottieAnimation", "Error loading Lottie animation")
            isLottieError = true
        }
    }
    
    // If we have a valid composition and no errors, show the Lottie animation
    if (composition != null && !isLottieError) {
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = iterations
        )
        
        LottieAnimation(
            composition = composition,
            progress = progress,
            modifier = modifier
        )
    } else {
        // If composition failed to load or is invalid, show the backup animation
        ConstitutionAnimation(modifier = modifier)
    }
} 