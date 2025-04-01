package com.example.partyofthepeople.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.partyofthepeople.ui.theme.ComposeColors
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

@Composable
fun FreedomBuckIcon() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(ComposeColors.PatriotRed)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Freedom Buck",
            tint = ComposeColors.NeonWhite,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DebateChampBadge() {
    Surface(
        color = ComposeColors.PatriotBlue,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Debate Champ",
                tint = ComposeColors.NeonWhite
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Debate Champ",
                color = ComposeColors.NeonWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PatrioticFAB(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = ComposeColors.PatriotRed
    ) {
        Text(
            text = "Sound Off!",
            color = ComposeColors.NeonWhite,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NewsTicker(news: String) {
    Surface(
        color = ComposeColors.PatriotBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎤 Breaking News: ",
                color = ComposeColors.PatriotRed,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = news,
                color = ComposeColors.NeonWhite
            )
        }
    }
}

@Composable
fun PatrioticButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ComposeColors.PatriotRed)
    ) {
        Text("Sound Off!", color = ComposeColors.NeonWhite)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatrioticHomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Party of the People", color = ComposeColors.NeonWhite)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ComposeColors.PatriotBlue
                )
            )
        },
        floatingActionButton = {
            PatrioticFAB(onClick = { /* Handle FAB click */ })
        },
        bottomBar = {
            NewsTicker(news = "Patriotism surges to all-time high!")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FreedomBuckIcon()
            DebateChampBadge()
            PatrioticButton(onClick = { /* Handle button click */ })
            MyComposable()
        }
    }
}

@Composable
fun MyComposable() {
    Surface(color = ComposeColors.PatriotBlue) {
        Text("America First!", color = ComposeColors.NeonWhite)
    }
}