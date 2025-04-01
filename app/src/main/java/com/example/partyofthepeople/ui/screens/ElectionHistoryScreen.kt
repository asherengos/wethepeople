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
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.VoteRecord
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectionHistoryScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit
) {
    val voteHistory = userProfile.voteHistory
    
    // Track selected filter category
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All") + voteHistory.map { it.category }.distinct().sorted()
    
    // Filter votes by selected category
    val filteredVotes = if (selectedCategory == "All") {
        voteHistory
    } else {
        voteHistory.filter { it.category == selectedCategory }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Election History") },
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
            // Summary card
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
                        text = "YOUR VOTING RECORD",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Review your contributions to democracy! Your votes shape the nation's future.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Voting stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        VotingStat(
                            label = "Total Votes",
                            value = voteHistory.size.toString()
                        )
                        
                        VotingStat(
                            label = "Supported",
                            value = voteHistory.count { it.supported }.toString(),
                            color = Color(0xFF4CAF50)
                        )
                        
                        VotingStat(
                            label = "Opposed",
                            value = voteHistory.count { !it.supported }.toString(),
                            color = Color(0xFFE63946)
                        )
                    }
                }
            }
            
            // Category filter
            if (categories.size > 1) {
                Text(
                    text = "FILTER BY CATEGORY",
                    color = Color(0xFFFFD700),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 48.dp)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                CategoryChip(
                                    category = category,
                                    selected = category == selectedCategory,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }
                }
            }
            
            // Vote list
            if (filteredVotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (voteHistory.isEmpty()) {
                            "No voting history yet. Cast your first vote!"
                        } else {
                            "No votes in this category"
                        },
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Text(
                    text = "YOUR VOTES",
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
                    items(filteredVotes.sortedByDescending { it.timestamp }) { vote ->
                        VoteHistoryCard(vote = vote)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VotingStat(
    label: String,
    value: String,
    color: Color = Color(0xFFFFD700)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun CategoryChip(
    category: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Color(0xFF457B9D) else Color(0xFF2A4A73)
    val borderColor = if (selected) Color(0xFFFFD700) else Color.Transparent
    
    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.height(36.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = Color.White
            )
        }
    }
}

@Composable
fun VoteHistoryCard(vote: VoteRecord) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(vote.timestamp))
    
    val backgroundColor = if (vote.supported) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        Color(0xFFE63946).copy(alpha = 0.1f)
    }
    
    val borderColor = if (vote.supported) {
        Color(0xFF4CAF50)
    } else {
        Color(0xFFE63946)
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A4A73)
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Vote indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .border(2.dp, borderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (vote.supported) "✓" else "✗",
                    color = borderColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vote.proposalTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = vote.category,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    
                    Text(
                        text = formattedDate,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
} 