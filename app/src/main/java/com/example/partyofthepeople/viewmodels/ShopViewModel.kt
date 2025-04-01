package com.example.partyofthepeople.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partyofthepeople.shop.*
import com.example.partyofthepeople.services.ShopService
import com.example.partyofthepeople.services.UseItemResult
import com.example.partyofthepeople.utils.FirestoreConverters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UseItemStatus {
    object Loading : UseItemStatus()
    data class Success(val message: String, val item: ShopItem) : UseItemStatus()
    data class Error(val message: String) : UseItemStatus()
}

sealed class PurchaseStatus {
    object Loading : PurchaseStatus()
    data class Success(val message: String) : PurchaseStatus()
    data class Error(val message: String) : PurchaseStatus()
}

data class ShopUiState(
    val items: List<ShopItem> = emptyList(),
    val limitedTimeOffers: List<ShopItem> = emptyList(),
    val userInventory: UserInventory? = null,
    val freedomBucks: Int = 0,
    val patriotPoints: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: ShopItemCategory? = null
)

class ShopViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val TAG = "ShopViewModel"
    private val shopService = ShopService()
    
    // Shop items by category
    private val _powerItems = MutableStateFlow<List<ShopItem>>(emptyList())
    val powerItems: StateFlow<List<ShopItem>> = _powerItems
    
    private val _cosmeticItems = MutableStateFlow<List<ShopItem>>(emptyList())
    val cosmeticItems: StateFlow<List<ShopItem>> = _cosmeticItems
    
    private val _weaponItems = MutableStateFlow<List<ShopItem>>(emptyList())
    val weaponItems: StateFlow<List<ShopItem>> = _weaponItems
    
    private val _limitedItems = MutableStateFlow<List<ShopItem>>(emptyList())
    val limitedItems: StateFlow<List<ShopItem>> = _limitedItems
    
    // User inventory
    private val _userInventory = MutableStateFlow<UserInventory?>(null)
    val userInventory: StateFlow<UserInventory?> = _userInventory
    
    // Purchase status
    private val _purchaseStatus = MutableStateFlow<PurchaseStatus?>(null)
    val purchaseStatus: StateFlow<PurchaseStatus?> = _purchaseStatus
    
    // Use item status
    private val _useItemStatus = MutableStateFlow<UseItemStatus?>(null)
    val useItemStatus: StateFlow<UseItemStatus?> = _useItemStatus
    
    private val _selectedCategory = MutableStateFlow<ShopItemCategory>(ShopItemCategory.GENERAL)
    val selectedCategory: StateFlow<ShopItemCategory> = _selectedCategory
    
    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()
    
    init {
        loadShopData()
    }
    
    private fun loadShopData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                // Load shop items
                val items = getShopItems()
                val limitedTimeOffers = getLimitedTimeOffers()
                
                // Load user data
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val userDoc = db.collection("users").document(userId).get().await()
                    val freedomBucks = userDoc.getLong("freedomBucks")?.toInt() ?: 0
                    val patriotPoints = userDoc.getLong("patriotPoints")?.toInt() ?: 0
                    val inventory = getUserInventory(userId)
                    
                    _uiState.update { state ->
                        state.copy(
                            items = items,
                            limitedTimeOffers = limitedTimeOffers,
                            userInventory = inventory,
                            freedomBucks = freedomBucks,
                            patriotPoints = patriotPoints,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    error = "Failed to load shop data: ${e.message}"
                ) }
            }
        }
    }
    
    fun setSelectedCategory(category: ShopItemCategory) {
        _selectedCategory.value = category
    }
    
    fun loadShopItems() {
        viewModelScope.launch {
            try {
                // Load items by category
                _powerItems.value = shopService.getShopItems(ShopItemCategory.POWER)
                _cosmeticItems.value = shopService.getShopItems(ShopItemCategory.COSMETIC)
                _weaponItems.value = shopService.getShopItems(ShopItemCategory.WEAPON)
                _limitedItems.value = shopService.getLimitedTimeOffers()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading shop items", e)
            }
        }
    }
    
    fun loadUserInventory() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                _userInventory.value = shopService.getUserInventory(userId)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user inventory", e)
            }
        }
    }
    
    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            _purchaseStatus.value = PurchaseStatus.Loading
            
            try {
                // Get item details
                val itemDoc = db.collection("shop_items")
                    .document(itemId)
                    .get()
                    .await()
                
                val item = FirestoreConverters.toShopItem(itemDoc)
                    ?: throw Exception("Item not found")
                
                // Check if item is available
                if (!item.available) {
                    throw Exception("Item is not available for purchase")
                }
                
                // Check stock
                if (item.stockRemaining != null && item.stockRemaining <= 0) {
                    throw Exception("Item is out of stock")
                }
                
                // Check if limited time offer is still valid
                if (item.limitedTimeUntil != null && item.limitedTimeUntil < System.currentTimeMillis()) {
                    throw Exception("Limited time offer has expired")
                }
                
                // TODO: Check user's currency balance
                when (item.currency) {
                    CurrencyType.PATRIOT_POINTS -> {
                        // Check PP balance
                    }
                    CurrencyType.FREEDOM_BUCKS -> {
                        // Check FB balance
                    }
                }
                
                // TODO: Process purchase transaction
                
                _purchaseStatus.value = PurchaseStatus.Success("Successfully purchased ${item.name}!")
                
            } catch (e: Exception) {
                _purchaseStatus.value = PurchaseStatus.Error(e.message ?: "Failed to purchase item")
            }
        }
    }
    
    fun useItem(itemId: String, targetId: String? = null) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                
                _useItemStatus.value = UseItemStatus.Loading
                
                when (val result = shopService.useItem(userId, itemId, targetId)) {
                    is UseItemResult.Success -> {
                        _useItemStatus.value = UseItemStatus.Success(result.message, result.item)
                        loadUserInventory() // Refresh inventory after using item
                    }
                    is UseItemResult.Failure -> {
                        _useItemStatus.value = UseItemStatus.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error using item", e)
                _useItemStatus.value = UseItemStatus.Error("Error: ${e.message}")
            }
        }
    }
    
    fun clearPurchaseStatus() {
        _purchaseStatus.value = null
    }
    
    fun clearUseItemStatus() {
        _useItemStatus.value = null
    }
    
    fun getItemQuantityInInventory(itemId: String): Int {
        val inventory = _userInventory.value ?: return 0
        val item = inventory.items[itemId] ?: return 0
        return item.quantity - item.used
    }
    
    fun hasUnusedItem(itemId: String): Boolean {
        return getItemQuantityInInventory(itemId) > 0
    }
    
    private suspend fun getShopItems(): List<ShopItem> {
        val itemsCollection = db.collection("shop")
            .document("catalog")
            .collection("items")
            .whereEqualTo("available", true)
            .get()
            .await()
        
        return itemsCollection.documents.mapNotNull { doc ->
            FirestoreConverters.toShopItem(doc)
        }
    }
    
    private suspend fun getLimitedTimeOffers(): List<ShopItem> {
        val currentTime = System.currentTimeMillis()
        val itemsCollection = db.collection("shop")
            .document("catalog")
            .collection("items")
            .whereGreaterThan("limitedTimeUntil", currentTime)
            .whereEqualTo("available", true)
            .get()
            .await()
        
        return itemsCollection.documents.mapNotNull { doc ->
            FirestoreConverters.toShopItem(doc)
        }
    }
    
    private suspend fun getUserInventory(userId: String): UserInventory {
        val inventoryDoc = db.collection("users")
            .document(userId)
            .collection("inventory")
            .document("items")
            .get()
            .await()
        
        return FirestoreConverters.toUserInventory(inventoryDoc)
            ?: UserInventory(userId)
    }
} 