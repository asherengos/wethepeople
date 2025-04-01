package com.example.partyofthepeople.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.partyofthepeople.ui.components.*
import com.example.partyofthepeople.ui.theme.ComposeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatrioticHomeScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Party of the People") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ComposeColors.PatriotBlue,
                    titleContentColor = ComposeColors.NeonWhite
                )
            )
        },
        floatingActionButton = {
            PatrioticFAB(onClick = { /* Handle FAB click */ })
        },
        bottomBar = {
            NewsTicker(news = "Patriotism surges to all-time high!")
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FreedomBuckIcon()
                DebateChampBadge()
                PatrioticButton(onClick = { /* Handle button click */ })
                MyComposable()
            }
        }
    }
}

@Composable
fun MyComposable() {
    Surface(color = ComposeColors.PatriotBlue) {
        Text("America First!", color = ComposeColors.NeonWhite)
    }
}