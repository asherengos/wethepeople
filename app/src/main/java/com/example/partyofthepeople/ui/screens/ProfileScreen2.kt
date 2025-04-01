package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.partyofthepeople.Achievement
import com.example.partyofthepeople.UserProfile
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen2(
    profile: UserProfile,
    onBackClick: () -> Unit,
    onViewAllAchievements: () -> Unit,
    onViewBipartisanMatch: () -> Unit,
    onViewElectionHistory: () -> Unit,
    onViewControversyRadar: () -> Unit,
    onCustomizeProfile: () -> Unit,
    onManageTitles: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFF1D3557))
            .padding(16.dp)
    ) {
        // Profile Header
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF457B9D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1D3557),
                    modifier = Modifier.size(120.dp)
                ) {
                    Text(
                        text = "🦅",
                        fontSize = 64.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Username and Rank
                Text(
                    text = profile.username,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = profile.politicalRank,
                    color = Color(0xFFFFD700),
                    fontSize = 18.sp
                )

                // District
                Text(
                    text = "District: ${profile.district}",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF457B9D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PATRIOT STATS",
                    color = Color(0xFFFFD700),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Votes Cast: ${profile.stats.votesCast}", color = Color.White)
                Text("Laws Passed: ${profile.stats.lawsPassed}", color = Color.White)
                Text("Streak Days: ${profile.stats.streakDays}", color = Color.White)
                Text("Freedom Bucks: ${profile.freedomBucks}", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Button(
            onClick = onViewAllAchievements,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("VIEW ACHIEVEMENTS 🏆")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onViewBipartisanMatch,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BIPARTISAN MATCH 🤝")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onViewElectionHistory,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ELECTION HISTORY 📊")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onViewControversyRadar,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONTROVERSY RADAR 🔍")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onCustomizeProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CUSTOMIZE PROFILE 🎨")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onManageTitles,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF795548)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("MANAGE TITLES 👑")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Back button
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63946)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("BACK TO HEADQUARTERS 🦅")
        }
    }
}

@Composable
fun ProfileHeader(profile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFF457B9D)),
            contentAlignment = Alignment.Center
        ) {
            if (profile.avatar.isNotEmpty()) {
                AsyncImage(
                    model = profile.avatar,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "🦅",
                    fontSize = 48.sp,
                    color = Color.White
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Username
        Text(
            text = profile.username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        // Political Rank
        Text(
            text = profile.politicalRank,
            fontSize = 18.sp,
            color = Color(0xFFFFD700)
        )
        
        // Join Date
        val joinDate = try {
            val timestamp = profile.joinDate.toLongOrNull() ?: 0
            val date = Date(timestamp)
            SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(date)
        } catch (e: Exception) {
            "Unknown join date"
        }
        
        Text(
            text = "Joined $joinDate",
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Freedom Bucks
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Freedom Bucks",
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${profile.freedomBucks} FB",
                fontSize = 18.sp,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PowerScoreCard(profile: UserProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF457B9D)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "POWER SCORE",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = profile.stats.powerScore.toString(),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Based on participation and positive interactions",
                fontSize = 12.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BipartisanMatchButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "🤝",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VIEW BIPARTISAN MATCH %",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ElectionHistoryButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF457B9D)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VIEW ELECTION HISTORY",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ControversyRadarButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFC107)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "📊",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "VIEW CONTROVERSY RADAR",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CustomizeProfileButton(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9C27B0)
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text = "✨",
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CUSTOMIZE PROFILE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StatsSection(profile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "STATS",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A4A73)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                StatRow("Votes Cast", profile.stats.votesCast.toString())
                StatRow("Laws Passed", profile.stats.lawsPassed.toString())
                StatRow("Streak Days", profile.stats.streakDays.toString())
                StatRow("Participation", profile.stats.participation.toString())
                StatRow("Positive Interactions", profile.stats.positiveInteractions.toString())
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color(0xFFFFD700),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AchievementsSection(achievements: List<Achievement>, onViewAllClick: () -> Unit) {
    val visibleAchievements = achievements
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ACHIEVEMENTS",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Add badge count
            if (visibleAchievements.isNotEmpty()) {
                Badge(
                    containerColor = Color(0xFFFFD700),
                    contentColor = Color(0xFF1D3557)
                ) {
                    Text(
                        text = "${visibleAchievements.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
        
        if (visibleAchievements.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A4A73)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No achievements yet. Keep participating!",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2A4A73)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Show the most recent achievements (up to 3)
                    val sortedAchievements = visibleAchievements
                        .sortedByDescending { it.dateEarned }
                        .take(3)
                    
                    sortedAchievements.forEach { achievement ->
                        AchievementItem(achievement)
                    }
                    
                    // Show "View All" button if there are more than 3 achievements
                    // or if any achievements exist at all
                    if (visibleAchievements.isNotEmpty()) {
                        Button(
                            onClick = onViewAllClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF457B9D)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("VIEW ALL ACHIEVEMENTS")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Achievement icon with better styling
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF457B9D)),
            contentAlignment = Alignment.Center
        ) {
            if (achievement.icon.isNotEmpty()) {
                AsyncImage(
                    model = achievement.icon,
                    contentDescription = achievement.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "🏆",
                    fontSize = 24.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = achievement.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = achievement.description,
                fontSize = 12.sp,
                color = Color.LightGray
            )
            
            // Add date earned
            if (achievement.dateEarned > 0) {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val dateEarned = Date(achievement.dateEarned)
                
                Text(
                    text = "Earned: ${dateFormat.format(dateEarned)}",
                    fontSize = 10.sp,
                    color = Color(0xFFFFD700)
                )
            }
        }
    }
}

@Composable
fun TitlesSection(titles: List<String>, onManageTitles: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TITLES",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Button(
                onClick = onManageTitles,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF457B9D)
                )
            ) {
                Text("Manage Titles")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A4A73)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            if (titles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No titles earned yet",
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    titles.forEach { title ->
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            color = Color(0xFFFFD700),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
} 