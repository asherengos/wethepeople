package com.example.partyofthepeople.utils

import android.util.Log
import com.example.partyofthepeople.shop.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Utility class to populate the shop with initial items.
 * This should be called only once, typically from an admin screen or during first launch.
 */
object ShopDataPopulator {
    private const val TAG = "ShopDataPopulator"
    
    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun populateShopItems() {
        scope.launch {
            try {
                // Check if items already exist
                val existingItems = db.collection("shop_items").get().await()
                if (!existingItems.isEmpty) {
                    return@launch
                }

                // Add items in batches
                val batch = db.batch()
                
                // Power items
                val superVote = ShopItem(
                    name = "Super Vote",
                    description = "Your vote counts as 2 votes for 24 hours",
                    price = 100,
                    currency = CurrencyType.FREEDOM_BUCKS,
                    category = ShopItemCategory.POWER,
                    effectType = ShopItemEffect.SUPER_VOTE,
                    effectValue = 2,
                    stockRemaining = 100
                )
                batch.set(db.collection("shop_items").document(), superVote)

                // ... rest of the items ...

                // Execute batch
                batch.commit()
            } catch (e: Exception) {
                println("Error populating shop items: ${e.message}")
            }
        }
    }
    
    private fun createShopItems(): List<ShopItem> {
        val items = mutableListOf<ShopItem>()
        
        // Power Items
        items.add(
            ShopItem(
                id = "super_vote",
                name = "Super Vote",
                description = "Your vote counts 2x on any law (Democracy optional)",
                price = 50,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.POWER,
                effectType = ShopItemEffect.SUPER_VOTE,
                effectValue = 2,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "impeachment_shield",
                name = "Impeachment Shield",
                description = "Block one impeachment attempt against you",
                price = 100,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.POWER,
                effectType = ShopItemEffect.IMPEACHMENT_SHIELD,
                effectValue = 1,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "executive_order",
                name = "Executive Order",
                description = "Skip debate phase on your proposed law",
                price = 75,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.POWER,
                effectType = ShopItemEffect.EXECUTIVE_ORDER,
                effectValue = 1,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "filibuster_pass",
                name = "Filibuster Pass",
                description = "Comment twice in a row in debates",
                price = 30,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.POWER,
                effectType = ShopItemEffect.FILIBUSTER_PASS,
                effectValue = 1,
                stockRemaining = -1
            )
        )
        
        // Cosmetic Items
        items.add(
            ShopItem(
                id = "oligarch_theme",
                name = "Oligarch Theme",
                description = "Gold profile border and diamond flag",
                price = 200,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.COSMETIC,
                effectType = ShopItemEffect.PROFILE_THEME,
                effectValue = 0,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "tax_evader_title",
                name = "Tax Evader Title",
                description = "Special display name prefix",
                price = 75,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.COSMETIC,
                effectType = ShopItemEffect.PROFILE_TITLE,
                effectValue = 0,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "deep_state_badge",
                name = "Deep State Badge",
                description = "Mysterious eye icon on profile",
                price = 100,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.COSMETIC,
                effectType = ShopItemEffect.PROFILE_BADGE,
                effectValue = 0,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "freedom_warrior_avatar",
                name = "Freedom Warrior Avatar Frame",
                description = "Eagle-themed profile frame",
                price = 50,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.COSMETIC,
                effectType = ShopItemEffect.PROFILE_THEME,
                effectValue = 0,
                stockRemaining = -1
            )
        )
        
        // Weapon Items
        items.add(
            ShopItem(
                id = "irs_audit",
                name = "IRS Audit",
                description = "Steal 10% of target user's FB balance",
                price = 30,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.WEAPON,
                effectType = ShopItemEffect.AUDIT_USER,
                effectValue = 10,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "debate_bomb",
                name = "Debate Bomb",
                description = "Auto-downvote a comment 5 times",
                price = 75,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.WEAPON,
                effectType = ShopItemEffect.DEBATE_BOMB,
                effectValue = 5,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "troll_farm",
                name = "Troll Farm",
                description = "Generate fake supporters on your law",
                price = 120,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.WEAPON,
                effectType = ShopItemEffect.TROLL_FARM,
                effectValue = 10,
                stockRemaining = -1
            )
        )
        
        items.add(
            ShopItem(
                id = "media_blackout",
                name = "Media Blackout",
                description = "Hide a law from feed for 1 hour",
                price = 40,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.WEAPON,
                effectType = ShopItemEffect.MEDIA_BLACKOUT,
                effectValue = 60, // Minutes
                stockRemaining = -1
            )
        )
        
        // Limited-Time Items
        val oneWeekFromNow = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
        
        items.add(
            ShopItem(
                id = "fourth_reich_bundle",
                name = "Fourth Reich Bundle",
                description = "Dictator hat + ban hammer GIF (Edge lord starter pack)",
                price = 500,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.LIMITED,
                effectType = ShopItemEffect.BUNDLE,
                effectValue = 0,
                limitedTimeUntil = oneWeekFromNow,
                stockRemaining = 10
            )
        )
        
        items.add(
            ShopItem(
                id = "corporate_overlord_pack",
                name = "Corporate Overlord Pack",
                description = "Business theme + tax breaks (Capitalism at its finest)",
                price = 300,
                currency = CurrencyType.PATRIOT_POINTS,
                category = ShopItemCategory.LIMITED,
                effectType = ShopItemEffect.BUNDLE,
                effectValue = 0,
                limitedTimeUntil = oneWeekFromNow,
                stockRemaining = 15
            )
        )
        
        items.add(
            ShopItem(
                id = "freedom_fighter_kit",
                name = "Freedom Fighter Kit",
                description = "Rebel avatar + immunity to audits (Fight the power!)",
                price = 200,
                currency = CurrencyType.FREEDOM_BUCKS,
                category = ShopItemCategory.LIMITED,
                effectType = ShopItemEffect.BUNDLE,
                effectValue = 0,
                limitedTimeUntil = oneWeekFromNow,
                stockRemaining = 20
            )
        )
        
        return items
    }
} 