package com.example.partyofthepeople.models

data class InventoryItem(
    val itemId: String = "",
    val quantity: Int = 1,
    val used: Int = 0,
    val acquiredAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null
)