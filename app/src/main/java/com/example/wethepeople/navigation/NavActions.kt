package com.example.wethepeople.navigation

import androidx.navigation.NavHostController
import com.example.wethepeople.navigation.Screen

/**
 * Navigation actions for the app
 */
class NavActions(private val navController: NavHostController) {
    
    fun navigateToScaffold() {
        navController.navigate(Screen.Scaffold.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    fun navigateUp(): Boolean {
        return navController.navigateUp()
    }
} 