package com.example.wethepeople.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.theme.*
import java.time.Duration

@Composable
fun PatriotButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = patriot_red_bright,
            contentColor = patriot_white
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = patriot_white
            )
        } else {
            Text(
                text = text,
                fontSize = font_md.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VoteProgressBar(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "Progress Animation"
    )
    
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = patriot_red_bright,
        trackColor = patriot_blue_light
    )
}

@Composable
fun CountdownTimer(
    timeRemaining: Duration,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Schedule,
            contentDescription = "Time remaining",
            tint = patriot_gold
        )
        
        Spacer(modifier = Modifier.width(4.dp))
        
        Text(
            text = "${timeRemaining.toHours()}h ${timeRemaining.toMinutesPart()}m remaining",
            color = patriot_gold,
            fontSize = font_sm.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun VoteOption(
    text: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) patriot_medium_blue else patriot_blue_light
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = patriot_white,
                    unselectedColor = patriot_dark_blue
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = text,
                color = if (isSelected) patriot_white else patriot_dark_blue,
                fontSize = font_md.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun SuccessAnimation(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(success_green, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            tint = patriot_white,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun TriviaCard(
    question: String,
    isCorrect: Boolean?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = when (isCorrect) {
                true -> success_green.copy(alpha = 0.1f)
                false -> error_red.copy(alpha = 0.1f)
                null -> patriot_medium_blue
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = question,
                color = patriot_white,
                fontSize = font_lg.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            if (isCorrect != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isCorrect) "Correct!" else "Try again!",
                    color = if (isCorrect) success_green else error_red,
                    fontSize = font_sm.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PointsProgressBar(currentPoints: Int, maxPoints: Int, level: Int) {
    val progress = (currentPoints.toFloat() / maxPoints.toFloat()).coerceIn(0f, 1f)
    LinearProgressIndicator(
        progress = progress,
        modifier = Modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = patriot_red_bright,
        trackColor = patriot_blue_light
    )
} 