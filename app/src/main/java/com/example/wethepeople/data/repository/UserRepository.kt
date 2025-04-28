package com.example.wethepeople.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.wethepeople.data.local.AppDatabase
import com.example.wethepeople.data.local.entities.EquippedItem
import com.example.wethepeople.data.local.entities.OwnedItem
import com.example.wethepeople.data.local.entities.UserData
import com.example.wethepeople.data.model.ShopItem
import com.example.wethepeople.data.model.CommentIdentifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository for managing user data including their emoji identity
 */
class UserRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val database = AppDatabase.getInstance(context)
    private val userDao = database.userDao()
    private val currentUserId = "current_user" // TODO: Replace with actual user authentication

    private val _freedomBucksBalance = MutableStateFlow(0)
    val freedomBucksBalance: StateFlow<Int> = _freedomBucksBalance.asStateFlow()

    private val _ownedItems = MutableStateFlow<Set<String>>(emptySet())
    val ownedItems: StateFlow<Set<String>> = _ownedItems.asStateFlow()

    private val _equippedItems = MutableStateFlow<Map<String, String>>(emptyMap())
    val equippedItems: StateFlow<Map<String, String>> = _equippedItems.asStateFlow()
    
    // Add StateFlows for user profile data to avoid synchronous reads
    private val _userEmojiId = MutableStateFlow("")
    val userEmojiId: StateFlow<String> = _userEmojiId.asStateFlow()
    
    private val _userRank = MutableStateFlow(DEFAULT_RANK)
    val userRank: StateFlow<String> = _userRank.asStateFlow()
    
    private val _userLevel = MutableStateFlow(1)
    val userLevel: StateFlow<Int> = _userLevel.asStateFlow()

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        USER_PREFERENCES, Context.MODE_PRIVATE
    )

    init {
        coroutineScope.launch {
            // Load initial user data
            userDao.getUserData(currentUserId).collect { userData ->
                _freedomBucksBalance.value = userData?.freedomBucks ?: 0
            }

            // Load owned items
            userDao.getOwnedItems(currentUserId).collect { items ->
                _ownedItems.value = items.map { it.id }.toSet()
            }

            // Load equipped items
            userDao.getEquippedItems(currentUserId).collect { items ->
                _equippedItems.value = items.associate { it.category to it.itemId }
            }
            
            // Load user profile data asynchronously
            loadUserProfileData()
        }
    }
    
    // Load all user profile data from SharedPreferences asynchronously
    private suspend fun loadUserProfileData() = withContext(Dispatchers.IO) {
        val emojiId = sharedPreferences.getString(KEY_EMOJI_ID, null)
        val rank = sharedPreferences.getString(KEY_USER_RANK, DEFAULT_RANK) ?: DEFAULT_RANK
        val level = sharedPreferences.getInt(KEY_USER_LEVEL, 1)
        
        if (emojiId == null) {
            // Generate a new emoji ID for this user and save it
            val newEmojiId = CommentIdentifier.generateUniqueEmojiId()
            sharedPreferences.edit().putString(KEY_EMOJI_ID, newEmojiId).apply()
            _userEmojiId.value = newEmojiId
        } else {
            _userEmojiId.value = emojiId
        }
        
        _userRank.value = rank
        _userLevel.value = level
    }

    suspend fun addFreedomBucks(amount: Int) {
        userDao.updateFreedomBucks(currentUserId, amount)
        _freedomBucksBalance.value += amount
        // TODO: Sync with backend
    }

    suspend fun spendFreedomBucks(amount: Int): Boolean {
        if (_freedomBucksBalance.value >= amount) {
            userDao.updateFreedomBucks(currentUserId, -amount)
            _freedomBucksBalance.value -= amount
            // TODO: Sync with backend
            return true
        }
        return false
    }

    suspend fun purchaseItem(item: ShopItem): Boolean {
        if (spendFreedomBucks(item.price)) {
            userDao.insertOwnedItem(OwnedItem(item.id, currentUserId))
            _ownedItems.value = _ownedItems.value + item.id
            // TODO: Sync with backend
            return true
        }
        return false
    }

    suspend fun equipItem(item: ShopItem) {
        if (_ownedItems.value.contains(item.id)) {
            userDao.unequipItemInCategory(item.category.name, currentUserId)
            userDao.insertEquippedItem(
                EquippedItem(item.category.name, item.id, currentUserId)
            )
            _equippedItems.value = _equippedItems.value + (item.category.name to item.id)
            // TODO: Sync with backend
        }
    }

    suspend fun unequipItem(item: ShopItem) {
        userDao.unequipItemInCategory(item.category.name, currentUserId)
        _equippedItems.value = _equippedItems.value - item.category.name
        // TODO: Sync with backend
    }

    fun isItemOwned(itemId: String): Boolean {
        return _ownedItems.value.contains(itemId)
    }

    fun isItemEquipped(itemId: String): Boolean {
        return _equippedItems.value.containsValue(itemId)
    }

    /**
     * Get the current user's emoji identifier from the StateFlow (non-blocking)
     */
    fun getUserEmojiId(): String {
        return _userEmojiId.value
    }
    
    /**
     * Get the user's current rank from the StateFlow (non-blocking)
     */
    fun getUserRank(): String {
        return _userRank.value
    }
    
    /**
     * Get the user's current level from the StateFlow (non-blocking)
     */
    fun getUserLevel(): Int {
        return _userLevel.value
    }
    
    /**
     * Get the user's display name which combines emoji ID and rank (non-blocking)
     */
    fun getUserDisplayName(useFullName: Boolean = true): String {
        val emojiId = _userEmojiId.value
        return if (useFullName) {
            CommentIdentifier.createDisplayName(emojiId, _userRank.value, _userLevel.value)
        } else {
            CommentIdentifier.createShortDisplayName(emojiId)
        }
    }
    
    /**
     * Update the user's rank asynchronously
     */
    fun updateUserRank(newRank: String) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putString(KEY_USER_RANK, newRank).apply()
            }
            _userRank.value = newRank
        }
    }
    
    /**
     * Update the user's level asynchronously
     */
    fun updateUserLevel(newLevel: Int) {
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putInt(KEY_USER_LEVEL, newLevel).apply()
            }
            _userLevel.value = newLevel
        }
    }
    
    /**
     * Reset user's emoji ID (for testing purposes) asynchronously
     */
    fun resetEmojiId() {
        val newEmojiId = CommentIdentifier.generateUniqueEmojiId()
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                sharedPreferences.edit().putString(KEY_EMOJI_ID, newEmojiId).apply()
            }
            _userEmojiId.value = newEmojiId
        }
    }

    companion object {
        private const val USER_PREFERENCES = "user_preferences"
        private const val KEY_EMOJI_ID = "emoji_identifier"
        private const val KEY_USER_RANK = "user_rank"
        private const val KEY_USER_LEVEL = "user_level"
        
        private const val DEFAULT_RANK = "Citizen"
        
        // Singleton instance
        @Volatile
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(context: Context): UserRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
} 