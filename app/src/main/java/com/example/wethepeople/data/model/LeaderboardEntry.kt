package com.example.wethepeople.data.model

data class LeaderboardEntry(
    val userId: String,
    val username: String,
    val patriotPoints: Int,
    val citizenRank: String,
    val customEagley: EagleyAppearance? = null
) 