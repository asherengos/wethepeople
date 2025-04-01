package com.example.partyofthepeople.utils

import com.example.partyofthepeople.shop.CurrencyType
import com.example.partyofthepeople.shop.ShopItemCategory
import com.example.partyofthepeople.shop.ShopItemEffect
import com.example.partyofthepeople.shop.ShopItem
import com.example.partyofthepeople.shop.UserInventory
import com.example.partyofthepeople.shop.InventoryItem
import com.example.partyofthepeople.shop.CurrencyTransaction
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreConverters {
    
    // Convert Firestore DocumentSnapshot to ShopItem
    fun toShopItem(doc: DocumentSnapshot): ShopItem? {
        val data = doc.data ?: return null
        
        return ShopItem(
            id = doc.id,
            name = data["name"] as? String ?: "",
            description = data["description"] as? String ?: "",
            price = (data["price"] as? Number)?.toInt() ?: 0,
            currency = CurrencyType.valueOf(data["currency"] as? String ?: CurrencyType.FREEDOM_BUCKS.name),
            category = ShopItemCategory.valueOf(data["category"] as? String ?: ShopItemCategory.GENERAL.name),
            effectType = ShopItemEffect.valueOf(data["effectType"] as? String ?: ShopItemEffect.NONE.name),
            effectValue = (data["effectValue"] as? Number)?.toInt() ?: 0,
            available = data["available"] as? Boolean ?: true,
            stockRemaining = (data["stockRemaining"] as? Number)?.toInt(),
            limitedTimeUntil = data["limitedTimeUntil"] as? Long,
            discountPercentage = (data["discountPercentage"] as? Number)?.toInt() ?: 0
        )
    }
    
    // Convert Firestore DocumentSnapshot to UserInventory
    fun toUserInventory(document: DocumentSnapshot): UserInventory? {
        val data = document.data ?: return null
        
        val itemsData = data["items"] ?: return null
        if (itemsData !is Map<*, *>) return null
        
        val itemsMap = itemsData.mapKeys { it.key.toString() }
            .mapValues { (_, itemData) ->
                if (itemData !is Map<*, *>) null
                else InventoryItem(
                    itemId = (itemData["itemId"] as? String) ?: "",
                    quantity = (itemData["quantity"] as? Number)?.toInt() ?: 0,
                    acquiredAt = (itemData["acquiredAt"] as? Number)?.toLong() ?: 0,
                    expiresAt = (itemData["expiresAt"] as? Number)?.toLong(),
                    used = (itemData["used"] as? Number)?.toInt() ?: 0
                )
            }
            .filterValues { it != null }
            .mapValues { it.value!! }
        
        return UserInventory(
            userId = document.id,
            items = itemsMap
        )
    }
    
    // Convert Firestore DocumentSnapshot to CurrencyTransaction
    fun toCurrencyTransaction(document: DocumentSnapshot): CurrencyTransaction? {
        val data = document.data ?: return null
        
        return CurrencyTransaction(
            id = document.id,
            userId = data["userId"] as? String ?: "",
            type = CurrencyType.valueOf(data["type"] as? String ?: CurrencyType.FREEDOM_BUCKS.name),
            amount = (data["amount"] as? Number)?.toInt() ?: 0,
            reason = data["reason"] as? String ?: "",
            timestamp = data["timestamp"] as? Long ?: 0,
            isSpending = data["isSpending"] as? Boolean ?: false,
            relatedItemId = data["relatedItemId"] as? String
        )
    }
}

// Extension functions to simplify data fetching
suspend fun FirebaseFirestore.getShopItems(category: ShopItemCategory? = null): List<ShopItem> {
    val query = collection("shop").document("catalog").collection("items").let { 
        if (category != null) {
            it.whereEqualTo("category", category.name)
        } else {
            it
        }
    }
    
    return query.get().await().documents.mapNotNull { 
        FirestoreConverters.toShopItem(it)
    }
}

suspend fun FirebaseFirestore.getUserInventory(userId: String): UserInventory? {
    val document = collection("users").document(userId)
        .collection("inventory").document("items").get().await()
    
    return FirestoreConverters.toUserInventory(document)
} 