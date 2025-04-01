package com.example.partyofthepeople.profile

import androidx.compose.ui.graphics.Color

/**
 * Data class for profile theme
 */
data class ProfileTheme(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
    val textColor: Color,
    val accentColor: Color,
    val backgroundColor: Color,
    val backgroundUrl: String? = null,
    val isLocked: Boolean = true
)

/**
 * Provides available profile themes for customization
 */
object ProfileThemes {
    private val themes = mapOf(
        "default" to ProfileTheme(
            id = "default",
            name = "Patriot",
            description = "Standard theme for all Freedom Fighters",
            price = 0,
            primaryColor = Color(0xFF1D3557),
            secondaryColor = Color(0xFFE63946),
            textColor = Color.White,
            accentColor = Color(0xFFF1FAEE),
            backgroundColor = Color(0xFF457B9D),
            isLocked = false
        ),
        "dark_mode" to ProfileTheme(
            id = "dark_mode",
            name = "Dark Mode",
            description = "For the night owls among us",
            price = 50,
            primaryColor = Color(0xFF121212),
            secondaryColor = Color(0xFF333333),
            textColor = Color.White,
            accentColor = Color(0xFF666666),
            backgroundColor = Color(0xFF000000)
        ),
        "retro" to ProfileTheme(
            id = "retro",
            name = "Retro",
            description = "Celebrate democracy with 80s style",
            price = 100,
            primaryColor = Color(0xFF800080),
            secondaryColor = Color(0xFF00FFFF),
            textColor = Color.White,
            accentColor = Color(0xFFFF00FF),
            backgroundColor = Color(0xFF000080)
        ),
        "gold" to ProfileTheme(
            id = "gold",
            name = "Gold Elite",
            description = "For true patriots",
            price = 500,
            primaryColor = Color(0xFFFFD700),
            secondaryColor = Color(0xFF663300),
            textColor = Color.Black,
            accentColor = Color(0xFFFFA500),
            backgroundColor = Color(0xFF8B4513)
        ),
        "freedom" to ProfileTheme(
            id = "freedom",
            name = "Freedom",
            description = "Red, White, and Blue all over",
            price = 250,
            primaryColor = Color(0xFF0A3161),
            secondaryColor = Color(0xFFB31942),
            textColor = Color.White,
            accentColor = Color(0xFFFFFFFF),
            backgroundColor = Color(0xFF041E42)
        )
    )
    
    fun getThemeById(id: String): ProfileTheme {
        return themes[id] ?: themes["default"]!!
    }
    
    fun getAllThemes(): List<ProfileTheme> {
        return themes.values.toList()
    }
} 