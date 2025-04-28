package com.example.wethepeople.data.local.dao

import androidx.room.*
import com.example.wethepeople.data.local.entities.UserData
import com.example.wethepeople.data.local.entities.OwnedItem
import com.example.wethepeople.data.local.entities.EquippedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface UserDao {
    @Query("SELECT * FROM user_data WHERE userId = :userId")
    fun getUserData(userId: String): Flow<UserData?>

    @Query("SELECT * FROM owned_items WHERE userId = :userId")
    fun getOwnedItems(userId: String): Flow<List<OwnedItem>>

    @Query("SELECT * FROM equipped_items WHERE userId = :userId")
    fun getEquippedItems(userId: String): Flow<List<EquippedItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserData(userData: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedItem(ownedItem: OwnedItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquippedItem(equippedItem: EquippedItem)

    @Query("DELETE FROM equipped_items WHERE category = :category AND userId = :userId")
    suspend fun unequipItemInCategory(category: String, userId: String)

    @Transaction
    suspend fun updateFreedomBucks(userId: String, amount: Int) {
        val currentData = getUserData(userId).first() ?: UserData(userId, 0)
        insertUserData(currentData.copy(freedomBucks = currentData.freedomBucks + amount))
    }
} 