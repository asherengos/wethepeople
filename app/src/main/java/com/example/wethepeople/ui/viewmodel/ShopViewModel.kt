package com.example.wethepeople.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.wethepeople.data.model.ShopItem
import com.example.wethepeople.data.model.ShopItemCategory
import com.example.wethepeople.data.repository.ShopRepository
import com.example.wethepeople.data.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ShopUiState(
    val freedomBucksBalance: Int = 0,
    val items: List<ShopItem> = emptyList(),
    val selectedCategory: ShopItemCategory = ShopItemCategory.CHARACTER_OUTFIT,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showPurchaseAnimation: Boolean = false,
    val purchasedItem: ShopItem? = null
)

class ShopViewModel(context: Context) : ViewModel() {
    private val shopRepository = ShopRepository.getInstance()
    private val userRepository = UserRepository.getInstance(context)

    private val _uiState = MutableStateFlow(ShopUiState())
    val uiState: StateFlow<ShopUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine shop items with user data
            combine(
                shopRepository.items,
                userRepository.ownedItems,
                userRepository.equippedItems,
                userRepository.freedomBucksBalance
            ) { items, ownedItems, equippedItems, balance ->
                val updatedItems = items.map { item ->
                    item.copy(
                        isOwned = ownedItems.contains(item.id),
                        isEquipped = equippedItems[item.category.name] == item.id
                    )
                }
                _uiState.value = _uiState.value.copy(
                    items = updatedItems,
                    freedomBucksBalance = balance,
                    isLoading = false
                )
            }.collect()
        }
    }

    fun selectCategory(category: ShopItemCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun purchaseItem(item: ShopItem) {
        viewModelScope.launch {
            try {
                if (userRepository.purchaseItem(item)) {
                    // Show purchase animation
                    _uiState.value = _uiState.value.copy(
                        showPurchaseAnimation = true,
                        purchasedItem = item
                    )
                    // Hide animation after delay
                    kotlinx.coroutines.delay(2000)
                    _uiState.value = _uiState.value.copy(
                        showPurchaseAnimation = false,
                        purchasedItem = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Insufficient Freedom Bucks!"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Purchase failed: ${e.message}"
                )
            }
        }
    }

    fun equipItem(item: ShopItem) {
        viewModelScope.launch {
            try {
                if (!item.isOwned) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "You don't own this item!"
                    )
                    return@launch
                }

                if (item.isEquipped) {
                    userRepository.unequipItem(item)
                } else {
                    userRepository.equipItem(item)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to equip item: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Factory for creating ShopViewModel with context
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
                return ShopViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 