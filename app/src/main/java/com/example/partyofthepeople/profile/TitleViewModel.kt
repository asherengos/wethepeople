package com.example.partyofthepeople.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partyofthepeople.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for the title system
 */
data class TitleUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val earnedTitles: List<TitleManager.Title> = emptyList(),
    val availableTitles: List<TitleManager.Title> = emptyList(),
    val selectedTitle: TitleManager.Title? = null,
    val showcasedTitle: TitleManager.Title? = null,
    val showcaseTimeRemainingHours: Int = 0,
    val freedomBucks: Int = 0,
    val categorizedTitles: Map<TitleManager.TitleCategory, List<TitleManager.Title>> = emptyMap(),
    val newlyEarnedTitle: TitleManager.Title? = null
)

/**
 * ViewModel for the title system
 */
class TitleViewModel : ViewModel() {
    private val titleManager = TitleManager()
    private val TAG = "TitleViewModel"
    
    private val _uiState = MutableStateFlow(TitleUiState())
    val uiState: StateFlow<TitleUiState> = _uiState.asStateFlow()
    
    /**
     * Load a user's titles
     */
    fun loadUserTitles(userProfile: UserProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val userId = userProfile.userId
                
                // Get all user's titles
                val earnedTitles = titleManager.getUserTitleObjects(userId)
                
                // Get all available titles
                val allTitles = titleManager.allTitles
                
                // Get preferred title
                val preferredTitleId = titleManager.getPreferredTitle(userId)
                val selectedTitle = preferredTitleId?.let { titleManager.getTitleById(it) }
                
                // Get showcased title
                val (showcasedTitleId, showcaseEndTime) = titleManager.getShowcasedTitle(userId)
                val showcasedTitle = showcasedTitleId?.let { titleManager.getTitleById(it) }
                
                // Calculate showcase time remaining
                val currentTime = System.currentTimeMillis()
                val showcaseTimeRemainingHours = if (showcaseEndTime > currentTime) {
                    ((showcaseEndTime - currentTime) / (60 * 60 * 1000)).toInt()
                } else {
                    0
                }
                
                // Categorize titles
                val categorizedTitles = earnedTitles.groupBy { it.category }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        earnedTitles = earnedTitles,
                        availableTitles = allTitles,
                        selectedTitle = selectedTitle,
                        showcasedTitle = showcasedTitle,
                        showcaseTimeRemainingHours = showcaseTimeRemainingHours,
                        freedomBucks = userProfile.freedomBucks,
                        categorizedTitles = categorizedTitles
                    )
                }
                
                // Check for new titles
                checkForNewTitles(userProfile)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user titles: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load titles: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Set a user's preferred title
     */
    fun setPreferredTitle(userProfile: UserProfile, titleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val success = titleManager.setPreferredTitle(userProfile.userId, titleId)
                
                if (success) {
                    val selectedTitle = titleManager.getTitleById(titleId)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            selectedTitle = selectedTitle,
                            successMessage = "Title set as preferred!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to set preferred title."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting preferred title: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Purchase a title showcase
     */
    fun purchaseTitleShowcase(userProfile: UserProfile, titleId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val success = titleManager.purchaseTitleShowcase(
                    userProfile.userId, 
                    titleId, 
                    userProfile.freedomBucks
                )
                
                if (success) {
                    val showcasedTitle = titleManager.getTitleById(titleId)
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showcasedTitle = showcasedTitle,
                            showcaseTimeRemainingHours = 24 * 7, // 1 week in hours
                            freedomBucks = userProfile.freedomBucks - 50, // Showcase price
                            successMessage = "Title showcase purchased!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to purchase title showcase."
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing title showcase: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error: ${e.message}"
                    )
                }
            }
        }
    }
    
    /**
     * Check for new titles the user may have earned
     */
    private fun checkForNewTitles(userProfile: UserProfile) {
        viewModelScope.launch {
            try {
                val newTitles = titleManager.checkAndAwardTitles(userProfile)
                
                if (newTitles.isNotEmpty()) {
                    // Update the UI state with the newly earned title
                    _uiState.update {
                        it.copy(
                            newlyEarnedTitle = newTitles.first(),
                            earnedTitles = it.earnedTitles + newTitles,
                            categorizedTitles = (it.earnedTitles + newTitles).groupBy { title -> title.category }
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking for new titles: ${e.message}")
            }
        }
    }
    
    /**
     * Clear newly earned title notification
     */
    fun clearNewTitleNotification() {
        _uiState.update { it.copy(newlyEarnedTitle = null) }
    }
    
    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Get all titles by category
     */
    fun getTitlesByCategory(): Map<TitleManager.TitleCategory, List<TitleManager.Title>> {
        return titleManager.allTitles.groupBy { it.category }
    }
    
    /**
     * Get all earned titles by category
     */
    fun getEarnedTitlesByCategory(): Map<TitleManager.TitleCategory, List<TitleManager.Title>> {
        return _uiState.value.earnedTitles.groupBy { it.category }
    }
    
    /**
     * Check if a title is earned
     */
    fun isTitleEarned(titleId: String): Boolean {
        return _uiState.value.earnedTitles.any { it.id == titleId }
    }
} 