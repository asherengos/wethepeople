package com.example.wethepeople.ui.screens.voting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wethepeople.ui.theme.*

// Simple data class to hold proposal information
data class Proposal(
    val id: String,
    val title: String,
    val description: String,
    val sponsor: String? = null,
    // Add other relevant fields like counts, tags etc. later
)

@Composable
fun SwipeableVoteCard(
    proposal: Proposal,
    _onVote: (Boolean) -> Unit, // Added underscore
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Add some padding
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = patriot_medium_blue) // Use a theme color
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding inside the card
        ) {
            Text(
                text = proposal.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = patriot_white
            )
            Spacer(modifier = Modifier.height(8.dp))
            proposal.sponsor?.let {
                Text(
                    text = "Sponsored by: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = patriot_gold // Example color
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = proposal.description,
                style = MaterialTheme.typography.bodyMedium,
                color = patriot_white.copy(alpha = 0.9f) // Slightly less prominent
            )
            // We will add swipe detection and overlays here later
        }
    }
}

// TODO: Add Preview function for easier development 