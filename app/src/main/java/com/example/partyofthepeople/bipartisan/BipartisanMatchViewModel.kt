package com.example.partyofthepeople.bipartisan

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partyofthepeople.BipartisanMatch
import com.example.partyofthepeople.SimilarityType
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.VoteRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BipartisanMatchViewModel : ViewModel() {
    private val bipartisanManager = BipartisanMatchManager()
    private val TAG = "BipartisanViewModel"
    
    // UI state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Matches data
    private val _matches = MutableStateFlow<List<BipartisanMatch>>(emptyList())
    val matches: StateFlow<List<BipartisanMatch>> = _matches.asStateFlow()
    
    // Current user's voting history
    private val _voteHistory = MutableStateFlow<List<VoteRecord>>(emptyList())
    val voteHistory: StateFlow<List<VoteRecord>> = _voteHistory.asStateFlow()
    
    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    /**
     * Load the bipartisan match data for the current user
     */
    fun loadBipartisanMatches(userProfile: UserProfile) {
        if (_isLoading.value) return
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                // Store vote history
                _voteHistory.value = userProfile.voteHistory
                
                // For quick UI display, use cached match percentages if available
                if (userProfile.matchPercentages.isNotEmpty()) {
                    // Convert to BipartisanMatch objects (with placeholder data)
                    val quickMatches = userProfile.matchPercentages.map { (userId, percentage) ->
                        BipartisanMatch(
                            userId = userId,
                            username = "Loading...",
                            avatar = "",
                            matchPercentage = percentage,
                            similarityType = SimilarityType.MIXED
                        )
                    }.sortedByDescending { it.matchPercentage }
                    
                    _matches.value = quickMatches
                } else if (userProfile.voteHistory.isEmpty()) {
                    // Generate sample data if no votes yet
                    _matches.value = generateSampleMatches()
                }
                
                // Calculate actual matches (this may take time)
                val calculatedMatches = bipartisanManager.calculateBipartisanMatches(userProfile)
                
                if (calculatedMatches.isNotEmpty()) {
                    _matches.value = calculatedMatches
                    
                    // Update stored matches for future reference
                    bipartisanManager.updateStoredMatchPercentages(userProfile.userId, calculatedMatches)
                
                    // If still no matches, use sample data
                    _matches.value = generateSampleMatches()
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error loading bipartisan matches: ${e.message}")
                _errorMessage.value = "Failed to load match data: ${e.message}"
                
                // Fallback to sample data
                if (_matches.value.isEmpty()) {
                    _matches.value = generateSampleMatches()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Record a vote in the user's history
     */
    fun recordVote(userId: String, voteRecord: VoteRecord) {
        viewModelScope.launch {
            try {
                bipartisanManager.recordVote(userId, voteRecord)
                
                // Update local vote history
                val updatedHistory = _voteHistory.value.toMutableList()
                updatedHistory.add(voteRecord)
                _voteHistory.value = updatedHistory
                
            } catch (e: Exception) {
                Log.e(TAG, "Error recording vote: ${e.message}")
                _errorMessage.value = "Failed to record vote: ${e.message}"
            }
        }
    }
    
    /**
     * Generate sample matches for UI preview
     */
    private fun generateSampleMatches(): List<BipartisanMatch> {
        val sampleUsernames = listOf(
            "FreedomFighter76", "LibertyCat", "ConstitutionDog", 
            "DemocracyEagle", "PatriotBear", "LibertyBird"
        )
        
        val sampleAvatars = listOf("🦅", "🗽", "🦁", "🦊", "🐻", "🐱")
        
        return List(6) { index ->
            val matchPercent = (25..95).random()
            val similarityType = when {
                matchPercent > 75 -> SimilarityType.SIMILAR
                matchPercent < 40 -> SimilarityType.OPPOSITE
                else -> SimilarityType.MIXED
            }
            
            BipartisanMatch(
                userId = "sample-${index}",
                username = sampleUsernames.getOrElse(index) { "Patriot${index+1}" },
                avatar = sampleAvatars.getOrElse(index) { "🦅" },
                matchPercentage = matchPercent,
                similarityType = similarityType
            )
        }.sortedByDescending { it.matchPercentage }
    }
} 