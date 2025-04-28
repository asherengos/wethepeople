package com.example.wethepeople.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A badge that shows the unread notification count
 */
@Composable
fun NotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = fadeIn(animationSpec = tween(150)),
        exit = fadeOut(animationSpec = tween(150))
    ) {
        Box(
            modifier = modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(color)
                .padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            val displayCount = if (count > 99) "99+" else count.toString()
            
            Text(
                text = displayCount,
                color = Color.White,
                fontSize = if (count > 99) 7.sp else 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * A notification badge that is overlaid on another component (like a navigation icon)
 */
@Composable
fun OverlayNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.error
) {
    if (count > 0) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            NotificationBadge(
                count = count,
                color = color
            )
        }
    }
} 