package com.example.wethepeople.ui.screens.voting

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.R
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.patriot_white
import com.example.wethepeople.ui.theme.patriot_blue_light
import com.example.wethepeople.navigation.Screen
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wethepeople.viewmodels.VotingViewModel

@Composable
fun VotingScreen(
    appNavController: NavController,
    viewModel: VotingViewModel = hiltViewModel()
) {
    // Temporarily comment out as viewModel.proposals doesn't exist yet
    // val proposals = viewModel.proposals.collectAsState().value
    
    Column(
        modifier = Modifier
            .background(patriot_dark_blue)
            .padding(16.dp) // Keep internal padding
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FREEDOM THROUGH VOTING!",
            color = patriot_white.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        
        // Welcome message
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = patriot_dark_blue
            ),
            border = BorderStroke(1.dp, patriot_medium_blue)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "WELCOME BACK, !",
                    color = patriot_white,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "CITIZEN: Your vote is your weapon! Deploy it for DEMOCRACY!",
                    color = patriot_gold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                // Points display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PointCounter(label = "PATRIOT PTS", count = "0")
                    PointCounter(label = "$", count = "0")
                    PointCounter(label = "LIBERTY CREDITS", count = "0")
                    PointCounter(label = "CITIZEN RANK", count = "1")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Eagle Companion Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            colors = CardDefaults.cardColors(
                containerColor = patriot_medium_blue
            ),
            border = BorderStroke(1.dp, patriot_gold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "EAGLE COMPANION",
                    color = patriot_white,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Coming Soon!",
                    color = patriot_gold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Action Cards Section
        Text(
            text = "ACTION CENTER",
            color = patriot_white,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Left
        )
        
        // Grid of action cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Vote Now card
            ActionCardSmall(
                title = "VOTE NOW",
                icon = Icons.Default.Check,
                backgroundColor = patriot_red_bright,
                modifier = Modifier.weight(1f),
                onClick = {
                    appNavController.navigate(Screen.VoteSwipe.route)
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Debate card
            ActionCardSmall(
                title = "DEBATE",
                icon = Icons.Default.Email,
                backgroundColor = patriot_red_bright,
                modifier = Modifier.weight(1f),
                onClick = {}
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Patriot Missions card
        ActionCardSmall(
            title = "PATRIOT MISSIONS",
            icon = Icons.Default.Star,
            backgroundColor = patriot_medium_blue,
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Battlefield section
        Text(
            text = "BATTLEFIELD OF DEBATES",
            color = patriot_white,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            textAlign = TextAlign.Left
        )
        
        // Debate Topic Card (Example)
        DebateTopicCard(
            title = "Tax Evasion: Treason or Super-Treason?",
            authorName = "CITIZEN SQUARE",
            timeAgo = "2 hours ago"
        )
        
        Spacer(modifier = Modifier.height(10.dp))
        
        // Second debate topic
        DebateTopicCard(
            title = "Should Voting Be Mandatory For All Citizens?",
            authorName = "CITIZEN SQUARE", 
            timeAgo = "5 hours ago"
        )
        
        // Breaking news section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 80.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(1.dp, patriot_blue_light)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📰 Breaking News:",
                    color = patriot_red_bright,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Welcome to Party of the People!",
                    color = patriot_white,
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PointCounter(label: String, count: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            color = patriot_white,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Text(
            text = label,
            color = patriot_white.copy(alpha = 0.7f),
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ActionCardSmall(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(90.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = patriot_white,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DebateTopicCard(
    title: String,
    authorName: String,
    timeAgo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Author profile icon (placeholder)
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(patriot_red_bright)
                    .align(Alignment.Start)
            ) {
                Text(
                    text = authorName.first().toString(),
                    color = patriot_white,
                    modifier = Modifier.align(Alignment.Center),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Debate title
            Text(
                text = title,
                color = patriot_white,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author and time
                Text(
                    text = "$authorName · $timeAgo",
                    color = patriot_white.copy(alpha = 0.7f),
                    fontSize = 10.sp
                )
                
                // Report button
                Button(
                    onClick = { /* Handle report */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = patriot_red_bright
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "REPORT DISSENT!",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
} 