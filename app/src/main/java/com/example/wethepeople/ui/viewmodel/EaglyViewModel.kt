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
class EaglyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EagleyRepository(application)
    private val customizationRepository = CustomizationRepository(
        application.getSharedPreferences("eagley_customization", android.content.Context.MODE_PRIVATE)
    )
    private val avatarStateManager = AvatarStateManager(customizationRepository)

    // Expose necessary StateFlows from repository and state manager
    val userData: StateFlow<UserEagleyData> = repository.userDataFlow
    val assetCustomizations: StateFlow<Map<String, Pair<Offset, Float>>> = avatarStateManager.assetCustomizations
    val backgroundState: StateFlow<Background?> = avatarStateManager.backgroundState
    val currentCustomizations: StateFlow<Map<String, Pair<Offset, Float>>> = avatarStateManager.assetCustomizations

    // Add the init block correctly
    init {
        // Log initial value from the repository flow
        Log.d("EaglyViewModel", "Initial selected accessory ID: ${repository.userDataFlow.value.appearance.selectedAccessoryId}")
    }

    // Restore original functions
    fun getItemsByType(type: CustomizationType): List<EagleyItem> {
        return repository.getItemsByType(type)
    }

    fun getItem(id: String): EagleyItem? {
        return repository.getItemById(id)
    }

    fun hasItem(itemId: String): Boolean {
        return repository.hasItem(itemId)
    }

    fun setSelectedItem(itemId: String, type: CustomizationType) {
        // Ensure the item is owned before selecting
        if (!hasItem(itemId)) return
        // Delegate selection logic entirely to the repository
        viewModelScope.launch {
            repository.setSelectedItem(itemId, type)
        }
    }

    fun purchaseItem(itemId: String) {
        // Delegate purchase logic entirely to the repository
        viewModelScope.launch {
            repository.purchaseItem(itemId)
        }
    }

    fun addPatriotPoints(amount: Int) {
        // Delegate points logic entirely to the repository
        viewModelScope.launch {
            repository.addPatriotPoints(amount)
        }
    }

    fun getCurrentSelection(type: CustomizationType): EagleyItem? {
        // Get the current appearance data from the repository flow
        val currentAppearance = repository.userDataFlow.value.appearance
        val itemId = when (type) {
            CustomizationType.BORDER -> currentAppearance.selectedBorderId
            CustomizationType.HAT -> currentAppearance.selectedHatId
            CustomizationType.ACCESSORY -> currentAppearance.selectedAccessoryId
            CustomizationType.MASCOT -> currentAppearance.selectedColorSchemeId
        }
        // Get the item details using the ID
        return itemId?.let { repository.getItemById(it) }
    }

    // Keep AvatarStateManager update functions if UI interacts directly for dragging/scaling
    // These likely don't need to be here if DraggableEagleyCustomizer manages its own offset/scale state
    // But we keep them for now if they were used elsewhere.
    fun updateAssetPosition(assetId: String, position: Offset) {
        avatarStateManager.updateAssetPosition(assetId, position)
    }

    fun setBackground(background: Background) {
        avatarStateManager.setBackground(background)
    }

    fun updateAssetScale(assetId: String, scale: Float) {
        avatarStateManager.updateAssetScale(assetId, scale)
    }

    // Save function calling repository
    fun saveEagleyCustomization(customizations: Map<String, Pair<Offset, Float>>) {
        viewModelScope.launch {
            customizationRepository.saveCustomization(customizations)
            notifyCustomizationSaved() // Notify UI if needed
        }
    }

    // Restore notification flow
    private val _customizationSaved = MutableStateFlow(false)
    val customizationSaved: StateFlow<Boolean> = _customizationSaved.asStateFlow()

    fun notifyCustomizationSaved() {
        _customizationSaved.value = true
    }

    fun resetCustomizationSavedFlag() {
        _customizationSaved.value = false
    }
} 