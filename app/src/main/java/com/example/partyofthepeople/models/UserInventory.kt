package com.example.partyofthepeople.models

/**
 * Represents a user's inventory in the shop.
 */
data class UserInventory(
    val userId: String = "",
    val items: Map<String, InventoryItem> = mapOf()
) 