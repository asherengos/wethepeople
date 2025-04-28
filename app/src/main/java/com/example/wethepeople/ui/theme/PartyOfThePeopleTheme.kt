package com.example.wethepeople.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Patriotic dark theme color scheme
private val PatrioticDarkColorScheme = darkColorScheme(
    primary = patriot_blue_light,        // Bright blue for primary actions
    onPrimary = patriot_white,           // White text on primary
    
    secondary = patriot_red_bright,      // Bright red for secondary actions
    onSecondary = patriot_white,         // White text on secondary
    
    tertiary = patriot_gold,             // Gold for tertiary/special actions
    onTertiary = patriot_black,          // Black text on gold for readability
    
    background = patriot_dark_blue,      // Deep navy blue background
    onBackground = patriot_white,        // White text on background
    
    surface = patriot_medium_blue,       // Medium navy for surface components
    onSurface = patriot_white,           // White text on surface
    
    surfaceVariant = patriot_medium_blue.copy(alpha = 0.7f), // Lighter navy for surface variants
    onSurfaceVariant = patriot_light_gray,                  // Light gray text on surface variants
    
    error = patriot_red_light,           // Red for errors
    onError = patriot_white,             // White text on error
    
    outline = patriot_light_gray.copy(alpha = 0.6f)        // Light gray outlines with transparency
)

// Light theme color scheme
private val PatrioticLightColorScheme = lightColorScheme(
    primary = patriot_blue,              // Standard blue for primary actions
    onPrimary = patriot_white,           // White text on primary
    
    secondary = patriot_red,             // Standard red for secondary actions
    onSecondary = patriot_white,         // White text on secondary
    
    tertiary = patriot_gold,             // Gold for tertiary/special actions
    onTertiary = patriot_black,          // Black text on gold for readability
    
    background = patriot_off_white,      // Off-white background
    onBackground = patriot_dark_blue,    // Dark blue text on background
    
    surface = patriot_white,             // White surface components
    onSurface = patriot_dark_blue,       // Dark blue text on surface
    
    surfaceVariant = patriot_light_gray, // Light gray for surface variants
    onSurfaceVariant = patriot_dark_blue,// Dark blue text on surface variants
    
    error = patriot_red,                 // Red for errors
    onError = patriot_white,             // White text on error
    
    outline = patriot_dark_gray.copy(alpha = 0.6f)         // Dark gray outlines with transparency
)

@Composable
fun PartyOfThePeopleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    forceDarkTheme: Boolean = false,
    dynamicColor: Boolean = false,  // Default to false to use our custom color scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme || forceDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme || forceDarkTheme -> PatrioticDarkColorScheme
        else -> PatrioticLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color based on theme
            window.statusBarColor = (if (darkTheme || forceDarkTheme) patriot_black else patriot_blue).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !(darkTheme || forceDarkTheme)
            
            // Also set navigation bar color for full immersion
            window.navigationBarColor = (if (darkTheme || forceDarkTheme) patriot_black else patriot_blue).toArgb()
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 