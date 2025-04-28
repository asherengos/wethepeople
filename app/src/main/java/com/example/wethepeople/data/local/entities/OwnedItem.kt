package com.example.wethepeople.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owned_items")
data class OwnedItem(
    @PrimaryKey val id: String,
    val userId: String,
    val purchaseDate: Long = System.currentTimeMillis()
) 