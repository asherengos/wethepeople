package com.example.wethepeople.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey val userId: String,
    @ColumnInfo(name = "freedom_bucks") val freedomBucks: Int,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long = System.currentTimeMillis()
) 