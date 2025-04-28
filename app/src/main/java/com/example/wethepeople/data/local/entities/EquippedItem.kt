package com.example.wethepeople.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipped_items")
data class EquippedItem(
    @PrimaryKey val category: String,
    val itemId: String,
    val userId: String
) 