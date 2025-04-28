package com.example.wethepeople.ui.screens.debate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.wethepeople.ui.theme.Blue700
import com.example.wethepeople.ui.theme.Red700

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebateDetailScreen(
    navController: NavController,
    debateTitle: String,
    debateDescription: String,
    viewModel: DebateViewModel = viewModel()
) {
    val comments by viewModel.comments.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Debate Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share functionality */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Debate title and content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = debateTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = debateDescription,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Quick opinion poll
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        var leftSelected by remember { mutableStateOf(false) }
                        var rightSelected by remember { mutableStateOf(false) }
                        
                        Button(
                            onClick = { 
                                leftSelected = !leftSelected
                                if (leftSelected) rightSelected = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (leftSelected) Blue700 else MaterialTheme.colorScheme.surface,
                                contentColor = if (leftSelected) Color.White else Blue700
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Agree")
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = { 
                                rightSelected = !rightSelected
                                if (rightSelected) leftSelected = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (rightSelected) Red700 else MaterialTheme.colorScheme.surface,
                                contentColor = if (rightSelected) Color.White else Red700
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Disagree")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Poll results
                    val agreePercentage = 65 // Mock data
                    val disagreePercentage = 35 // Mock data
                    
                    Text(
                        text = "Current Results:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .background(Color.LightGray, RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(agreePercentage / 100f)
                                .background(Blue700, RoundedCornerShape(4.dp))
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$agreePercentage%",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                            
                            Text(
                                text = "$disagreePercentage%",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    Text(
                        text = "Based on ${agreePercentage + disagreePercentage} votes",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Comments section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                CommentsSection(
                    comments = comments,
                    onAddComment = { content -> viewModel.addComment(content) },
                    onLikeComment = { comment -> viewModel.likeComment(comment) },
                    onDislikeComment = { comment -> viewModel.dislikeComment(comment) },
                    onFlagComment = { comment -> viewModel.flagComment(comment) }
                )
            }
        }
    }
} 