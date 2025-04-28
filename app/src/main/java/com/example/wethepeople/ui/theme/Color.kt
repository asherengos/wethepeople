package com.example.wethepeople.ui.theme

import androidx.compose.ui.graphics.Color

// Patriotic color scheme - enhanced for dark theme
val patriot_dark_blue = Color(0xFF08132A)      // Deeper navy blue for backgrounds
val patriot_medium_blue = Color(0xFF18325A)    // More vibrant medium blue for surfaces
val patriot_blue = Color(0xFF3F51B5)           // Vibrant blue for accents
val patriot_blue_light = Color(0xFF5D71E5)     // Light blue for highlights

val patriot_red = Color(0xFFE63946)            // Deep red
val patriot_red_bright = Color(0xFFFF3B4D)     // More vivid bright red for buttons/actions
val patriot_red_light = Color(0xFFF05545)      // Light red for highlights

// Gold accent colors
val patriot_gold = Color(0xFFFFC700)           // Richer gold for badges/achievements
val patriot_gold_light = Color(0xFFFFE57F)     // Light gold for highlights

// Neutral colors
val patriot_white = Color(0xFFF8F9FA)          // Slightly brighter white
val patriot_off_white = Color(0xFFF5F5F5)      // Slightly off-white
val patriot_light_gray = Color(0xFFE0E0E0)     // Light gray
val patriot_dark_gray = Color(0xFF616161)      // Dark gray
val patriot_black = Color(0xFF121212)          // Off-black for true black elements

// Background and surface colors
val background_dark = patriot_dark_blue        // Navy blue background
val surface_dark = patriot_medium_blue         // Medium blue surface
val background_light = patriot_off_white       // Off-white background
val surface_light = patriot_white              // White surface

// Text colors
val text_primary_dark = patriot_white          // White text for dark theme
val text_secondary_dark = patriot_light_gray   // Light gray text for dark theme
val text_primary_light = patriot_dark_blue     // Dark blue text for light theme
val text_secondary_light = patriot_dark_gray   // Dark gray text for light theme

// Status bar colors
val status_bar_dark = patriot_black            // Black status bar for dark theme
val status_bar_light = patriot_blue            // Blue status bar for light theme

// Define the missing ComposeColors object
object ComposeColors {
    // Primary colors
    val PatriotBlue = patriot_blue
    val PatriotRed = patriot_red
    val PatriotDarkBlue = patriot_dark_blue
    
    // Accent colors
    val AccentGold = patriot_gold
    val LibertyGold = patriot_gold
    
    // Background colors
    val DarkBackground = patriot_dark_blue
    val LightBackground = patriot_off_white
    
    // Text colors
    val NeonWhite = patriot_white
    val AmericanWhite = patriot_off_white
    
    // Action colors
    val PatriotPointsColor = Color(0xFF4CAF50)    // Green for points
    val Gold = patriot_gold
    val PatriotLightBlue = patriot_blue_light
    val PatriotGreen = Color(0xFF4CAF50)         // Green for success/positive actions
    
    // Map colors
    val StateMapBackground = patriot_light_gray
    val DarkSecondary = patriot_medium_blue
    val StateLightBlue = patriot_blue_light
}

// Legacy colors - keeping for compatibility
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Semantic colors
val success_green = Color(0xFF4CAF50)
val warning_yellow = Color(0xFFFFC107)
val error_red = Color(0xFFFF5252)

// Typography scale (sp)
val font_xs = 12
val font_sm = 14
val font_md = 16
val font_lg = 20
val font_xl = 24
val font_2xl = 32

// Spacing scale (dp)
val space_xs = 4
val space_sm = 8
val space_md = 16
val space_lg = 24
val space_xl = 32
val space_2xl = 48

// Category-Specific Colors
val category_environment_bg = Color(0xFF2E7D32) // Dark Green
val category_economy_bg = Color(0xFF6F5D45) // Earthy Gold/Brown
val category_education_bg = Color(0xFF0277BD) // Medium Blue
val category_public_safety_bg = Color(0xFFB71C1C) // Dark Red
val category_electoral_reform_bg = Color(0xFF4A148C) // Purple
val category_infrastructure_bg = Color(0xFF424242) // Dark Gray
val category_economy_yellow = Color(0xFFFFC107) // Amber/Yellow
val category_default_bg = patriot_medium_blue

// Support/Oppose Colors
val patriot_green_support = Color(0xFF4CAF50) // Standard Green

// Add Material colors for backward compatibility
val Blue700 = Color(0xFF1976D2)
val Red700 = Color(0xFFC62828)

// Removed the misleading comment