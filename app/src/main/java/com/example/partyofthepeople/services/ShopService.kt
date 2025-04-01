package com.example.partyofthepeople.services

import android.util.Log
import com.example.partyofthepeople.UserProfile
import com.example.partyofthepeople.shop.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.math.min

class ShopService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val TAG = "ShopService"
    private val shopCollection = firestore.collection("shop")
    private val itemsCollection = shopCollection.document("catalog").collection("items")
    private val usersCollection = firestore.collection("users")
    
    // Get all shop items with optional category filter
    suspend fun getShopItems(category: ShopItemCategory? = null): List<ShopItem> {
        try {
            var query: Query = itemsCollection
            
            if (category != null) {
                query = query.whereEqualTo("category", category)
            }
            
            // Only show available items
            query = query.whereEqualTo("available", true)
            
            // Filter out expired limited-time offers
            val currentTime = System.currentTimeMillis()
            
            // Split into two queries - one for items with no expiration, one for non-expired items
            val nullLimitQuery = query.whereEqualTo("limitedTimeUntil", null)
            val validLimitQuery = query.whereGreaterThan("limitedTimeUntil", currentTime)
            
            val nullLimitSnapshot = nullLimitQuery.get().await()
            val validLimitSnapshot = validLimitQuery.get().await()
            
            val result = mutableListOf<ShopItem>()
            result.addAll(nullLimitSnapshot.toObjects(ShopItem::class.java))
            result.addAll(validLimitSnapshot.toObjects(ShopItem::class.java))
            
            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error getting shop items", e)
            return emptyList()
        }
    }
    
    // Get limited-time offers
    suspend fun getLimitedTimeOffers(): List<ShopItem> {
        try {
            val currentTime = System.currentTimeMillis()
            val query: Query = itemsCollection
                .whereGreaterThan("limitedTimeUntil", currentTime)
                .whereEqualTo("available", true)
                
            val snapshot = query.get().await()
                
            return snapshot.toObjects(ShopItem::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting limited-time offers", e)
            return emptyList()
        }
    }
    
    // Get user inventory
    suspend fun getUserInventory(userId: String): UserInventory {
        try {
            val snapshot = usersCollection.document(userId)
                .collection("inventory")
                .document("items")
                .get()
                .await()
                
            return snapshot.toObject(UserInventory::class.java) ?: UserInventory(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user inventory", e)
            return UserInventory(userId)
        }
    }
    
    // Purchase an item
    suspend fun purchaseItem(userId: String, itemId: String): PurchaseResult {
        try {
            // Get the item
            val itemSnapshot = itemsCollection.document(itemId).get().await()
            val item = itemSnapshot.toObject(ShopItem::class.java)
                ?: return PurchaseResult.Failure("Item not found")
            
            // Check if item is available
            if (!item.available) {
                return PurchaseResult.Failure("Item is not available")
            }
            
            // Check if limited-time offer is expired
            if (item.limitedTimeUntil != null && item.limitedTimeUntil < System.currentTimeMillis()) {
                return PurchaseResult.Failure("Limited-time offer expired")
            }
            
            // Check if out of stock
            if (item.stockRemaining != null && item.stockRemaining <= 0) {
                return PurchaseResult.Failure("Item out of stock")
            }
            
            // Get user profile
            val userSnapshot = usersCollection.document(userId).get().await()
            val userProfile = userSnapshot.toObject(UserProfile::class.java)
                ?: return PurchaseResult.Failure("User not found")
            
            // Check if user has enough currency
            val userBalance = when (item.currency) {
                CurrencyType.PATRIOT_POINTS -> userProfile.patriotPoints
                CurrencyType.FREEDOM_BUCKS -> userProfile.freedomBucks
            }
            
            if (userBalance < item.price) {
                return PurchaseResult.Failure("Insufficient ${item.currency} balance")
            }
            
            // Update user inventory
            val inventoryRef = usersCollection.document(userId)
                .collection("inventory")
                .document("items")
            
            firestore.runTransaction { transaction ->
                // Get current inventory
                val inventorySnapshot = transaction.get(inventoryRef)
                val inventory = inventorySnapshot.toObject(UserInventory::class.java) 
                    ?: UserInventory(userId)
                
                // Update inventory
                val existingItem = inventory.items[itemId]
                val newQuantity = (existingItem?.quantity ?: 0) + 1
                
                val updatedItems = inventory.items.toMutableMap()
                updatedItems[itemId] = InventoryItem(
                    itemId = itemId,
                    quantity = newQuantity,
                    acquiredAt = System.currentTimeMillis(),
                    expiresAt = null, // Set expiry if needed
                    used = existingItem?.used ?: 0
                )
                
                // Update inventory document
                transaction.set(
                    inventoryRef,
                    UserInventory(userId, updatedItems)
                )
                
                // Update user currency
                val userRef = usersCollection.document(userId)
                when (item.currency) {
                    CurrencyType.PATRIOT_POINTS -> {
                        transaction.update(userRef, "patriotPoints", userProfile.patriotPoints - item.price)
                    }
                    CurrencyType.FREEDOM_BUCKS -> {
                        transaction.update(userRef, "freedomBucks", userProfile.freedomBucks - item.price)
                    }
                }
                
                // Update item stock if needed
                if (item.stockRemaining != null && item.stockRemaining > 0) {
                    transaction.update(
                        itemsCollection.document(itemId),
                        "stockRemaining",
                        item.stockRemaining - 1
                    )
                }
                
                // Record transaction
                val transactionId = UUID.randomUUID().toString()
                val transactionRef = usersCollection.document(userId)
                    .collection("transactions")
                    .document(transactionId)
                
                val currencyTransaction = CurrencyTransaction(
                    id = transactionId,
                    userId = userId,
                    type = item.currency,
                    amount = item.price,
                    reason = "Purchased ${item.name}",
                    timestamp = System.currentTimeMillis(),
                    isSpending = true,
                    relatedItemId = itemId
                )
                
                transaction.set(transactionRef, currencyTransaction)
            }.await()
            
            return PurchaseResult.Success(
                message = "Successfully purchased ${item.name}",
                item = item
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error purchasing item", e)
            return PurchaseResult.Failure("Error: ${e.message}")
        }
    }
    
    // Use an item from inventory
    suspend fun useItem(userId: String, itemId: String, targetId: String? = null): UseItemResult {
        try {
            // Get the item
            val itemSnapshot = itemsCollection.document(itemId).get().await()
            val item = itemSnapshot.toObject(ShopItem::class.java)
                ?: return UseItemResult.Failure("Item not found")
            
            // Get user inventory
            val inventoryRef = usersCollection.document(userId)
                .collection("inventory")
                .document("items")
            
            val inventorySnapshot = inventoryRef.get().await()
            val inventory = inventorySnapshot.toObject(UserInventory::class.java) 
                ?: return UseItemResult.Failure("Inventory not found")
            
            // Check if user has the item
            val inventoryItem = inventory.items[itemId] 
                ?: return UseItemResult.Failure("Item not in inventory")
            
            if (inventoryItem.quantity <= inventoryItem.used) {
                return UseItemResult.Failure("No unused items remaining")
            }
            
            // Apply item effect based on item type
            val effect = when (item.effectType) {
                ShopItemEffect.SUPER_VOTE -> {
                    // Super Vote logic - requires a proposal ID
                    if (targetId == null) {
                        return UseItemResult.Failure("Proposal ID required for Super Vote")
                    }
                    
                    // Apply super vote to the proposal
                    applySuperVote(userId, targetId, item.effectValue)
                }
                
                ShopItemEffect.IMPEACHMENT_SHIELD -> {
                    // Add impeachment shield to user
                    addUserPerk(userId, "impeachment_shield")
                }
                
                ShopItemEffect.PROFILE_THEME -> {
                    // Add theme to user's owned themes
                    addUserTheme(userId, itemId)
                }
                
                ShopItemEffect.PROFILE_BADGE -> {
                    // Add badge to user
                    addUserBadge(userId, itemId)
                }
                
                ShopItemEffect.PROFILE_TITLE -> {
                    // Add title to user
                    addUserTitle(userId, itemId)
                }
                
                ShopItemEffect.AUDIT_USER -> {
                    // Audit another user - requires target user ID
                    if (targetId == null) {
                        return UseItemResult.Failure("Target user ID required for Audit")
                    }
                    
                    // Apply audit to target user
                    auditUser(userId, targetId, item.effectValue)
                }
                
                ShopItemEffect.DEBATE_BOMB -> {
                    // Apply debate bomb to a comment - requires comment ID
                    if (targetId == null) {
                        return UseItemResult.Failure("Comment ID required for Debate Bomb")
                    }
                    
                    // Apply debate bomb to the comment
                    applyDebateBomb(userId, targetId, item.effectValue)
                }
                
                // Add other effect types as needed
                
                else -> {
                    return UseItemResult.Failure("Item effect not implemented")
                }
            }
            
            // Update user inventory to mark item as used
            val updatedItems = inventory.items.toMutableMap()
            updatedItems[itemId] = inventoryItem.copy(used = inventoryItem.used + 1)
            
            inventoryRef.update("items", updatedItems).await()
            
            return UseItemResult.Success(
                message = "Successfully used ${item.name}",
                item = item
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error using item", e)
            return UseItemResult.Failure("Error: ${e.message}")
        }
    }
    
    // Helper methods for applying item effects
    
    private suspend fun applySuperVote(userId: String, proposalId: String, voteMultiplier: Int): Boolean {
        try {
            // Apply super vote logic to the proposal
            // This will depend on how your voting system is implemented
            
            // Example: Add a vote multiplier to the user's vote on this proposal
            val voteRef = firestore.collection("proposals")
                .document(proposalId)
                .collection("votes")
                .document(userId)
            
            voteRef.update("multiplier", voteMultiplier).await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying super vote", e)
            return false
        }
    }
    
    private suspend fun addUserPerk(userId: String, perkId: String): Boolean {
        try {
            // Add perk to user's perks list
            usersCollection.document(userId)
                .update("perks", FieldValue.arrayUnion(perkId))
                .await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user perk", e)
            return false
        }
    }
    
    private suspend fun addUserTheme(userId: String, themeId: String): Boolean {
        try {
            // Add theme to user's owned themes
            usersCollection.document(userId)
                .update("ownedThemes", FieldValue.arrayUnion(themeId))
                .await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user theme", e)
            return false
        }
    }
    
    private suspend fun addUserBadge(userId: String, badgeId: String): Boolean {
        try {
            // Add badge to user's badges
            usersCollection.document(userId)
                .update("badges", FieldValue.arrayUnion(badgeId))
                .await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user badge", e)
            return false
        }
    }
    
    private suspend fun addUserTitle(userId: String, titleId: String): Boolean {
        try {
            // Add title to user's titles
            usersCollection.document(userId)
                .update("titles", FieldValue.arrayUnion(titleId))
                .await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding user title", e)
            return false
        }
    }
    
    private suspend fun auditUser(userId: String, targetUserId: String, percentage: Int): Boolean {
        try {
            // Get target user's Freedom Bucks
            val targetUserSnapshot = usersCollection.document(targetUserId).get().await()
            val targetUserProfile = targetUserSnapshot.toObject(UserProfile::class.java) ?: return false
            
            // Calculate amount to steal (percentage of target's Freedom Bucks)
            val amountToSteal = (targetUserProfile.freedomBucks * percentage / 100)
            
            // Update target user's Freedom Bucks
            usersCollection.document(targetUserId)
                .update("freedomBucks", targetUserProfile.freedomBucks - amountToSteal)
                .await()
            
            // Update auditor's Freedom Bucks
            usersCollection.document(userId)
                .update("freedomBucks", FieldValue.increment(amountToSteal.toLong()))
                .await()
            
            // Record transactions for both users
            val transactionId = UUID.randomUUID().toString()
            
            // Target user transaction (money taken)
            val targetTransaction = CurrencyTransaction(
                id = "${transactionId}_target",
                userId = targetUserId,
                type = CurrencyType.FREEDOM_BUCKS,
                amount = amountToSteal,
                reason = "Audited by another user",
                isSpending = true,
                relatedItemId = userId,
                timestamp = System.currentTimeMillis()
            )
            
            usersCollection.document(targetUserId)
                .collection("transactions")
                .document("${transactionId}_target")
                .set(targetTransaction)
                .await()
            
            // Auditor transaction (money received)
            val auditorTransaction = CurrencyTransaction(
                id = "${transactionId}_auditor",
                userId = userId,
                type = CurrencyType.FREEDOM_BUCKS,
                amount = amountToSteal,
                reason = "Audit proceeds from user",
                isSpending = false,
                relatedItemId = targetUserId,
                timestamp = System.currentTimeMillis()
            )
            
            usersCollection.document(userId)
                .collection("transactions")
                .document("${transactionId}_auditor")
                .set(auditorTransaction)
                .await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error auditing user", e)
            return false
        }
    }
    
    private suspend fun applyDebateBomb(userId: String, commentId: String, downvoteCount: Int): Boolean {
        try {
            // Apply debate bomb logic to the comment
            // This will depend on how your comment/debate system is implemented
            
            // Example: Add downvotes to the comment
            val commentRef = firestore.collection("comments").document(commentId)
            
            commentRef.update(
                "downvotes", FieldValue.increment(downvoteCount.toLong()),
                "bombedBy", FieldValue.arrayUnion(userId)
            ).await()
            
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error applying debate bomb", e)
            return false
        }
    }
}

// Result classes for shop operations
sealed class PurchaseResult {
    data class Success(val message: String, val item: ShopItem) : PurchaseResult()
    data class Failure(val message: String) : PurchaseResult()
}

sealed class UseItemResult {
    data class Success(val message: String, val item: ShopItem) : UseItemResult()
    data class Failure(val message: String) : UseItemResult()
} 