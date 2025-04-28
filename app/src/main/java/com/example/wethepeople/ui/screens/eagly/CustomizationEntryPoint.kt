package com.example.wethepeople.ui.screens.eagly

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wethepeople.R
import com.example.wethepeople.data.CustomizationRepository
import com.example.wethepeople.data.model.Accessory
import com.example.wethepeople.ui.viewmodel.EagleyViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/**
 * Main entry point for the Eagley HQ screen with drag-and-drop customization
 */
@Composable
fun CustomizationEntryPoint(navController: NavController? = null) {
    val context = LocalContext.current
    val viewModel: EagleyViewModel = viewModel()
    val sharedPreferences = remember { 
        context.getSharedPreferences("eagley_customization", 0) 
    }
    val repository = remember { CustomizationRepository(sharedPreferences) }
    val localNavController = navController ?: rememberNavController()

    // Show a toast when screen is first loaded
    LaunchedEffect(Unit) {
        Toast.makeText(context, "Welcome to Eagley HQ!", Toast.LENGTH_SHORT).show()
    }

    // Accessories with specific sizes
    val accessories = listOf(
        Accessory("flag", R.drawable.accessory_flag, initialSize = 100),
        Accessory("cowboy_hat", R.drawable.hat_cowboy, initialSize = 120),
        Accessory("shield", R.drawable.patriot_shield, initialSize = 80)
    )

    // Load saved customizations when screen is first launched
    LaunchedEffect(Unit) {
        try {
            val savedCustomizations = repository.loadCustomization()
            savedCustomizations.forEach { (id, customization) ->
                val (position, scale) = customization
                viewModel.updateAssetCustomization(id, position, scale)
            }
        } catch (e: Exception) {
            android.util.Log.e("EagleyHQ", "Error loading customizations: ${e.message}", e)
            // Fall back to older position-only data if needed
            val savedPositions = repository.loadPositions()
            savedPositions.forEach { (id, offset) ->
                viewModel.updateAssetPosition(id, offset)
            }
        }
    }

    // Use a state variable to track errors
    val errorState = remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    // If we have an error, show the error screen
    if (errorState.value != null) {
        Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "Error: ${errorState.value}",
                color = androidx.compose.ui.graphics.Color.White
            )
            androidx.compose.material3.Button(
                onClick = { errorState.value = null }
            ) {
                androidx.compose.material3.Text("Try Again")
            }
        }
    } else {
        // Show the drag-and-drop customization screen
        EaglyCustomizationScreen(
            viewModel = viewModel,
            accessories = accessories,
            baseResId = R.drawable.eagley_base,
            onSave = { customizations ->
                try {
                    repository.saveCustomization(customizations)
                    
                    // Signal back to the main screen that we've saved customizations
                    viewModel.notifyCustomizationSaved()
                    
                    Toast.makeText(context, "Customization saved and applied!", Toast.LENGTH_SHORT).show()
                    
                    // Navigate back to main screen after saving
                    android.os.Handler().postDelayed({
                        localNavController.popBackStack()
                    }, 1500)
                } catch (e: Exception) {
                    val errorMsg = "Error saving customization: ${e.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    android.util.Log.e("EagleyHQ", errorMsg, e)
                }
            },
            onError = { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                android.util.Log.e("EagleyHQ", "Error: $errorMessage")
                errorState.value = errorMessage
            }
        )
    }
} 