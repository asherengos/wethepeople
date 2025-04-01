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
 * UI state for profile customization screen
 */
data class ProfileUiState(
    val currentTheme: ProfileTheme = ProfileThemes.getThemeById("default"),
    val ownedThemes: List<ProfileTheme> = listOf(ProfileThemes.getThemeById("default")),
    val availableThemes: List<ProfileTheme> = emptyList(),
    val availableTitles: List<String> = emptyList(),
    val selectedTitle: String = "",
    val isProfileBoosted: Boolean = false,
    val boostTimeRemainingMinutes: Int = 0,
    val freedomBucks: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * ViewModel for profile customization and premium features
 */
class ProfileViewModel : ViewModel() {
    private val profileManager = ProfileManager()
    private val TAG = "ProfileViewModel"
    
    // UI state
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    // Available themes
    val allThemes = ProfileThemes.getAllThemes()
    
    init {
        _uiState.update { it.copy(
            availableThemes = allThemes
        )}
    }
    
    /**
     * Load current theme and profile data
     */
    fun loadProfileData(userProfile: UserProfile) {
        val themeId = userProfile.preferences.theme
        val currentTheme = ProfileThemes.getThemeById(themeId)
        val ownedThemes = userProfile.ownedThemes.map { ProfileThemes.getThemeById(it) }
        val preferredTitle = if (userProfile.preferences.preferredTitleId.isNotEmpty()) {
            userProfile.preferences.preferredTitleId
        } else {
            userProfile.titles.firstOrNull() ?: ""
        }
        
        // Check if profile is boosted
        val isProfileBoosted = profileManager.isProfileBoosted(userProfile)
        val boostTimeRemaining = if (isProfileBoosted) {
            profileManager.getProfileBoostTimeRemaining(userProfile)
        } else {
            0
        }
        
        _uiState.update { it.copy(
            currentTheme = currentTheme,
            ownedThemes = ownedThemes,
            availableTitles = userProfile.titles,
            selectedTitle = preferredTitle,
            isProfileBoosted = isProfileBoosted,
            boostTimeRemainingMinutes = boostTimeRemaining,
            freedomBucks = userProfile.freedomBucks
        )}
    }
    
    /**
     * Purchase a new theme
     */
    fun purchaseTheme(userProfile: UserProfile, themeId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        )}
        
        viewModelScope.launch {
            try {
                val success = profileManager.purchaseTheme(userId, userProfile, themeId)
                
                if (success) {
                    // Update state with new theme as owned
                    val newTheme = ProfileThemes.getThemeById(themeId)
                    val updatedOwnedThemes = _uiState.value.ownedThemes.toMutableList()
                    updatedOwnedThemes.add(newTheme)
                    
                    _uiState.update { it.copy(
                        ownedThemes = updatedOwnedThemes,
                        successMessage = "Theme purchased successfully!",
                        freedomBucks = userProfile.freedomBucks - newTheme.price
                    )}
                } else {
                    _uiState.update { it.copy(
                        errorMessage = "Failed to purchase theme. Insufficient Freedom Bucks."
                    )}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing theme: ${e.message}")
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.message}"
                )}
            } finally {
                _uiState.update { it.copy(
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Apply a theme to the profile
     */
    fun applyTheme(userProfile: UserProfile, themeId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        )}
        
        viewModelScope.launch {
            try {
                val success = profileManager.applyTheme(userId, userProfile, themeId)
                
                if (success) {
                    val newTheme = ProfileThemes.getThemeById(themeId)
                    
                    _uiState.update { it.copy(
                        currentTheme = newTheme,
                        successMessage = "Theme applied successfully!"
                    )}
                } else {
                    _uiState.update { it.copy(
                        errorMessage = "Failed to apply theme. You do not own this theme."
                    )}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error applying theme: ${e.message}")
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.message}"
                )}
            } finally {
                _uiState.update { it.copy(
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Select a preferred title
     */
    fun setPreferredTitle(userProfile: UserProfile, titleId: String) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        )}
        
        viewModelScope.launch {
            try {
                val success = profileManager.setPreferredTitle(userId, userProfile, titleId)
                
                if (success) {
                    _uiState.update { it.copy(
                        selectedTitle = titleId,
                        successMessage = "Title set as preferred!"
                    )}
                } else {
                    _uiState.update { it.copy(
                        errorMessage = "Failed to set preferred title."
                    )}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting preferred title: ${e.message}")
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.message}"
                )}
            } finally {
                _uiState.update { it.copy(
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Purchase a profile boost
     */
    fun purchaseProfileBoost(userProfile: UserProfile) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        
        _uiState.update { it.copy(
            isLoading = true,
            errorMessage = null
        )}
        
        viewModelScope.launch {
            try {
                val boostPrice = 50 // Same as in ProfileManager
                val success = profileManager.purchaseProfileBoost(userId, userProfile)
                
                if (success) {
                    _uiState.update { it.copy(
                        isProfileBoosted = true,
                        boostTimeRemainingMinutes = 24 * 60, // 24 hours in minutes
                        freedomBucks = userProfile.freedomBucks - boostPrice,
                        successMessage = "Profile boosted for 24 hours!"
                    )}
                } else {
                    _uiState.update { it.copy(
                        errorMessage = "Failed to purchase profile boost. Insufficient Freedom Bucks."
                    )}
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error purchasing profile boost: ${e.message}")
                _uiState.update { it.copy(
                    errorMessage = "Error: ${e.message}"
                )}
            } finally {
                _uiState.update { it.copy(
                    isLoading = false
                )}
            }
        }
    }
    
    /**
     * Clear success message to prevent showing it again
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(
            successMessage = null
        )}
    }
    
    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _uiState.update { it.copy(
            errorMessage = null
        )}
    }
} 