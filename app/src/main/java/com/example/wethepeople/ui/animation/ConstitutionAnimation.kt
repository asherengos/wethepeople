package com.example.wethepeople.ui.animation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A simple animation to represent the Constitution document
 * Used as a fallback when Lottie animation resources aren't available
 */
@Composable
fun ConstitutionAnimation(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    foregroundColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    // Use a more efficient animation spec
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing), // Slower animation to reduce CPU usage
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Optional: lower the framerate for animations
    SideEffect {
        // This is just a hint - system may ignore it
        System.setProperty("debug.hwui.max_fps", "30")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // A simpler shape with less frequent updates
        Box(
            modifier = Modifier
                .size(120.dp)
                .rotate(angle)
                .clip(RoundedCornerShape(16.dp))
                .background(foregroundColor),
            contentAlignment = Alignment.Center
        ) {
            // Add text to make it more interesting
            Text(
                text = "We The People",
                color = backgroundColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
} 