package com.example.wethepeople.data

import android.content.SharedPreferences
import androidx.compose.ui.geometry.Offset
import com.example.wethepeople.data.model.Background
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CustomizationRepository(private val preferences: SharedPreferences) {
    private val gson = Gson()

    // Flow to hold and emit customization changes
    private val _customizationsFlow = MutableStateFlow<Map<String, Pair<Offset, Float>>>(emptyMap())
    val customizationsFlow: StateFlow<Map<String, Pair<Offset, Float>>> = _customizationsFlow.asStateFlow()

    companion object {
        private const val KEY_ASSET_POSITIONS = "asset_positions"
        private const val KEY_BACKGROUND_STATE = "background_state"
        private const val KEY_ASSET_CUSTOMIZATION = "asset_customization"
    }

    init {
        // Load initial values into the flow when the repository is created
        _customizationsFlow.value = loadCustomizationInternal()
    }

    // Keep legacy save/load for positions if needed elsewhere, but prefer customization
    fun savePositions(positions: Map<String, Offset>) {
        val json = gson.toJson(positions.mapValues { listOf(it.value.x, it.value.y) })
        preferences.edit().putString(KEY_ASSET_POSITIONS, json).apply()
    }

    fun loadPositions(): Map<String, Offset> {
        val json = preferences.getString(KEY_ASSET_POSITIONS, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, List<Float>>>() {}.type
        val map: Map<String, List<Float>> = gson.fromJson(json, type)
        return map.mapValues { Offset(it.value[0], it.value[1]) }
    }

    fun saveBackground(background: Background) {
        val json = gson.toJson(background)
        preferences.edit().putString(KEY_BACKGROUND_STATE, json).apply()
    }

    fun loadBackground(): Background? {
        val json = preferences.getString(KEY_BACKGROUND_STATE, null) ?: return null
        return gson.fromJson(json, Background::class.java)
    }

    // Updated save function: saves to prefs AND updates the flow
    fun saveCustomization(customization: Map<String, Pair<Offset, Float>>) {
        val json = gson.toJson(customization.mapValues { 
            listOf(it.value.first.x, it.value.first.y, it.value.second) 
        })
        preferences.edit().putString(KEY_ASSET_CUSTOMIZATION, json).apply()
        // Update the flow to notify observers
        _customizationsFlow.value = customization
    }

    // Public load function now simply returns the latest value from the flow
    fun loadCustomization(): Map<String, Pair<Offset, Float>> {
        return _customizationsFlow.value
    }

    // Internal function to load directly from preferences (used in init)
    private fun loadCustomizationInternal(): Map<String, Pair<Offset, Float>> {
        val json = preferences.getString(KEY_ASSET_CUSTOMIZATION, null) ?: return emptyMap()
        return try {
            val type = object : TypeToken<Map<String, List<Float>>>() {}.type
            val map: Map<String, List<Float>> = gson.fromJson(json, type)
            map.mapValues { 
                Pair(Offset(it.value[0], it.value[1]), it.value.getOrElse(2) { 1.0f }) 
            }
        } catch (e: Exception) {
            // Handle potential JSON parsing errors
            android.util.Log.e("CustomizationRepo", "Error parsing customization JSON", e)
            emptyMap()
        }
    }
} 