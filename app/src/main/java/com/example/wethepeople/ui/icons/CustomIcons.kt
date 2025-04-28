package com.example.wethepeople.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Chat
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.vectorResource
import com.example.wethepeople.R

/**
 * Custom icon utility to provide backward compatibility for AutoMirrored icons
 * which may not be available in the current Material icons version.
 */
object CustomIcons {
    /**
     * A simple back arrow that doesn't rely on AutoMirrored functionality
     */
    val ArrowBack: ImageVector = Icons.Default.ArrowBack
    
    /**
     * A forward arrow that doesn't rely on AutoMirrored functionality
     */
    val ArrowForward: ImageVector = Icons.Default.ArrowForward

    /**
     * Default chat/debate icon from Material Icons
     */
    val Debate: ImageVector = Icons.Default.Chat
    
    /**
     * Custom debate icon from drawable resources
     */
    object Drawable {
        @Composable
        fun Debate(): ImageVector = ImageVector.vectorResource(id = R.drawable.ic_debate)
    }
} 