package com.example.wethepeople.ui.screens.voting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wethepeople.ui.components.*
import com.example.wethepeople.ui.theme.*
import java.time.Duration

data class Poll(
    val id: String,
    val title: String,
    val description: String,
    val options: List<String>,
    val votesPerOption: List<Int>,
    val timeRemaining: Duration?,
    val isCompleted: Boolean
)

@Composable
fun ActivePollsScreen(
    polls: List<Poll>,
    onVoteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ACTIVE POLLS",
                color = patriot_white,
                fontSize = font_xl.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(polls.filter { !it.isCompleted }) { poll ->
            ActivePollCard(
                poll = poll,
                onVoteClick = { onVoteClick(poll.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "COMPLETED POLLS",
                color = patriot_white,
                fontSize = font_xl.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(polls.filter { it.isCompleted }) { poll ->
            CompletedPollCard(poll = poll)
        }
    }
}

@Composable
fun ActivePollCard(
    poll: Poll,
    onVoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = patriot_medium_blue
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = poll.title,
                color = patriot_white,
                fontSize = font_lg.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = poll.description,
                color = patriot_white.copy(alpha = 0.8f),
                fontSize = font_sm.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            poll.timeRemaining?.let { duration ->
                CountdownTimer(
                    timeRemaining = duration,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            PatriotButton(
                text = "CAST YOUR VOTE",
                onClick = onVoteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
fun CompletedPollCard(
    poll: Poll,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = patriot_dark_blue
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = poll.title,
                color = patriot_white,
                fontSize = font_lg.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            poll.options.zip(poll.votesPerOption).forEach { (option, votes) ->
                val totalVotes = poll.votesPerOption.sum()
                val progress = if (totalVotes > 0) votes.toFloat() / totalVotes else 0f

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = option,
                        color = patriot_white,
                        fontSize = font_sm.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    VoteProgressBar(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "${(progress * 100).toInt()}% ($votes votes)",
                        color = patriot_gold,
                        fontSize = font_xs.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
} 