package com.example.partyofthepeople.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.partyofthepeople.R

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    var isImageLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1D3557)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "FREEDOM FIGHTERS",
                color = Color(0xFFFFD700),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.america)
                    .decoderFactory(GifDecoder.Factory())
                    .crossfade(true)
                    .listener(
                        onSuccess = { _, _ ->
                            isImageLoaded = true
                            Log.d("SplashScreen", "GIF loaded successfully")
                        },
                        onError = { _, result ->
                            hasError = true
                            Log.e("SplashScreen", "Error loading GIF: ${result.throwable}")
                        }
                    )
                    .build(),
                contentDescription = "Patriotic Animation",
                modifier = Modifier.size(300.dp)
            )

            if (hasError) {
                Text(
                    text = "🦅",
                    fontSize = 64.sp,
                    color = Color.White,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
