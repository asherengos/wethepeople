package com.example.wethepeople.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.R
import com.example.wethepeople.data.model.CustomizationType
import com.example.wethepeople.data.model.EagleyAppearance
import com.example.wethepeople.data.model.EagleyItem
import com.example.wethepeople.data.model.ItemRarity
import com.example.wethepeople.data.model.UserEagleyData
import com.example.wethepeople.data.repository.EagleyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.wethepeople.data.AvatarStateManager
import com.example.wethepeople.data.model.Background
import androidx.compose.ui.geometry.Offset
import com.example.wethepeople.data.CustomizationRepository
import android.util.Log

/**
 * ViewModel for Eagley customization screen
 */
class EagleyViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = EagleyRepository(application)
    private val customizationRepository = CustomizationRepository(
        application.getSharedPreferences("eagley_customization", android.content.Context.MODE_PRIVATE)
    )
    private val avatarStateManager = AvatarStateManager(customizationRepository)
    
    val userData: StateFlow<UserEagleyData> = repository.userDataFlow
    val assetCustomizations: StateFlow<Map<String, Pair<Offset, Float>>> = avatarStateManager.assetCustomizations
    val backgroundState: StateFlow<Background?> = avatarStateManager.backgroundState
    val currentCustomizations: StateFlow<Map<String, Pair<Offset, Float>>> = avatarStateManager.assetCustomizations
    
    init {
        Log.d("EaglyViewModel", "Initial selected accessory ID: ${repository.userDataFlow.value.appearance.selectedAccessoryId}")
    }
    
    /**
     * Get all items of a specific type
     */
    fun getItemsByType(type: CustomizationType): List<EagleyItem> {
        return repository.getItemsByType(type)
    }
    
    /**
     * Get an item by ID
     */
    fun getItem(id: String): EagleyItem? {
        return repository.getItemById(id)
    }
    
    /**
     * Check if user owns a specific item
     */
    fun hasItem(itemId: String): Boolean {
        return repository.hasItem(itemId)
    }
    
    /**
     * Set the selected item for a category
     */
    fun setSelectedItem(itemId: String, type: CustomizationType) {
        if (!hasItem(itemId)) return
        
        viewModelScope.launch { 
            repository.setSelectedItem(itemId, type) 
        }
    }
    
    /**
     * Purchase a new item
     */
    fun purchaseItem(itemId: String) {
        viewModelScope.launch { 
            repository.purchaseItem(itemId)
        }
    }
    
    /**
     * Add Patriot Points to user's balance
     */
    fun addPatriotPoints(amount: Int) {
        viewModelScope.launch { 
            repository.addPatriotPoints(amount)
        }
    }
    
    /**
     * Get the currently selected item for a category
     */
    fun getCurrentSelection(type: CustomizationType): EagleyItem? {
        val currentAppearance = repository.userDataFlow.value.appearance
        val itemId = when (type) {
            CustomizationType.BORDER -> currentAppearance.selectedBorderId
            CustomizationType.HAT -> currentAppearance.selectedHatId
            CustomizationType.ACCESSORY -> currentAppearance.selectedAccessoryId
            CustomizationType.MASCOT -> currentAppearance.selectedColorSchemeId
        }
        
        return itemId?.let { repository.getItemById(it) }
    }

    fun updateAssetPosition(assetId: String, position: Offset) {
        avatarStateManager.updateAssetPosition(assetId, position)
    }

    fun setBackground(background: Background) {
        avatarStateManager.setBackground(background)
    }

    fun updateAssetScale(assetId: String, scale: Float) {
        avatarStateManager.updateAssetScale(assetId, scale)
    }

    fun updateAssetCustomization(assetId: String, position: Offset, scale: Float) {
        avatarStateManager.updateAssetCustomization(assetId, position, scale)
    }

    // Function to save the entire customization map
    fun saveEagleyCustomization(customizations: Map<String, Pair<Offset, Float>>) {
        viewModelScope.launch {
            customizationRepository.saveCustomization(customizations)
            notifyCustomizationSaved()
        }
    }

    // Add a flow to notify when customization is saved
    private val _customizationSaved = MutableStateFlow(false)
    val customizationSaved: StateFlow<Boolean> = _customizationSaved.asStateFlow()

    fun notifyCustomizationSaved() {
        _customizationSaved.value = true
    }

    fun resetCustomizationSavedFlag() {
        _customizationSaved.value = false
    }
} 