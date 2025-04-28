package com.example.wethepeople.ui.screens.voting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class VotingCard(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val sponsor: String,
    val upvotes: Int,
    val downvotes: Int,
    val tags: List<String>,
    val imageUrl: String? = null
) {
    // Derived property to determine if a topic is "hot"
    val isHot: Boolean
        get() = (upvotes + downvotes) > 6000
}

data class Vote(
    val cardId: String,
    val title: String,
    val isSupport: Boolean
)

@HiltViewModel
class VoteSwipeViewModel @Inject constructor() : ViewModel() {
    
    // Use a mutable state list to track available cards
    private var _votingCards = mutableStateListOf<VotingCard>()
    val votingCards: List<VotingCard> get() = _votingCards
    
    // Track vote history
    private var _voteHistory = mutableStateListOf<Vote>()
    val voteHistory: List<Vote> get() = _voteHistory
    
    var showDetailDialog by mutableStateOf(false)
        private set
    
    var selectedCard by mutableStateOf<VotingCard?>(null)
        private set
    
    var showFinalReview by mutableStateOf(false)
        private set
    
    init {
        loadInitialCards()
    }
    
    private fun loadInitialCards() {
        // Temporary sample data - replace with actual data loading
        _votingCards.addAll(listOf(
            VotingCard(
                id = "1",
                title = "Universal Basic Income",
                description = "Fund: $1000/month per person to all citizens over 18, regardless of employment status.",
                category = "Economy",
                sponsor = "Rep. James Wilson",
                upvotes = 4521,
                downvotes = 4892,
                tags = listOf("Economy", "Social Welfare", "UBI")
            ),
            VotingCard(
                id = "2",
                title = "Green Energy Initiative",
                description = "Mandate transition to 100% renewable energy sources for all federal buildings by 2030.",
                category = "Environment",
                sponsor = "Senator Green",
                upvotes = 5234,
                downvotes = 3891,
                tags = listOf("Environment", "Energy", "Climate Change")
            ),
            VotingCard(
                id = "3",
                title = "Federal Education Reform",
                description = "Implement nationwide standardized testing for K-12 and allocate funds based on performance.",
                category = "Education",
                sponsor = "Sec. Education Dept.",
                upvotes = 3123,
                downvotes = 3567,
                tags = listOf("Education", "Policy", "Testing")
            ),
            VotingCard(
                id = "4",
                title = "Ban Assault Weapons",
                description = "Implement a comprehensive ban on the sale and manufacturing of assault-style weapons.",
                category = "Public Safety",
                sponsor = "Senator Sarah Chen",
                upvotes = 6890,
                downvotes = 7150,
                tags = listOf("Gun Control", "Public Safety", "Legislation")
            ),
            VotingCard(
                id = "5",
                title = "Mandatory Voting Act",
                description = "All eligible citizens must participate in federal elections or face a nominal fine. Exemptions for valid reasons.",
                category = "Electoral Reform",
                sponsor = "Senator J. Rico",
                upvotes = 6456,
                downvotes = 5345,
                tags = listOf("Voting", "Rights", "Democracy")
            ),
            VotingCard(
                id = "6",
                title = "Infrastructure Investment Plan",
                description = "Allocate $1 Trillion over 10 years to modernize roads, bridges, and public transport networks nationwide.",
                category = "Infrastructure",
                sponsor = "Dept. Transportation",
                upvotes = 7105,
                downvotes = 6988,
                tags = listOf("Infrastructure", "Economy", "Transport")
            ),
            VotingCard(
                id = "7",
                title = "Term Limits Amendment",
                description = "Propose constitutional amendment to limit Congressional terms to 12 years total service.",
                category = "Electoral Reform",
                sponsor = "Citizens for Reform",
                upvotes = 8234,
                downvotes = 5678,
                tags = listOf("Government", "Reform", "Congress")
            ),
            VotingCard(
                id = "8",
                title = "National Healthcare System",
                description = "Establish a single-payer healthcare system covering all US citizens and legal residents.",
                category = "Public Safety",
                sponsor = "Healthcare Alliance",
                upvotes = 9876,
                downvotes = 8765,
                tags = listOf("Healthcare", "Social Policy", "Reform")
            ),
            VotingCard(
                id = "9",
                title = "Student Debt Relief",
                description = "Cancel up to $50,000 in federal student loan debt for borrowers making under $125,000 annually.",
                category = "Education",
                sponsor = "Education Coalition",
                upvotes = 7654,
                downvotes = 6543,
                tags = listOf("Education", "Economy", "Student Loans")
            ),
            VotingCard(
                id = "10",
                title = "Digital Privacy Act",
                description = "Comprehensive data protection law giving citizens control over personal data collection and usage.",
                category = "Public Safety",
                sponsor = "Tech Ethics Board",
                upvotes = 6789,
                downvotes = 5678,
                tags = listOf("Privacy", "Technology", "Rights")
            ),
            VotingCard(
                id = "11",
                title = "Carbon Tax Initiative",
                description = "Implement a national carbon tax starting at $40 per ton, increasing annually.",
                category = "Environment",
                sponsor = "Climate Action Now",
                upvotes = 7123,
                downvotes = 6234,
                tags = listOf("Environment", "Economy", "Climate")
            ),
            VotingCard(
                id = "12",
                title = "Immigration Reform Act",
                description = "Comprehensive immigration reform including path to citizenship and border security measures.",
                category = "Public Safety",
                sponsor = "Bipartisan Coalition",
                upvotes = 8432,
                downvotes = 7654,
                tags = listOf("Immigration", "Security", "Reform")
            )
        ))
        // TODO: Filter out cards already voted on by the user (when persistence is added)
    }
    
    val currentCard: VotingCard?
        get() = _votingCards.firstOrNull()

    // Revert back to exposing only the single next card
    val nextCard: VotingCard?
        get() {
             // Get the card at index 1 if it exists
            return _votingCards.getOrNull(1)
        }

    // Re-add upcomingCards for the visual stack
    val upcomingCards: List<VotingCard>
        get() {
            // Get cards from index 1 up to index 3 (max 2 upcoming cards)
            return _votingCards.drop(1).take(2)
        }

    fun onCardSwiped(swipedRight: Boolean) {
        viewModelScope.launch {
            currentCard?.let { cardToRemove ->
                // Add to vote history
                _voteHistory.add(Vote(
                    cardId = cardToRemove.id,
                    title = cardToRemove.title,
                    isSupport = swipedRight
                ))

                // Remove the card from the list
                _votingCards.remove(cardToRemove)

                // Check if we've hit 10 votes
                if (_voteHistory.size >= 10) {
                    showFinalReview = true
                }
            }
        }
    }
    
    fun onCardClicked(card: VotingCard) {
        selectedCard = card
        showDetailDialog = true
    }
    
    fun onDismissDialog() {
        showDetailDialog = false
        selectedCard = null
    }
    
    fun onJoinDebate() {
        // Placeholder for debate logic
        println("Joining debate for card: ${selectedCard?.id}")
        // Navigation will be handled in the Screen
    }

    fun getHotTopics(): List<String> {
        return votingCards.filter { it.isHot }.map { it.title }
    }

    fun getCategories(): List<String> {
        return votingCards.map { it.category }.distinct()
    }

    fun onFinalReviewDismissed() {
        showFinalReview = false
    }

    fun updateVote(index: Int, isSupport: Boolean) {
        if (index < _voteHistory.size) {
            val vote = _voteHistory[index]
            _voteHistory[index] = vote.copy(isSupport = isSupport)
        }
    }

    fun submitFinalVotes() {
        // TODO: Submit votes to repository
        showFinalReview = false
    }
} 