package com.example.wethepeople.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.R
import com.example.wethepeople.ui.theme.*

@Composable
fun EagleySignature(
    rank: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start // Keep alignment to start
    ) {
        // Eagley text
        Text(
            text = "Eagley",
            fontFamily = ArtySignatureFontFamily,
            fontSize = 34.sp,
            color = patriot_dark_blue,
            modifier = Modifier.align(Alignment.Bottom) // Align baseline
        )
        
        Spacer(modifier = Modifier.width(6.dp)) // Space before talon
        
        // Talon Signature Image
        Image(
            painter = painterResource(id = R.drawable.eagley_talon_signature),
            contentDescription = "Eagley Signature Talon",
            modifier = Modifier
                .height(36.dp) // Slightly smaller talon to fit better
                .padding(bottom = 4.dp) // Adjust vertical alignment slightly
                .align(Alignment.Bottom) // Align baseline
        )
        
        Spacer(modifier = Modifier.width(10.dp)) // More space after talon
        
        // Rank with enhanced stamp effect
        Text(
            text = rank.uppercase(),
            fontFamily = RankSignatureFontFamily,
            fontSize = 32.sp, // Made rank bigger
            color = patriot_red_bright,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = -10f
                }
                .border(
                    width = 1.5.dp,
                    color = patriot_red_bright.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 10.dp, vertical = 3.dp) // Adjusted padding
                .align(Alignment.Bottom) // Align baseline
        )
    }
} 