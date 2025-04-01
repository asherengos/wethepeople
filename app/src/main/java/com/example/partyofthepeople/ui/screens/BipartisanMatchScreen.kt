package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.partyofthepeople.BipartisanMatch
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.bipartisan.BipartisanMatchViewModel
import com.example.partyofthepeople.VoteRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BipartisanMatchScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit,
    viewModel: BipartisanMatchViewModel = viewModel()
) {
    val matches by viewModel.matches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val voteHistory by viewModel.voteHistory.collectAsState()
    
    // Load matches when screen is shown
    LaunchedEffect(userProfile) {
        viewModel.loadBipartisanMatches(userProfile)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bipartisan Match %") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1D3557),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1D3557))
                .padding(16.dp)
        ) {
            // Explanation card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF457B9D)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "BIPARTISAN MATCH",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "See how your voting aligns with other patriots. Higher percentages mean you agree more often on proposals!",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${voteHistory.size}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Your Votes",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${matches.size}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Patriot Matches",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
            
            // Error message
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE63946)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = message,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            if (isLoading && matches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (matches.isEmpty()) {
                // No matches message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cast more votes to see your bipartisan matches!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Match list
                Text(
                    text = "YOUR MATCHES",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(matches) { match ->
                        MatchRow(match = match)
                        
                        Divider(
                            color = Color(0xFF3A5A83),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                
                if (voteHistory.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Recent votes section
                    Text(
                        text = "YOUR RECENT VOTES",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A4A73)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 180.dp)
                                .padding(8.dp)
                        ) {
                            items(voteHistory.takeLast(5).reversed()) { vote ->
                                VoteHistoryItem(vote = vote)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchRow(match: BipartisanMatch) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Avatar
        if (match.avatar.isNotEmpty()) {
            AsyncImage(
                model = match.avatar,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF457B9D))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // User info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = match.username,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Text(
                text = getMatchDescriptionText(match.matchPercentage),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        
        // Match percentage
        val color = when {
            match.matchPercentage >= 70 -> Color(0xFF4CAF50) // Green
            match.matchPercentage >= 40 -> Color(0xFFFFC107) // Yellow
            else -> Color(0xFFE63946) // Red
        }
        
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${match.matchPercentage}%",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun VoteHistoryItem(vote: VoteRecord) {
    val backgroundColor = if (vote.supported) {
        Color(0xFF4CAF50).copy(alpha = 0.2f) // Green
    } else {
        Color(0xFFE63946).copy(alpha = 0.2f) // Red
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Vote indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (vote.supported) Color(0xFF4CAF50) else Color(0xFFE63946))
                .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (vote.supported) "✓" else "✗",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Proposal title
        Text(
            text = vote.proposalTitle,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

// Add helper function for match description
private fun getMatchDescriptionText(matchPercentage: Int): String {
    return when {
        matchPercentage >= 75 -> "Strong Agreement"
        matchPercentage >= 50 -> "Moderate Agreement"
        matchPercentage >= 25 -> "Some Disagreement" 
        else -> "Strong Disagreement"
    }
} 