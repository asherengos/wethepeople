package com.example.partyofthepeople.shop

data class ShopItem(
    val id: String = "",
    val name: String,
    val description: String,
    val price: Int,
    val currency: CurrencyType,
    val category: ShopItemCategory,
    val effectType: ShopItemEffect,
    val effectValue: Int,
    val available: Boolean = true,
    val stockRemaining: Int? = null,
    val limitedTimeUntil: Long? = null,
    val discountPercentage: Int = 0
)

data class UserInventory(
    val userId: String,
    val items: Map<String, InventoryItem> = mapOf()
)

data class InventoryItem(
    val itemId: String,
    val quantity: Int = 0,
    val acquiredAt: Long,
    val expiresAt: Long? = null,
    val used: Int = 0
)

data class CurrencyTransaction(
    val id: String = "",
    val userId: String,
    val type: CurrencyType,
    val amount: Int,
    val reason: String,
    val timestamp: Long,
    val isSpending: Boolean,
    val relatedItemId: String? = null
)

enum class CurrencyType {
    PATRIOT_POINTS,
    FREEDOM_BUCKS
}

enum class ShopItemCategory {
    GENERAL,
    PROFILE,
    POWER_UPS,
    CONSUMABLES,
    SPECIAL,
    POWER,
    COSMETIC,
    WEAPON,
    LIMITED,
    BOOST,
    TITLE,
    CONSUMABLE
}

enum class ShopItemEffect {
    NONE,
    SUPER_VOTE,
    IMPEACHMENT_SHIELD,
    PROFILE_THEME,
    PROFILE_BADGE,
    PROFILE_TITLE,
    AUDIT_USER,
    DEBATE_BOMB,
    TROLL_FARM,
    MEDIA_BLACKOUT,
    EXECUTIVE_ORDER,
    FILIBUSTER_PASS,
    BUNDLE,
    BOOST_VOTES,
    BOOST_INFLUENCE,
    BOOST_VISIBILITY,
    BOOST_EARNINGS,
    BOOST_EXPERIENCE,
    BOOST_REPUTATION,
    UNLOCK_TITLE,
    UNLOCK_THEME,
    UNLOCK_BADGE,
    UNLOCK_EMOTE,
    UNLOCK_FRAME,
    UNLOCK_BACKGROUND
} 