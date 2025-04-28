package com.example.wethepeople.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.wethepeople.R
import com.example.wethepeople.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository for managing Eagley customization items and user data
 */
class EagleyRepository(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(EAGLEY_PREFERENCES, Context.MODE_PRIVATE)
    
    private val gson = Gson()
    private val repositoryScope = CoroutineScope(Dispatchers.IO) // Scope for background tasks
    
    // StateFlow to observe changes to user data - initialized with default
    private val _userDataFlow = MutableStateFlow(getDefaultUserData())
    val userDataFlow: StateFlow<UserEagleyData> = _userDataFlow.asStateFlow()
    
    companion object {
        private const val EAGLEY_PREFERENCES = "eagley_preferences"
        private const val KEY_USER_DATA = "user_data"
        
        // Use a Map for efficient lookups
        private val EAGLEY_ITEMS_MAP: Map<String, EagleyItem> = listOf(
            // Default border
            EagleyItem(
                id = "border_default",
                name = "Standard Border",
                description = "The standard border for every patriot",
                type = CustomizationType.BORDER,
                resourceId = R.drawable.border_default,
                cost = 0,
                isDefault = true,
                rarity = ItemRarity.COMMON
            ),
            
            // Hats
            EagleyItem(
                id = "eagley_freedom_hat",
                name = "Freedom Eagle Hat",
                description = "The majestic Freedom Eagle hat for patriotic eagles",
                type = CustomizationType.HAT,
                resourceId = R.drawable.eagley_freedom_hat,
                cost = 250,
                rarity = ItemRarity.LEGENDARY
            ),
            EagleyItem(
                id = "hat_top_hat",
                name = "Top Hat",
                description = "A distinguished hat for a distinguished eagle",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_top_hat,
                cost = 100,
                rarity = ItemRarity.RARE
            ),
            EagleyItem(
                id = "hat_pirate",
                name = "Pirate Hat",
                description = "Arrrr! A patriotic pirate's hat",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_pirate,
                cost = 150,
                rarity = ItemRarity.RARE
            ),
            EagleyItem(
                id = "hat_cowboy",
                name = "Cowboy Hat",
                description = "Yeehaw! A true American cowboy hat",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_cowboy,
                cost = 200,
                rarity = ItemRarity.EPIC
            ),
            EagleyItem(
                id = "hat_clown",
                name = "Clown Hat",
                description = "For when you're feeling silly and patriotic",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_clown,
                cost = 125,
                rarity = ItemRarity.UNCOMMON
            ),
            
            // Accessories
            EagleyItem(
                id = "accessory_flag",
                name = "American Flag",
                description = "Let Eagley wave the stars and stripes",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.accessory_flag,
                cost = 75,
                rarity = ItemRarity.UNCOMMON
            ),
            
            // Default color scheme
            EagleyItem(
                id = "color_default",
                name = "Standard Colors",
                description = "The standard Eagley colors",
                type = CustomizationType.MASCOT,
                resourceId = R.drawable.eagley_base,
                cost = 0,
                isDefault = true,
                rarity = ItemRarity.COMMON
            ),
            
            // Alternative color schemes
            EagleyItem(
                id = "color_patriotic",
                name = "Patriotic Colors",
                description = "Red, white and blue Eagley",
                type = CustomizationType.MASCOT,
                resourceId = R.drawable.eagley_patriotic,
                cost = 200,
                rarity = ItemRarity.EPIC
            ),

            // New Accessories
            EagleyItem(
                id = "accessory_cap_shield",
                name = "Captain's Shield",
                description = "A patriotic shield for freedom",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.cap_shield,
                cost = 300,
                rarity = ItemRarity.EPIC
            ),
            EagleyItem(
                id = "accessory_sling_shot",
                name = "Liberty Slingshot",
                description = "For launching freedom from a distance",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.sling_shot,
                cost = 150,
                rarity = ItemRarity.UNCOMMON
            ),
            EagleyItem(
                id = "accessory_wand",
                name = "Freedom Wand",
                description = "Cast spells of democracy",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.wand,
                cost = 250,
                rarity = ItemRarity.RARE
            ),
            EagleyItem(
                id = "accessory_pizza",
                name = "Pizza of Liberty",
                description = "A slice of freedom",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.pizza,
                cost = 100,
                rarity = ItemRarity.UNCOMMON
            ),
            EagleyItem(
                id = "accessory_burger",
                name = "Freedom Burger",
                description = "The most American meal",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.burger,
                cost = 100,
                rarity = ItemRarity.UNCOMMON
            ),
            EagleyItem(
                id = "accessory_fries",
                name = "Freedom Fries",
                description = "The patriotic side dish",
                type = CustomizationType.ACCESSORY,
                resourceId = R.drawable.freedom_fries,
                cost = 75,
                rarity = ItemRarity.COMMON
            ),

            // New Hats
            EagleyItem(
                id = "hat_rasta_dreads",
                name = "Rasta Dreads",
                description = "Chill vibes for your eagle",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_rasta_dreads,
                cost = 175,
                rarity = ItemRarity.RARE
            ),
            EagleyItem(
                id = "hat_rasta_winter",
                name = "Winter Rasta",
                description = "Stay warm with style",
                type = CustomizationType.HAT,
                resourceId = R.drawable.hat_rasta_winter,
                cost = 200,
                rarity = ItemRarity.RARE
            )
        ).associateBy { it.id } // Convert List to Map<String, EagleyItem>
        
        // Keep a List version if needed elsewhere, derived from the map
        private val EAGLEY_ITEMS_LIST: List<EagleyItem> = EAGLEY_ITEMS_MAP.values.toList()
    }
    
    init {
        // Launch a coroutine to load actual data asynchronously
        repositoryScope.launch {
            _userDataFlow.value = loadUserData()
        }
    }
    
    /**
     * Get all available customization items
     */
    fun getAllItems(): List<EagleyItem> = EAGLEY_ITEMS_LIST // Return the derived list
    
    /**
     * Get all items of a specific type
     */
    fun getItemsByType(type: CustomizationType): List<EagleyItem> {
        // Filter the derived list
        return EAGLEY_ITEMS_LIST.filter { it.type == type }
    }
    
    /**
     * Get a specific item by ID (now faster using the Map)
     */
    fun getItemById(id: String): EagleyItem? {
        return EAGLEY_ITEMS_MAP[id] // O(1) lookup
    }
    
    /**
     * Check if user has purchased a specific item
     */
    fun hasItem(itemId: String): Boolean {
        val userData = _userDataFlow.value
        // Use map for slightly faster lookup here too
        return itemId in userData.purchasedItems || EAGLEY_ITEMS_MAP[itemId]?.isDefault == true
    }
    
    /**
     * Add Patriot Points to user's balance
     */
    fun addPatriotPoints(amount: Int) {
        val userData = _userDataFlow.value
        val updatedData = userData.copy(patriotPoints = userData.patriotPoints + amount)
        saveUserData(updatedData)
    }
    
    /**
     * Set custom items on Eagley
     */
    fun setAppearance(appearance: EagleyAppearance) {
        val userData = _userDataFlow.value
        val updatedData = userData.copy(appearance = appearance)
        saveUserData(updatedData)
    }
    
    /**
     * Purchase an item if user has enough Patriot Points
     * @return true if purchase successful, false otherwise
     */
    fun purchaseItem(itemId: String): Boolean {
        val item = getItemById(itemId) ?: return false
        val userData = _userDataFlow.value
        
        // Check if already purchased
        if (itemId in userData.purchasedItems) return true
        
        // Check if user has enough points
        if (userData.patriotPoints < item.cost) return false
        
        // Make the purchase
        val updatedPoints = userData.patriotPoints - item.cost
        val updatedItems = userData.purchasedItems + itemId
        val updatedData = userData.copy(
            patriotPoints = updatedPoints,
            purchasedItems = updatedItems
        )
        
        saveUserData(updatedData)
        return true
    }
    
    /**
     * Update user's selected item for a specific category
     */
    fun setSelectedItem(itemId: String, type: CustomizationType) {
        if (!hasItem(itemId)) return
        
        val userData = _userDataFlow.value
        val currentAppearance = userData.appearance
        
        val updatedAppearance = when (type) {
            CustomizationType.BORDER -> currentAppearance.copy(selectedBorderId = itemId)
            CustomizationType.HAT -> currentAppearance.copy(selectedHatId = itemId)
            CustomizationType.ACCESSORY -> currentAppearance.copy(selectedAccessoryId = itemId)
            CustomizationType.MASCOT -> currentAppearance.copy(selectedColorSchemeId = itemId)
        }
        
        val updatedData = userData.copy(appearance = updatedAppearance)
        saveUserData(updatedData)
    }
    
    /**
     * Load user data from SharedPreferences
     */
    private suspend fun loadUserData(): UserEagleyData = withContext(Dispatchers.IO) {
        val json = sharedPreferences.getString(KEY_USER_DATA, null)
        if (json != null) {
            try {
                gson.fromJson(json, UserEagleyData::class.java)
            } catch (e: Exception) {
                // Handle error, maybe return default data
                // Log.e("EagleyRepository", "Error parsing user data JSON", e) // Consider logging
                getDefaultUserData()
            }
        } else {
            getDefaultUserData()
        }
    }
    
    /**
     * Save user data to SharedPreferences
     */
    private fun saveUserData(userData: UserEagleyData) {
        val userDataJson = gson.toJson(userData)
        sharedPreferences.edit {
            putString(KEY_USER_DATA, userDataJson)
        }
        _userDataFlow.value = userData
    }
    
    /**
     * Get default user data with starter items
     */
    private fun getDefaultUserData(): UserEagleyData {
        // Use the map for default item filtering
        val defaultItems = EAGLEY_ITEMS_MAP.values
            .filter { it.isDefault }
            .map { it.id }
            .toList()
        
        return UserEagleyData(
            patriotPoints = 100, // Give user some starting points
            freedomBucks = 0,
            citizenRank = "SERF",
            appearance = EagleyAppearance(
                selectedBorderId = "border_default",
                selectedHatId = null,
                selectedAccessoryId = null,
                selectedColorSchemeId = "color_default"
            ),
            purchasedItems = defaultItems
        )
    }
} 