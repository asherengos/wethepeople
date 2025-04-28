package com.example.wethepeople.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wethepeople.R
import com.example.wethepeople.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavController) {
    // Screen width for perspective calculation if needed
    // val configuration = LocalConfiguration.current
    // val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val density = LocalDensity.current.density

    // Door animation state - now for rotation
    val leftDoorRotation = remember { Animatable(0f) }
    val rightDoorRotation = remember { Animatable(0f) }

    // Coroutine scope for animations/navigation
    val scope = rememberCoroutineScope()

    // Initial loading delay & trigger animations/navigation
    LaunchedEffect(Unit) {
        delay(1500) // Loading time

        // Start navigation IMMEDIATELY after delay
        scope.launch {
            navController.navigate(Screen.Main.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }

        // Animate doors swinging open AFTER triggering navigation
        scope.launch {
            launch {
                leftDoorRotation.animateTo(
                    targetValue = 90f, // Rotate left door INWARDS (positive Y rotation)
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
            }
            launch {
                rightDoorRotation.animateTo(
                    targetValue = -90f, // Rotate right door INWARDS (negative Y rotation)
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    // Content (Doors)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Use a Row with weight to perfectly divide screen
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Left door
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        rotationY = leftDoorRotation.value
                        transformOrigin = TransformOrigin(1f, 0.5f) // Hinge on RIGHT edge (center)
                        cameraDistance = 8 * density // Add perspective
                    }
            ) {
            Image(
                    painter = painterResource(id = R.drawable.splash_door_left),
                    contentDescription = "Left Door",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            // Right door
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .graphicsLayer {
                        rotationY = rightDoorRotation.value
                        transformOrigin = TransformOrigin(0f, 0.5f) // Hinge on LEFT edge (center)
                        cameraDistance = 8 * density // Add perspective
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash_door_right),
                    contentDescription = "Right Door",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
} 