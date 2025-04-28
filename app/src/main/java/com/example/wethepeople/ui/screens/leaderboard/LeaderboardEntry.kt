package com.example.wethepeople.ui.screens.leaderboard

import androidx.annotation.DrawableRes

/**
 * Represents an entry in the leaderboard UI
 */
data class UILeaderboardEntry(
    val id: String,
    val username: String,
    val rank: Int,
    val score: Int,
    val avatarUrl: String? = null,
    val isFriend: Boolean = false,
    val isLocal: Boolean = false,
    val isCurrentUser: Boolean = false
) 