package com.example.wethepeople.ui.screens.voting

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.components.*
import com.example.wethepeople.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingInterface(
    poll: Poll,
    onVoteSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }
    var showConfirmation by remember { mutableStateOf(false) }
    var isVoting by remember { mutableStateOf(false) }
    var voteSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CAST YOUR VOTE") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = patriot_dark_blue,
                    titleContentColor = patriot_white,
                    navigationIconContentColor = patriot_white
                )
            )
        },
        containerColor = patriot_dark_blue
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(
                visible = !showConfirmation && !voteSubmitted,
                enter = fadeIn() + slideInHorizontally(),
                exit = fadeOut() + slideOutHorizontally()
            ) {
                VotingContent(
                    poll = poll,
                    selectedOption = selectedOption,
                    onOptionSelect = { selectedOption = it },
                    onVoteClick = { showConfirmation = true },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AnimatedVisibility(
                visible = showConfirmation && !voteSubmitted,
                enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { -it })
            ) {
                ConfirmationScreen(
                    poll = poll,
                    selectedOption = selectedOption ?: "",
                    isLoading = isVoting,
                    onConfirm = {
                        isVoting = true
                        selectedOption?.let { option ->
                            onVoteSubmit(option)
                            voteSubmitted = true
                        }
                    },
                    onBack = { showConfirmation = false },
                    modifier = Modifier.fillMaxSize()
                )
            }

            AnimatedVisibility(
                visible = voteSubmitted,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                SuccessScreen(
                    onDone = onBackClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun VotingContent(
    poll: Poll,
    selectedOption: String?,
    onOptionSelect: (String) -> Unit,
    onVoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = poll.title,
            color = patriot_white,
            fontSize = font_xl.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = poll.description,
            color = patriot_white.copy(alpha = 0.8f),
            fontSize = font_md.sp
        )

        poll.timeRemaining?.let { duration ->
            CountdownTimer(
                timeRemaining = duration,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        poll.options.forEach { option ->
            VoteOption(
                text = option,
                isSelected = option == selectedOption,
                onSelect = { onOptionSelect(option) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PatriotButton(
            text = "SUBMIT VOTE",
            onClick = onVoteClick,
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ConfirmationScreen(
    poll: Poll,
    selectedOption: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "CONFIRM YOUR VOTE",
            color = patriot_white,
            fontSize = font_xl.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "You are voting for:",
            color = patriot_white.copy(alpha = 0.8f),
            fontSize = font_md.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = selectedOption,
            color = patriot_gold,
            fontSize = font_lg.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "This action cannot be undone.",
            color = warning_yellow,
            fontSize = font_sm.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = patriot_white
                )
            ) {
                Text("GO BACK")
            }

            PatriotButton(
                text = "CONFIRM",
                onClick = onConfirm,
                isLoading = isLoading,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SuccessAnimation(
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text(
            text = "VOTE CAST SUCCESSFULLY!",
            color = patriot_white,
            fontSize = font_xl.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Thank you for participating in democracy!",
            color = patriot_gold,
            fontSize = font_md.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        PatriotButton(
            text = "DONE",
            onClick = onDone,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PollDetailContent(pollId: String) {
    // Fetch poll details based on pollId (replace with your actual data logic)
    // Commenting out because getPollById is not defined
    // val _poll = remember { getPollById(pollId) }

    Column(
        modifier = Modifier
    ) {
        // ... existing code ...
    }
} 