package com.example.wethepeople.data.model

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val target: Int,
    val progress: Int = 0,
    val isDaily: Boolean,
    val isCompleted: Boolean = false,
    val type: MissionType
)

enum class MissionType {
    DEBATE,
    VOTE,
    SHARE,
    COMMENT,
    COLLECT,
    DONATE,
    VOLUNTEER
} 