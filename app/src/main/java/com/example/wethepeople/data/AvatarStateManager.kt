package com.example.wethepeople.data

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.wethepeople.data.model.Background
import kotlinx.coroutines.flow.asStateFlow

class AvatarStateManager(private val repository: CustomizationRepository) {
    // Store both position and scale for each asset
    private val _assetCustomizations = MutableStateFlow<Map<String, Pair<Offset, Float>>>(emptyMap())
    val assetCustomizations: StateFlow<Map<String, Pair<Offset, Float>>> = _assetCustomizations

    private val _backgroundState = MutableStateFlow<Background?>(null)
    val backgroundState: StateFlow<Background?> = _backgroundState

    // For backward compatibility
    private val _assetPositions = MutableStateFlow<Map<String, Offset>>(emptyMap())
    val assetPositions: StateFlow<Map<String, Offset>> = _assetPositions

    // Update positions as well when customizations change
    init {
        // Load initial state from the repository
        _assetCustomizations.value = repository.loadCustomization()
        // Initialize _assetPositions based on loaded customizations
        _assetPositions.value = _assetCustomizations.value.mapValues { it.value.first }
    }

    fun updateAssetPosition(assetId: String, position: Offset) {
        val currentScale = _assetCustomizations.value[assetId]?.second ?: 1.0f
        _assetCustomizations.value = _assetCustomizations.value.toMutableMap().apply { 
            put(assetId, Pair(position, currentScale)) 
        }
        
        // Update position map for backward compatibility
        _assetPositions.value = _assetPositions.value.toMutableMap().apply {
            put(assetId, position)
        }
    }

    fun updateAssetScale(assetId: String, scale: Float) {
        val currentPosition = _assetCustomizations.value[assetId]?.first ?: Offset(0f, 0f)
        _assetCustomizations.value = _assetCustomizations.value.toMutableMap().apply { 
            put(assetId, Pair(currentPosition, scale)) 
        }
    }

    fun updateAssetCustomization(assetId: String, position: Offset, scale: Float) {
        _assetCustomizations.value = _assetCustomizations.value.toMutableMap().apply { 
            put(assetId, Pair(position, scale)) 
        }
        
        // Update position map for backward compatibility
        _assetPositions.value = _assetPositions.value.toMutableMap().apply {
            put(assetId, position)
        }
    }

    fun setBackground(background: Background) {
        _backgroundState.value = background
    }
} 