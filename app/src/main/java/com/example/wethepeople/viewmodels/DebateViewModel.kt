package com.example.wethepeople.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Replicate sample data here temporarily
private val mockSampleCards = listOf(
    com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "1",
        title = "Universal Basic Income",
        description = "Fund: $1000/month per person to all citizens over 18, regardless of employment status.",
        category = "Economy",
        sponsor = "Rep. James Wilson",
        upvotes = 4521,
        downvotes = 4892,
        tags = listOf("Economy", "Social Welfare", "UBI")
    ),
    com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "2",
        title = "Green Energy Initiative",
        description = "Mandate transition to 100% renewable energy sources for all federal buildings by 2030.",
        category = "Environment",
        sponsor = "Senator Green",
        upvotes = 5234,
        downvotes = 3891,
        tags = listOf("Environment", "Energy", "Climate Change")
    ),
     com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "3",
        title = "Federal Education Reform",
        description = "Implement nationwide standardized testing for K-12 and allocate funds based on performance.",
        category = "Education",
        sponsor = "Sec. Education Dept.",
        upvotes = 3123,
        downvotes = 3567,
        tags = listOf("Education", "Policy", "Testing")
    ),
     com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "4",
        title = "Ban Assault Weapons",
        description = "Implement a comprehensive ban on the sale and manufacturing of assault-style weapons.",
        category = "Public Safety",
        sponsor = "Senator Sarah Chen",
        upvotes = 2890,
        downvotes = 3150,
        tags = listOf("Gun Control", "Public Safety", "Legislation")
    ),
      com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "5",
        title = "Mandatory Voting Act",
        description = "All eligible citizens must participate in federal elections or face a nominal fine. Exemptions for valid reasons.",
        category = "Electoral Reform",
        sponsor = "Senator J. Rico",
        upvotes = 1456,
        downvotes = 2345,
        tags = listOf("Voting", "Rights", "Democracy")
    ),
     com.example.wethepeople.ui.screens.voting.VotingCard(
        id = "6",
        title = "Infrastructure Investment Plan",
        description = "Allocate $1 Trillion over 10 years to modernize roads, bridges, and public transport networks nationwide.",
        category = "Infrastructure",
        sponsor = "Dept. Transportation",
        upvotes = 7105,
        downvotes = 1988,
        tags = listOf("Infrastructure", "Economy", "Transport")
    )
)

// Data class for Debate Topic details (placeholder)
data class DebateTopicDetails(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val sponsor: String,
    val argumentsFor: List<String> = emptyList(), // Placeholder
    val argumentsAgainst: List<String> = emptyList(), // Placeholder
    val comments: List<String> = emptyList() // Placeholder
)

// Define possible UI states
sealed class DebateUiState {
    object Loading : DebateUiState()
    data class Success(val details: DebateTopicDetails) : DebateUiState()
    data class Error(val message: String) : DebateUiState()
}

@HiltViewModel
class DebateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
    // TODO: Inject Repository here later
) : ViewModel() {

    private val cardId: String? = savedStateHandle.get("cardId")

    private val _uiState = MutableStateFlow<DebateUiState>(DebateUiState.Loading)
    val uiState: StateFlow<DebateUiState> = _uiState

    init {
        fetchDebateDetails()
    }

    private fun fetchDebateDetails() {
        viewModelScope.launch {
            _uiState.value = DebateUiState.Loading
            if (cardId == null) {
                _uiState.value = DebateUiState.Error("Topic ID not found")
                return@launch
            }

            // --- Mock Data Fetch --- 
            kotlinx.coroutines.delay(500) // Simulate network delay
            // Find the corresponding card data from the local mock list
            val mockCard = mockSampleCards.find { card -> card.id == cardId } 

            if (mockCard != null) {
                _uiState.value = DebateUiState.Success(
                    DebateTopicDetails(
                        id = mockCard.id,
                        title = mockCard.title,
                        description = mockCard.description,
                        category = mockCard.category,
                        sponsor = mockCard.sponsor,
                        argumentsFor = listOf("Point A for", "Point B for"),
                        argumentsAgainst = listOf("Point X against", "Point Y against"),
                        comments = listOf("Comment 1", "Comment 2", "Comment 3")
                    )
                )
            } else {
                _uiState.value = DebateUiState.Error("Topic details not found for ID: $cardId")
            }
            // --- End Mock Data Fetch ---
        }
    }

    // TODO: Add functions for adding comments, arguments, etc.
} 