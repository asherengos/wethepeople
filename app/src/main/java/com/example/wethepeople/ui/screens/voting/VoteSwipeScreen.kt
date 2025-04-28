package com.example.wethepeople.ui.screens.voting

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wethepeople.ui.theme.*
import com.example.wethepeople.ui.screens.voting.VoteSwipeViewModel
import com.example.wethepeople.navigation.Screen
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import kotlin.math.abs
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import com.example.wethepeople.ui.screens.voting.SwipeableCard
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.wethepeople.ui.icons.CustomIcons
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import com.example.wethepeople.R
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_white
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.ArtySignatureFontFamily
import com.example.wethepeople.ui.screens.voting.Vote

@Composable
private fun getDrawableResourceByName(name: String): Int {
    val context = LocalContext.current
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteSwipeScreen(
    appNavController: NavController,
    viewModel: VoteSwipeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(patriot_dark_blue)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Tags Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hot Topics Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "HOT TOPICS",
                        color = patriot_gold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "🔥", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Scrollable Hot Topics
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.getHotTopics()) { topic ->
                        Surface(
                                shape = RoundedCornerShape(8.dp),
                            color = patriot_gold.copy(alpha = 0.15f),
                            contentColor = patriot_gold,
                            modifier = Modifier.clickable { /* TODO: Filter by hot topic */ }
                        ) {
                            Text(
                                text = topic,
                                    fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Categories Section
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                Text(
                    text = "CATEGORIES",
                    color = patriot_white,
                        fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Scrollable Categories
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    items(viewModel.getCategories()) { category ->
                        Surface(
                                shape = RoundedCornerShape(8.dp),
                            color = patriot_white.copy(alpha = 0.1f),
                            contentColor = patriot_white,
                            modifier = Modifier.clickable { /* TODO: Filter by category */ }
                        ) {
                            Text(
                                text = category,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            }
                        }
                    }
                }
            }

            // Cards Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Background cards
                viewModel.upcomingCards.reversed().forEachIndexed { index, upcomingCard ->
                    val scale = 1f - ((index + 1) * 0.04f)
                    val offsetY = ((index + 1) * 20).dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .aspectRatio(0.7f)
                            .scale(scale)
                            .offset(y = offsetY)
                    ) {
                        VotingCardContent(card = upcomingCard, isBackground = true)
                    }
                }

                // Current card
                viewModel.currentCard?.let { card ->
                    key(card.id) {
                        SwipeableCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .aspectRatio(0.7f),
                            onSwipeComplete = { swipedRight ->
                                viewModel.onCardSwiped(swipedRight)
                            },
                            onCardClick = { viewModel.onCardClicked(card) }
                        ) { swipeProgress, rotation ->
                            VotingCardContent(
                                card = card,
                                isBackground = false,
                                swipeProgress = swipeProgress,
                                rotation = rotation,
                                onKnowMoreClick = {
                                    viewModel.onCardClicked(card)
                                }
                            )
                        }
                    }
                } ?: run {
                    // No cards left view
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Voting Complete",
                            tint = patriot_blue_light,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "YOU'VE DONE YOUR PART!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = patriot_white,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You've voted on all available proposals. Check back later for more civic duties!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = patriot_white.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { appNavController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Voting.route) { inclusive = true }
                            } },
                            colors = ButtonDefaults.buttonColors(containerColor = patriot_medium_blue)
                        ) {
                            Text("BACK TO HEADQUARTERS", color = patriot_white)
                        }
                    }
                }
            }

            // Vote History Bar
            VoteHistoryBar(
                votes = viewModel.voteHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Dialogs
        if (viewModel.showDetailDialog && viewModel.selectedCard != null) {
            VoteDetailDialog(
                card = viewModel.selectedCard!!,
                onDismiss = { viewModel.onDismissDialog() },
                onJoinDebate = { viewModel.onDismissDialog() },
                _appNavController = appNavController
            )
        }

        if (viewModel.showFinalReview) {
            FinalReviewDialog(
                votes = viewModel.voteHistory,
                onDismiss = { viewModel.onFinalReviewDismissed() },
                onVoteChanged = { index, isSupport -> viewModel.updateVote(index, isSupport) },
                onSubmit = { viewModel.submitFinalVotes() }
            )
        }
    }
}

