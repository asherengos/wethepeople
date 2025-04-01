package com.example.partyofthepeople.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partyofthepeople.R

/**
 * A patriotic button that encourages users to leave comments on proposals
 * after voting. It has a pulsing animation to draw attention.
 */
@Composable
fun SoundOffButton(
    onClick: () -> Unit = {},
    isAfterVote: Boolean = false
) {
    val patriotRed = colorResource(id = R.color.patriot_red)
    val patriotBlue = colorResource(id = R.color.patriot_blue)
    val patriotGold = colorResource(id = R.color.patriot_gold)
    val neonWhite = colorResource(id = R.color.neon_white)
    
    // Animation for the button to pulse
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    // Change color based on whether this is shown after a vote
    val backgroundColor by animateColorAsState(
        targetValue = if (isAfterVote) patriotRed else patriotBlue,
        label = "background color"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        if (isAfterVote) {
            Text(
                text = "YOU VOTED! NOW SOUND OFF!",
                color = patriotGold,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Share your patriotic wisdom with fellow Freedom Fighters!",
                color = neonWhite,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
            modifier = Modifier
                .scale(scale)
                .height(48.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = "💬 " + if (isAfterVote) "SOUND OFF, PATRIOT!" else "SOUND OFF!",
                color = neonWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
        
        if (isAfterVote) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Suggested topics: Constitutional principles, freedom values, or your vision for America!",
                color = Color.LightGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewSoundOffButton() {
    Column {
        SoundOffButton()
        Spacer(modifier = Modifier.height(20.dp))
        SoundOffButton(isAfterVote = true)
    }
} 