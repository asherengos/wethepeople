package com.example.wethepeople.data.repository

import com.example.wethepeople.data.model.ShopItem
import com.example.wethepeople.data.model.ShopItemCategory
import com.example.wethepeople.data.model.ItemRarity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ShopRepository private constructor() {
    private val _items = MutableStateFlow<List<ShopItem>>(createShopItems())
    val items: StateFlow<List<ShopItem>> = _items.asStateFlow()

    private fun createShopItems(): List<ShopItem> {
        return listOf(
            // Character Outfits
            ShopItem(
                id = "suit_patriot",
                name = "Patriot's Suit",
                description = "A distinguished suit for the modern patriot",
                category = ShopItemCategory.CHARACTER_OUTFIT,
                price = 500,
                rarity = ItemRarity.RARE,
                previewImageUrl = "outfit_patriot_suit"
            ),
            ShopItem(
                id = "outfit_founding_father",
                name = "Founding Father's Attire",
                description = "Dress like the architects of democracy",
                category = ShopItemCategory.CHARACTER_OUTFIT,
                price = 1500,
                rarity = ItemRarity.LEGENDARY,
                previewImageUrl = "outfit_founding_father"
            ),
            
            // Character Accessories
            ShopItem(
                id = "acc_liberty_pin",
                name = "Liberty Pin",
                description = "Show your commitment to freedom",
                category = ShopItemCategory.CHARACTER_ACCESSORY,
                price = 200,
                rarity = ItemRarity.COMMON,
                previewImageUrl = "acc_liberty_pin"
            ),
            ShopItem(
                id = "acc_constitution_scroll",
                name = "Constitution Scroll",
                description = "Carry the sacred document",
                category = ShopItemCategory.CHARACTER_ACCESSORY,
                price = 800,
                rarity = ItemRarity.EPIC,
                previewImageUrl = "acc_constitution"
            ),

            // Profile Frames
            ShopItem(
                id = "frame_constitution",
                name = "Constitutional Frame",
                description = "Frame adorned with the Constitution's preamble",
                category = ShopItemCategory.PROFILE_FRAME,
                price = 1000,
                rarity = ItemRarity.EPIC,
                previewImageUrl = "frame_constitution"
            ),
            ShopItem(
                id = "frame_liberty_bell",
                name = "Liberty Bell Frame",
                description = "Let freedom ring with this iconic frame",
                category = ShopItemCategory.PROFILE_FRAME,
                price = 750,
                rarity = ItemRarity.RARE,
                previewImageUrl = "frame_liberty_bell"
            ),

            // Profile Backgrounds
            ShopItem(
                id = "bg_independence_hall",
                name = "Independence Hall",
                description = "The birthplace of American democracy",
                category = ShopItemCategory.PROFILE_BACKGROUND,
                price = 600,
                rarity = ItemRarity.RARE,
                previewImageUrl = "bg_independence_hall"
            ),
            ShopItem(
                id = "bg_mount_rushmore",
                name = "Mount Rushmore",
                description = "Stand among the great presidents",
                category = ShopItemCategory.PROFILE_BACKGROUND,
                price = 1200,
                rarity = ItemRarity.EPIC,
                previewImageUrl = "bg_mount_rushmore"
            ),

            // Special Effects
            ShopItem(
                id = "effect_fireworks",
                name = "Patriotic Fireworks",
                description = "Celebrate with spectacular fireworks",
                category = ShopItemCategory.SPECIAL_EFFECT,
                price = 400,
                rarity = ItemRarity.UNCOMMON,
                previewImageUrl = "effect_fireworks"
            ),
            ShopItem(
                id = "effect_eagle_fly",
                name = "Eagle Flyby",
                description = "Majestic eagle animation for special moments",
                category = ShopItemCategory.SPECIAL_EFFECT,
                price = 1000,
                rarity = ItemRarity.EPIC,
                previewImageUrl = "effect_eagle_fly"
            ),

            // Eagly Outfits
            ShopItem(
                id = "eagly_presidential",
                name = "Presidential Eagly",
                description = "Dress Eagly in presidential attire",
                category = ShopItemCategory.EAGLY_OUTFIT,
                price = 750,
                rarity = ItemRarity.LEGENDARY,
                previewImageUrl = "eagly_presidential"
            ),
            ShopItem(
                id = "eagly_revolutionary",
                name = "Revolutionary Eagly",
                description = "Eagly joins the revolution",
                category = ShopItemCategory.EAGLY_OUTFIT,
                price = 600,
                rarity = ItemRarity.RARE,
                previewImageUrl = "eagly_revolutionary"
            ),

            // Eagly Accessories
            ShopItem(
                id = "eagly_acc_flag",
                name = "American Flag",
                description = "Let Eagly carry Old Glory",
                category = ShopItemCategory.EAGLY_ACCESSORY,
                price = 300,
                rarity = ItemRarity.UNCOMMON,
                previewImageUrl = "eagly_acc_flag"
            ),
            ShopItem(
                id = "eagly_acc_medal",
                name = "Medal of Freedom",
                description = "Honor Eagly with this prestigious medal",
                category = ShopItemCategory.EAGLY_ACCESSORY,
                price = 900,
                rarity = ItemRarity.EPIC,
                previewImageUrl = "eagly_acc_medal"
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: ShopRepository? = null

        fun getInstance(): ShopRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ShopRepository().also { INSTANCE = it }
            }
        }
    }
} 