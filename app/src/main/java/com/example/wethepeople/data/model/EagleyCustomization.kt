package com.example.wethepeople.data.model

import androidx.annotation.DrawableRes

/**
 * Types of customizations available for Eagley
 */
enum class CustomizationType {
    BORDER,
    HAT,
    ACCESSORY,
    MASCOT
}

/**
 * Represents a customization item for Eagley
 */
data class EagleyItem(
    val id: String,
    val name: String,
    val description: String,
    val type: CustomizationType,
    @DrawableRes val resourceId: Int,
    val cost: Int, // Cost in Patriot Points
    val isDefault: Boolean = false, // Is this a default free item?
    val rarity: ItemRarity = ItemRarity.COMMON
)

/**
 * Represents the current appearance of Eagley
 */
data class EagleyAppearance(
    val selectedBorderId: String? = null,
    val selectedHatId: String? = null,
    val selectedAccessoryId: String? = null,
    val selectedColorSchemeId: String? = null
)

/**
 * Represents user data related to Eagley
 */
data class UserEagleyData(
    val patriotPoints: Int = 0,
    val freedomBucks: Int = 0,
    val citizenRank: String = "ROOKIE",
    val appearance: EagleyAppearance = EagleyAppearance(),
    val purchasedItems: List<String> = emptyList()
) 