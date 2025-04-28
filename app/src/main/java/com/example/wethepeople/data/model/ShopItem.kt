package com.example.wethepeople.data.model

import androidx.compose.ui.graphics.Color

enum class ShopItemCategory {
    CHARACTER_OUTFIT,
    CHARACTER_ACCESSORY,
    PROFILE_FRAME,
    PROFILE_BACKGROUND,
    SPECIAL_EFFECT,
    EAGLY_OUTFIT,
    EAGLY_ACCESSORY
}

data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val category: ShopItemCategory,
    val price: Int, // Price in Freedom Bucks
    val rarity: ItemRarity,
    val previewImageUrl: String,
    val imageUrl: String? = null, // URL to the actual image asset
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false
)

enum class ItemRarity(val color: Color, val multiplier: Float) {
    COMMON(Color(0xFF9E9E9E), 1.0f),
    UNCOMMON(Color(0xFF4CAF50), 1.2f),
    RARE(Color(0xFF2196F3), 1.5f),
    EPIC(Color(0xFF9C27B0), 2.0f),
    LEGENDARY(Color(0xFFFFD700), 3.0f),
    LIMITED_EDITION(Color(0xFFFF0000), 5.0f)
} 