@Composable
fun VotingCardContent(
    card: VotingCard,
    isBackground: Boolean,
    swipeProgress: Float = 0f,
    rotation: Float = 0f,
    onKnowMoreClick: (() -> Unit)? = null
) {
    val cardBackgroundColor = when (card.category) {
        "Environment" -> category_environment_bg
        "Economy" -> category_economy_bg
        "Education" -> category_education_bg
        "Public Safety" -> category_public_safety_bg
        "Electoral Reform" -> category_electoral_reform_bg
        "Infrastructure" -> category_infrastructure_bg
        else -> category_default_bg
    }

    // Animate swipe hints
    val hintAlpha by animateFloatAsState(
        targetValue = if (isBackground || abs(swipeProgress) > 0.1f) 0f else 0.15f,
        animationSpec = tween(1000)
    )

    // Eagley color state based on swipe
    val eagleyColor = when {
        swipeProgress > 0.1f -> patriot_blue_light
        swipeProgress < -0.1f -> patriot_red
        else -> patriot_white
    }

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackgroundColor
        ),
        border = if (card.isHot && !isBackground) BorderStroke(2.dp, patriot_gold) else null
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Left swipe hint
            Text(
                text = "← Oppose",
                color = patriot_red,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .alpha(hintAlpha)
            )

            // Right swipe hint
            Text(
                text = "Support →",
                color = patriot_blue,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .alpha(hintAlpha)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Card header and content
                Column {
                     Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val categoryIcon = when (card.category) {
                                 "Environment" -> Icons.Filled.Forest
                                "Economy" -> Icons.Filled.AccountBalance
                                "Education" -> Icons.Filled.School
                                "Public Safety" -> Icons.Filled.LocalPolice
                                "Electoral Reform" -> Icons.Filled.HowToVote
                                "Infrastructure" -> Icons.Filled.Construction
                                else -> Icons.Filled.Gavel
                            }
                            Icon(
                                imageVector = categoryIcon,
                                contentDescription = card.category,
                                tint = patriot_gold,
                                modifier = Modifier.size(16.dp).padding(end = 4.dp)
                            )
                            Text(
                                text = card.category,
                                color = patriot_gold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Sponsored by: ${card.sponsor}",
                            color = patriot_gold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Title with VoteCardSubject font
                        Text(
                            text = card.title,
                            fontFamily = VoteCardSubjectFontFamily,
                            fontSize = 38.sp,
                            color = patriot_white,
                            textAlign = TextAlign.Center,
                            lineHeight = 42.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = card.description,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = patriot_white,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (card.isHot) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = patriot_gold.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, patriot_gold)
                            ) {
                                Text(
                                    text = "🔥 Hot Topic",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = patriot_gold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Footer with Eagley and text
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Eagley image with voter state
                    val voterImage = when {
                        swipeProgress > 0.1f -> R.drawable.right_wing_voter
                        swipeProgress < -0.1f -> R.drawable.left_wing_voter
                        else -> R.drawable.undecided_voter
                    }
                    Image(
                        painter = painterResource(id = voterImage),
                        contentDescription = "Voter mascot",
                        modifier = Modifier
                            .size(100.dp)  // Slightly smaller size
                            .padding(vertical = 12.dp)
                            .graphicsLayer(alpha = 0.75f),  // More transparency
                        contentScale = ContentScale.Fit
                    )

                    // Footer text
                    if (!isBackground && onKnowMoreClick != null) {
                        Text(
                            text = "Swipe to vote • Tap for details",
                            color = patriot_white.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .clickable(onClick = onKnowMoreClick)
                                .padding(bottom = 8.dp)
                        )
                    }
                }
            }

            // Feedback overlays
            if (!isBackground) {
            val stampAlpha = (abs(swipeProgress) * 2f).coerceIn(0f, 0.8f)
            val feedbackIconAlpha = (abs(swipeProgress) * 1.5f - 0.3f).coerceIn(0f, 0.7f)
            val feedbackIconScale = (1f + abs(swipeProgress) * 0.3f).coerceAtMost(1.3f)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                 Text(
                    text = "SUPPORTED",
                    color = Color.Green.copy(alpha = stampAlpha * (if(swipeProgress > 0) 1f else 0f)),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .border(2.dp, Color.Green.copy(alpha = stampAlpha * (if(swipeProgress > 0) 1f else 0f)), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                 )
                 Text(
                    text = "OPPOSED",
                    color = Color.Red.copy(alpha = stampAlpha * (if(swipeProgress < 0) 1f else 0f)),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                        .graphicsLayer { rotationZ = rotation }
                        .border(2.dp, Color.Red.copy(alpha = stampAlpha * (if(swipeProgress < 0) 1f else 0f)), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                 )

                 Icon(
                    imageVector = Icons.Filled.ThumbUp,
                    contentDescription = "Vote Yes",
                    tint = Color.Green.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(100.dp)
                        .alpha(if (swipeProgress > 0.1) feedbackIconAlpha else 0f)
                        .scale(if (swipeProgress > 0.1) feedbackIconScale else 1f)
                 )
                 Icon(
                    imageVector = Icons.Filled.ThumbDown,
                    contentDescription = "Vote No",
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(100.dp)
                        .alpha(if (swipeProgress < -0.1) feedbackIconAlpha else 0f)
                        .scale(if (swipeProgress < -0.1) feedbackIconScale else 1f)
                 )
                }
            }
        }
    }
}

@Composable
fun VoteHistoryBar(
    votes: List<Vote>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(10) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .border(
                        width = 1.dp,
                        color = if (index < votes.size) Color.Transparent else patriot_white.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .background(
                        color = when {
                            index >= votes.size -> Color.Transparent
                            votes[index].isSupport -> Color(0xFF4CAF50).copy(alpha = 0.8f)
                            else -> Color(0xFFF44336).copy(alpha = 0.8f)
                        },
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (index < votes.size) {
                    Icon(
                        imageVector = if (votes[index].isSupport) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                        contentDescription = if (votes[index].isSupport) "Support" else "Oppose",
                        tint = patriot_white,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FinalReviewDialog(
    votes: List<Vote>,
    onDismiss: () -> Unit,
    onVoteChanged: (Int, Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = patriot_medium_blue)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "FINAL REVIEW",
                    style = MaterialTheme.typography.headlineMedium,
                    color = patriot_white,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Review your votes before submitting. Tap any vote to change it.",
                    color = patriot_white.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Vote list
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    votes.forEachIndexed { index, vote ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(patriot_dark_blue.copy(alpha = 0.5f))
                                .clickable { onVoteChanged(index, !vote.isSupport) }
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = vote.title,
                                color = patriot_white,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (vote.isSupport) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                                contentDescription = if (vote.isSupport) "Support" else "Oppose",
                                tint = if (vote.isSupport) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = patriot_dark_blue)
                    ) {
                        Text("KEEP VOTING")
                    }
                    Button(
                        onClick = onSubmit,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = patriot_gold)
                    ) {
                        Text("SUBMIT VOTES", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomActionButton(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: Color,
    backgroundColor: Color,
    buttonSize: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(buttonSize)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
} 