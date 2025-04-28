package com.example.wethepeople.ui.screens.demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wethepeople.ui.animation.ConstitutionAnimation
import com.example.wethepeople.ui.animation.FreedomAnimation
import com.example.wethepeople.ui.animation.FreedomAnimationType
import com.example.wethepeople.ui.animation.LottieAnimationComponent
import com.example.wethepeople.R

/**
 * A demo screen that showcases the various patriotic animations available in the app.
 * This screen allows users to preview different animations by pressing buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationDemoScreen() {
    var currentAnimation by remember { mutableStateOf<FreedomAnimationType?>(null) }
    var isAnimationPlaying by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Patriotic Animations") }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Tap a button to preview a patriotic animation",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { 
                        currentAnimation = FreedomAnimationType.CONSTITUTION
                        isAnimationPlaying = true
                    },
                    enabled = !isAnimationPlaying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Constitution Animation")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        currentAnimation = FreedomAnimationType.EAGLE
                        isAnimationPlaying = true
                    },
                    enabled = !isAnimationPlaying,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Flying Eagle Animation")
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Animation preview container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    // Show different animations based on selected type
                    when {
                        isAnimationPlaying && currentAnimation != null -> {
                            FreedomAnimation(
                                isActive = true,
                                animationType = currentAnimation!!,
                                onAnimationComplete = {
                                    isAnimationPlaying = false
                                }
                            )
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Animation preview will appear here",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
} 