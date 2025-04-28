package com.example.wethepeople.data.model

import androidx.compose.ui.graphics.Color

enum class CitizenRank(
    val title: String,
    val minPoints: Int,
    val description: String,
    val icon: String,
    val color: Long
) {
    CANVASSER(
        title = "CANVASSER",
        minPoints = 0,
        description = "Going door to door for democracy",
        icon = "📋",
        color = 0xFF8D6E63  // Brown
    ),
    VOTER(
        title = "VOTER",
        minPoints = 100,
        description = "Taking the first steps in democratic participation",
        icon = "🗳️",
        color = 0xFF7E57C2  // Deep Purple
    ),
    CITIZEN(
        title = "CITIZEN",
        minPoints = 250,
        description = "A proud member of the democratic process",
        icon = "📜",
        color = 0xFF5C6BC0  // Indigo
    ),
    PATRIOT(
        title = "PATRIOT",
        minPoints = 500,
        description = "Dedicated to the principles of freedom",
        icon = "🎖️",
        color = 0xFF42A5F5  // Blue
    ),
    MINUTEMAN(
        title = "MINUTEMAN",
        minPoints = 750,
        description = "Always ready to defend liberty",
        icon = "⚔️",
        color = 0xFF26A69A  // Teal
    ),
    CONGRESSMAN(
        title = "CONGRESSMAN",
        minPoints = 1000,
        description = "A voice of the people",
        icon = "🏛️",
        color = 0xFF66BB6A  // Green
    ),
    SENATOR(
        title = "SENATOR",
        minPoints = 1250,
        description = "A distinguished leader in the halls of democracy",
        icon = "⭐",
        color = 0xFFFFCA28  // Amber
    ),
    GOVERNOR(
        title = "GOVERNOR",
        minPoints = 1500,
        description = "Executive power in service of the people",
        icon = "👔",
        color = 0xFFFFA726  // Orange
    ),
    FOUNDING_FATHER(
        title = "FOUNDING FATHER",
        minPoints = 1776,
        description = "A legendary figure in the pursuit of liberty",
        icon = "👑",
        color = 0xFFF44336  // Red
    );

    companion object {
        fun fromPoints(points: Int): CitizenRank {
            return values().sortedByDescending { it.minPoints }
                .first { points >= it.minPoints }
        }

        fun getNextRank(currentRank: CitizenRank): CitizenRank? {
            val currentIndex = values().indexOf(currentRank)
            return if (currentIndex < values().size - 1) values()[currentIndex + 1] else null
        }

        fun getPointsToNextRank(currentPoints: Int): Int? {
            val currentRank = fromPoints(currentPoints)
            val nextRank = getNextRank(currentRank) ?: return null
            return nextRank.minPoints - currentPoints
        }
    }
} 