package com.example.partyofthepeople.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.VoteRecord
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControversyRadarScreen(
    userProfile: UserProfile,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Calculate controversial rating
    val positiveCount = userProfile.stats.positiveInteractions
    val negativeCount = userProfile.voteHistory.count { !it.supported } 
    val totalInteractions = positiveCount + negativeCount
    
    // Calculate controversy index (0-100 scale)
    // If almost all interactions are either positive or negative, it's more controversial
    // If there's a balance, it's less controversial
    val controversyIndex = if (totalInteractions > 0) {
        val positiveRatio = positiveCount.toFloat() / totalInteractions
        // Convert to 0-100 scale where 50 is balanced, 0 or 100 is controversial
        val balanceScore = (2 * kotlin.math.abs(positiveRatio - 0.5)) * 100
        balanceScore.roundToInt()
    } else {
        0
    }
    
    // Calculate ratio for display
    val positiveRatio = if (totalInteractions > 0) {
        (positiveCount.toFloat() / totalInteractions) * 100
    } else {
        50f
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controversy Radar") },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Explanation card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF457B9D)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "YOUR CONTROVERSY RADAR",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "See how your voting patterns align with others. A balanced citizen considers different viewpoints, while a controversial one tends to take strong positions.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            
            // Main radar display
            ControversyRadar(
                positivePercentage = positiveRatio.toInt(),
                controversyIndex = controversyIndex
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Interaction stats
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A4A73)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "INTERACTION BREAKDOWN",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        InteractionStatColumn(
                            label = "Positive",
                            value = positiveCount.toString(),
                            color = Color(0xFF4CAF50)
                        )
                        
                        InteractionStatColumn(
                            label = "Negative",
                            value = negativeCount.toString(),
                            color = Color(0xFFE63946)
                        )
                        
                        InteractionStatColumn(
                            label = "Total",
                            value = totalInteractions.toString(),
                            color = Color(0xFFFFD700)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Category breakdown
            VoteCategoryBreakdown(userProfile.voteHistory)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Controversy explanation
            ControversyExplanationCard(controversyIndex)
        }
    }
}

@Composable
fun ControversyRadar(
    positivePercentage: Int,
    controversyIndex: Int
) {
    val animatedPositivePercentage by animateFloatAsState(
        targetValue = positivePercentage.toFloat(),
        animationSpec = tween(1000),
        label = "PositivePercentage"
    )
    
    val animatedControversyIndex by animateFloatAsState(
        targetValue = controversyIndex.toFloat(),
        animationSpec = tween(1000),
        label = "ControversyIndex"
    )
    
    // Colors based on controversy index
    val radarColor = when {
        controversyIndex < 30 -> Color(0xFF4CAF50) // Green for balanced
        controversyIndex < 70 -> Color(0xFFFFC107) // Yellow for moderate
        else -> Color(0xFFE63946) // Red for controversial
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main gauge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp)
        ) {
            // Draw background
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background circle
                drawArc(
                    color = Color(0xFF1D3557),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                    size = Size(size.width * 0.8f, size.height * 0.8f),
                    style = Stroke(width = 30f, cap = StrokeCap.Round)
                )
                
                // Indicator
                drawArc(
                    color = radarColor,
                    startAngle = 135f,
                    sweepAngle = (270f * animatedControversyIndex / 100f),
                    useCenter = false,
                    topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                    size = Size(size.width * 0.8f, size.height * 0.8f),
                    style = Stroke(width = 30f, cap = StrokeCap.Round)
                )
            }
            
            // Center text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${animatedControversyIndex.roundToInt()}%",
                    color = radarColor,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = when {
                        controversyIndex < 30 -> "Balanced"
                        controversyIndex < 70 -> "Moderate"
                        else -> "Controversial"
                    },
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        
        // Positive/Negative ratio indicator
        RatioIndicator(
            positivePercentage = animatedPositivePercentage.roundToInt()
        )
    }
}

@Composable
fun RatioIndicator(positivePercentage: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "POSITIVE/NEGATIVE RATIO",
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Color(0xFF2A4A73))
        ) {
            // Positive bar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(positivePercentage.toFloat())
                    .background(Color(0xFF4CAF50))
            ) {
                if (positivePercentage > 15) {
                    Text(
                        text = "$positivePercentage%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Negative bar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((100 - positivePercentage).toFloat())
                    .background(Color(0xFFE63946))
            ) {
                if (positivePercentage < 85) {
                    Text(
                        text = "${100 - positivePercentage}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
        
        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "Positive",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color(0xFFE63946), CircleShape)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "Negative",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun InteractionStatColumn(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
                .border(2.dp, color, CircleShape)
        ) {
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

@Composable
fun VoteCategoryBreakdown(voteHistory: List<VoteRecord>) {
    if (voteHistory.isEmpty()) return
    
    // Group votes by category
    val categoryVotes = voteHistory.groupBy { it.category }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2A4A73)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "CATEGORY CONTROVERSY",
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            categoryVotes.forEach { (category, votes) ->
                val positiveCount = votes.count { it.supported }
                val totalCount = votes.size
                val positiveRatio = if (totalCount > 0) {
                    (positiveCount.toFloat() / totalCount) * 100
                } else 0f
                
                CategoryBar(
                    category = category,
                    positivePercentage = positiveRatio.roundToInt(),
                    totalVotes = totalCount
                )
                
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryBar(
    category: String,
    positivePercentage: Int,
    totalVotes: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = category,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "$totalVotes votes",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1D3557))
        ) {
            // Positive bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(positivePercentage.toFloat())
                    .background(Color(0xFF4CAF50))
            )
            
            // Negative bar
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((100 - positivePercentage).toFloat())
                    .background(Color(0xFFE63946))
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "$positivePercentage% Support",
                color = Color(0xFF4CAF50),
                fontSize = 12.sp
            )
            
            Text(
                text = "${100 - positivePercentage}% Oppose",
                color = Color(0xFFE63946),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun ControversyExplanationCard(controversyIndex: Int) {
    val (title, description) = when {
        controversyIndex < 30 -> Pair(
            "Balanced Citizen", 
            "You tend to consider perspectives from both sides. Your voting patterns indicate you're a moderate who weighs issues on their merits rather than partisan lines."
        )
        controversyIndex < 70 -> Pair(
            "Moderate Patriot", 
            "You have some strong positions but still maintain flexibility on certain issues. You're neither purely partisan nor entirely neutral."
        )
        else -> Pair(
            "Controversial Advocate", 
            "You take strong positions on most issues. Your voting patterns suggest you have clear and consistent political views that you advocate for passionately."
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                controversyIndex < 30 -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                controversyIndex < 70 -> Color(0xFFFFC107).copy(alpha = 0.2f)
                else -> Color(0xFFE63946).copy(alpha = 0.2f)
            }
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = description,
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
} 