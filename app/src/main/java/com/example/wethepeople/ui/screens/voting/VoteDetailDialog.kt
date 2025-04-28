package com.example.wethepeople.ui.screens.voting

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.theme.*
import com.example.wethepeople.ui.screens.voting.VoteSwipeViewModel
import com.example.wethepeople.ui.icons.CustomIcons
import kotlin.math.max

data class VotingCardTag(
    val name: String,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteDetailDialog(
    _appNavController: NavController,
    card: VotingCard,
    onDismiss: () -> Unit,
    onJoinDebate: () -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var selectedChip by remember { mutableStateOf<String?>("SUPPORT") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = patriot_medium_blue
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // --- Header Section --- 
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.size(40.dp))
                    Text(
                        text = "PROPOSAL DETAILS",
                        color = patriot_white,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = patriot_white.copy(alpha = 0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                 // --- Body Section (White Background & Bordered) ---
                 Column(
                     modifier = Modifier
                         .padding(horizontal = 16.dp)
                         .background(Color.White, RoundedCornerShape(12.dp))
                         .border(
                             BorderStroke(1.dp, patriot_gold.copy(alpha = 0.3f)),
                             RoundedCornerShape(12.dp)
                          )
                         .padding(16.dp),
                     horizontalAlignment = Alignment.Start
                 ) {
                    // Category and Sponsor Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                         val categoryColor = if (card.category == "Economy") category_economy_yellow else patriot_gold
                         val categoryEmoji = when (card.category) {
                             "Economy" -> "💰"
                             "Environment" -> "🌱"
                             "Education" -> "🎓"
                             "Public Safety" -> "🛡️"
                             "Electoral Reform" -> "🗳️"
                             "Infrastructure" -> "🏗️"
                             else -> "🏛️"
                         }
                         Surface(
                            onClick = { /* No action */ },
                            shape = RoundedCornerShape(16.dp),
                            color = categoryColor.copy(alpha = 0.15f),
                            contentColor = categoryColor.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.7f))
                         ) { 
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) { 
                                Text(categoryEmoji, modifier = Modifier.padding(end = 4.dp))
                                Text(card.category, fontSize = 12.sp, fontWeight = FontWeight.Bold) 
                            }
                         }
                        
                        Surface(
                            onClick = { /* No action */ },
                            shape = RoundedCornerShape(16.dp),
                            color = patriot_gold.copy(alpha = 0.15f),
                            contentColor = patriot_gold.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, patriot_gold.copy(alpha = 0.7f))
                        ) {
                            Text(
                                text = "Sponsor: ${card.sponsor}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)

                    // Title
                    Text(
                        text = card.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = patriot_dark_blue,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Description & More Info Button
                    Column(modifier = Modifier.animateContentSize()) {
                         Text(
                            text = card.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = patriot_dark_gray,
                            textAlign = TextAlign.Start,
                            maxLines = if (isDescriptionExpanded) Int.MAX_VALUE else 5,
                            overflow = TextOverflow.Clip
                        )
                         if (card.description.length > 150) {
                             Spacer(modifier = Modifier.height(8.dp))
                             Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedIconButton(
                                    onClick = { isDescriptionExpanded = !isDescriptionExpanded },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterEnd),
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, patriot_blue_light)
                                ) {
                                    Icon(
                                        imageVector = if (isDescriptionExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.MoreHoriz, 
                                        contentDescription = if (isDescriptionExpanded) "Collapse info" else "More info",
                                        tint = patriot_blue_light,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                             }
                         }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Tags
                    Text(
                        text = "TAGS:",
                        color = patriot_dark_blue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                         card.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = patriot_dark_blue.copy(alpha = 0.05f),
                                contentColor = patriot_dark_blue.copy(alpha = 0.8f)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                         }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // Vote Bar
                    VoteTugOfWarBar(upvotes = card.upvotes, downvotes = card.downvotes)

                    // Vote Breakdown Button (Navigates)
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                        OutlinedIconButton(
                             onClick = { 
                                 onDismiss()
                                 // Comment out the navigation to non-existent screen
                                 // navController.navigate(com.example.wethepeople.navigation.Screen.Analytics.withArgs(card.id))
                             },
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterEnd),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Icon(
                                Icons.Filled.MoreHoriz,
                                contentDescription = "Vote breakdown",
                                tint = Color.Gray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                 // --- Footer Action Buttons --- 
                 Column(modifier = Modifier.padding(16.dp)) {
                     Button(
                        onClick = { onJoinDebate() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = patriot_blue_light
                        )
                    ) {
                        Text("💬 JOIN DEBATE")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                         shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = patriot_red_bright
                        )
                    ) {
                        Text("🗳️ BACK TO VOTING")
                    }
                 }
            }
        }
    }
}

// Helper composable for the vote bar
@Composable
fun VoteTugOfWarBar(upvotes: Int, downvotes: Int) {
    val totalVotes = max(1, upvotes + downvotes) // Avoid division by zero
    val supportPercentage = upvotes.toFloat() / totalVotes
    val opposePercentage = downvotes.toFloat() / totalVotes

    val barHeight = 40.dp
    val cornerRadius = 8.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.DarkGray) // Background for the bar area
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Oppose Section (Red)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(opposePercentage)
                    .background(patriot_red_bright)
            )
            // Support Section (Green)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(supportPercentage)
                    .background(patriot_green_support)
            )
        }
        // Overlay Text (Votes)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp), // Padding for text inside the bar
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = downvotes.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
             Text(
                text = upvotes.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
} 