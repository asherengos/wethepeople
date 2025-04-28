package com.example.wethepeople.ui

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.wethepeople.navigation.AppNavigation
import com.example.wethepeople.ui.theme.PartyOfThePeopleTheme

@Composable
fun FreedomFightersApp() {
    PartyOfThePeopleTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavigation()
        }
    }
} 