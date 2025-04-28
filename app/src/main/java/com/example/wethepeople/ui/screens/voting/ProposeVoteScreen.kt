package com.example.wethepeople.ui.screens.voting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.wethepeople.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProposeVoteScreen(
    navController: NavHostController,
    viewModel: ProposeVoteViewModel = viewModel(factory = ProposeVoteViewModel.Factory()),
    onNavigateBack: () -> Unit
) {
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submissionSuccess by viewModel.submissionSuccess.collectAsState()
    val moderationResult by viewModel.moderationResult.collectAsState()
    
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(mutableListOf("", "")) }
    var supportingEvidence by remember { mutableStateOf("") }
    
    val expanded = remember { mutableStateOf(false) }
    val categories = listOf(
        "Constitutional Rights",
        "Defense & Security",
        "Economy", 
        "Education",
        "Environment",
        "Healthcare",
        "Humor",
        "Local Issues",
        "National Policy"
    ).sorted()
    
    val scrollState = rememberScrollState()
    
    // Handle submission success
    LaunchedEffect(submissionSuccess) {
        if (submissionSuccess) {
            delay(1500)
            onNavigateBack()
            viewModel.resetSubmissionState()
        }
    }
    
    // Content moderation dialog
    if (moderationResult != null) {
        ModerationDialog(
            moderationResult = moderationResult!!,
            onDismiss = { viewModel.acknowledgeModeration() },
            onAcknowledge = { viewModel.acknowledgeModeration() }
        )
    }
    
    // Success dialog
    if (submissionSuccess) {
        Dialog(
            onDismissRequest = { },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Proposal Submitted Successfully!",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your proposal is now awaiting review.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Propose a Vote") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title section
            Text(
                text = "Title",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter proposal title") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            
            // Description section
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe your proposal") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                maxLines = 8
            )
            
            // Category dropdown
            Text(
                text = "Category",
                style = MaterialTheme.typography.titleLarge
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select a category") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (expanded.value) "Less" else "More"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded.value = !expanded.value }
                )
                
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded.value = false
                            }
                        )
                    }
                }
            }
            
            // Voting options
            Text(
                text = "Voting Options",
                style = MaterialTheme.typography.titleLarge
            )
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = option,
                        onValueChange = { newOption ->
                            options = options.toMutableList().apply {
                                this[index] = newOption
                            }
                        },
                        label = { Text("Option ${index + 1}") },
                        modifier = Modifier
                            .weight(1f)
                    )
                    
                    if (options.size > 2) {
                        IconButton(
                            onClick = {
                                options = options.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove option")
                        }
                    }
                }
            }
            
            // Add option button
            if (options.size < 5) {
                OutlinedButton(
                    onClick = {
                        options = options.toMutableList().apply {
                            add("")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add option")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add another option")
                }
            }
            
            // Supporting evidence
            Text(
                text = "Supporting Evidence (Optional)",
                style = MaterialTheme.typography.titleLarge
            )
            OutlinedTextField(
                value = supportingEvidence,
                onValueChange = { supportingEvidence = it },
                label = { Text("Links to articles, research, etc.") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 5
            )
            
            // Submit button
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.submitProposal(
                        title = title,
                        description = description,
                        category = selectedCategory,
                        options = options.filter { it.isNotBlank() },
                        supportingEvidence = supportingEvidence
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSubmitting &&
                        title.isNotBlank() &&
                        description.isNotBlank() &&
                        selectedCategory.isNotBlank() &&
                        options.count { it.isNotBlank() } >= 2
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.HowToVote,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PROPOSE YOUR VOTE NOW",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ModerationDialog(
    moderationResult: ProposeVoteViewModel.ModerationResult,
    onDismiss: () -> Unit,
    onAcknowledge: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

                Text(
                    text = "Content Moderation Required",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your proposal contains content that may be considered inappropriate or against our community guidelines. " +
                           "Please review and modify the content before submitting again.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Display categorized issues
                if (moderationResult.categorizedIssues.isNotEmpty()) {
                    Text(
                        text = "Issues Detected:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    moderationResult.categorizedIssues.forEach { (category, terms) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (category) {
                                    "Hate Speech" -> Color(0xFFFCE4EC).copy(alpha = 0.7f)
                                    "Violence" -> Color(0xFFFFEBEE).copy(alpha = 0.7f)
                                    "Profanity" -> Color(0xFFFFFDE7).copy(alpha = 0.7f)
                                    "Sexual Content" -> Color(0xFFF3E5F5).copy(alpha = 0.7f)
                                    "Harmful Content" -> Color(0xFFE8EAF6).copy(alpha = 0.7f)
                                    else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                                }
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = when (category) {
                                        "Hate Speech" -> Color(0xFFE91E63)
                                        "Violence" -> Color(0xFFF44336)
                                        "Profanity" -> Color(0xFFFFC107)
                                        "Sexual Content" -> Color(0xFF9C27B0)
                                        "Harmful Content" -> Color(0xFF3F51B5)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = terms.joinToString(", "),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                Text(
                                    text = getCategoryAdvice(category),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Text(
                    text = "Risk Score: ${(moderationResult.riskScore * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { moderationResult.riskScore },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        moderationResult.riskScore > 0.7f -> MaterialTheme.colorScheme.error
                        moderationResult.riskScore > 0.4f -> Color(0xFFF57C00) // Orange
                        else -> Color(0xFFFFB74D) // Light Orange
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tips to improve your proposal:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Text("• Replace flagged terms with more appropriate language")
                    Text("• Focus on constructive solutions and ideas")
                    Text("• Be respectful and considerate of diverse perspectives")
                    Text("• Ensure your content is relevant to civic engagement")
                    Text("• Use moderate tone and clear communication")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Edit Proposal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onAcknowledge
                    ) {
                        Text("Acknowledge & Submit Anyway")
                    }
                }
            }
        }
    }
}

@Composable
private fun getCategoryAdvice(category: String): String {
    return when (category) {
        "Hate Speech" -> "Try using inclusive and respectful language instead."
        "Violence" -> "Consider discussing solutions rather than referencing violent acts."
        "Profanity" -> "Replace with more professional language appropriate for civic discourse."
        "Sexual Content" -> "This type of content is not appropriate for a civic engagement platform."
        "Harmful Content" -> "This content may be harmful to community members and should be removed."
        else -> "Please review and modify this content."
    }
} 