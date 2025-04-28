package com.example.wethepeople.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.screens.eagly.EaglyScreen
import com.example.wethepeople.ui.screens.facts.FactsScreen
import com.example.wethepeople.ui.screens.voting.VotingScreen
import com.example.wethepeople.ui.screens.voting.VoteSwipeScreen
import com.example.wethepeople.ui.theme.patriot_blue
import com.example.wethepeople.ui.theme.patriot_dark_blue
import com.example.wethepeople.ui.theme.patriot_gold
import com.example.wethepeople.ui.theme.patriot_medium_blue
import com.example.wethepeople.ui.theme.patriot_red_bright
import com.example.wethepeople.ui.theme.patriot_white
import com.example.wethepeople.ui.main.MainScreen
import android.util.Log
import com.example.wethepeople.ui.screens.missions.MissionsScreen
import com.example.wethepeople.ui.screens.DebateScreen
import com.example.wethepeople.ui.screens.shop.ShopScreen
import com.example.wethepeople.ui.components.OverlayNotificationBadge
import com.example.wethepeople.ui.viewmodel.NotificationViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.wethepeople.ui.viewmodel.MissionsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.example.wethepeople.R
import com.example.wethepeople.ui.theme.WeThePeopleFontFamily
import com.example.wethepeople.ui.screens.voting.VoteSwipeViewModel
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.compose.BackHandler
import com.example.wethepeople.ui.screens.eagly.CustomizationEntryPoint

// Define items for the bottom navigation bar
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Main.route,
        label = "Home",
        icon = Icons.Filled.Home
    )
    object Profile : BottomNavItem(
        route = Screen.Eagly.route,
        label = "Eagley HQ",
        icon = Icons.Filled.Person
    )
    object PatriotMissions : BottomNavItem(
        route = Screen.Missions.route,
        label = "Patriot Missions",
        icon = Icons.Filled.Assignment
    )
    object Shop : BottomNavItem(
        route = Screen.Shop.route,
        label = "Shop",
        icon = Icons.Filled.ShoppingCart
    )
    object Notifications : BottomNavItem(
        route = Screen.Notifications.route,
        label = "Notifications",
        icon = Icons.Filled.Notifications
    )
    object Debate : BottomNavItem(
        route = Screen.Debate.route,
        label = "Debate",
        icon = Icons.Filled.Chat
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen(
    appNavController: NavHostController,
    initialTab: String = Screen.Main.route
) {
    val notificationViewModel: NotificationViewModel = viewModel()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val innerNavController = rememberNavController()
    val currentRoute = innerNavController.currentBackStackEntryAsState().value?.destination?.route
    BackHandler(enabled = currentRoute != Screen.Main.route) {
        if (currentRoute == Screen.Notifications.route) {
            appNavController.popBackStack()
        } else {
            innerNavController.navigate(Screen.Main.route) {
                popUpTo(innerNavController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
    LaunchedEffect(initialTab) {
        if (initialTab != Screen.Main.route) {
            innerNavController.navigate(initialTab) {
                popUpTo(innerNavController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true 
                restoreState = true
            }
        }
    }
    Scaffold(
        containerColor = patriot_dark_blue,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = getScreenTitle(currentRoute ?: Screen.Main.route),
                        fontFamily = WeThePeopleFontFamily,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 1.sp,
                        color = patriot_white,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = patriot_medium_blue,
                    titleContentColor = patriot_white
                ),
                navigationIcon = if (currentRoute != Screen.Main.route) {
                    {
                        IconButton(onClick = {
                            if (currentRoute == Screen.Notifications.route) {
                                appNavController.popBackStack()
                            } else {
                                innerNavController.navigate(Screen.Main.route) {
                                    popUpTo(innerNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = patriot_white
                            )
                        }
                    }
                } else ({}) // Provide an empty lambda if no icon
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier.shadow(8.dp)
            ) {
                BottomAppBar(
                    containerColor = patriot_dark_blue,
                    contentColor = patriot_white,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Profile,
                        BottomNavItem.PatriotMissions,
                        BottomNavItem.Shop,
                        BottomNavItem.Notifications
                    )
                    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items.forEach { item ->
                            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (item is BottomNavItem.Notifications) {
                                            appNavController.navigate(Screen.Notifications.route)
                                        } else {
                                            val targetRoute = if (item is BottomNavItem.Home) {
                                                Screen.Main.route
                                            } else {
                                                item.route
                                            }
                                            innerNavController.navigate(targetRoute) { 
                                                popUpTo(innerNavController.graph.findStartDestination().id) { 
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Box(contentAlignment = Alignment.TopEnd) {
                                    Icon(
                                        imageVector = item.icon, 
                                        contentDescription = item.label,
                                        tint = if (isSelected) patriot_gold else patriot_white.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    if (item is BottomNavItem.Notifications && unreadCount > 0) {
                                        OverlayNotificationBadge(
                                            count = unreadCount,
                                            modifier = Modifier
                                                .offset(x = 6.dp, y = (-6).dp)
                                                .size(16.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.label,
                                    color = if (isSelected) patriot_gold else patriot_white.copy(alpha = 0.6f),
                                    fontSize = 10.sp,
                                    maxLines = 1
                                ) 
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Main.route) {
                MainScreen(navController = appNavController)
            }
            composable(Screen.Eagly.route) {
                CustomizationEntryPoint()
            }
            composable(Screen.Missions.route) {
                val viewModel: MissionsViewModel = hiltViewModel()
                MissionsScreen(
                    viewModel = viewModel,
                    onBackClick = { innerNavController.popBackStack() },
                    appNavController = appNavController
                )
            }
            composable(Screen.Shop.route) {
                ShopScreen(navController = appNavController)
            }
            composable(Screen.Debate.route) {
                DebateScreen(navController = appNavController)
            }
            composable(Screen.VoteSwipe.route) {
                val viewModel: VoteSwipeViewModel = hiltViewModel()
                VoteSwipeScreen(
                    appNavController = appNavController,
                    viewModel = viewModel
                )
            }
        }
    }
}

private fun getScreenTitle(route: String): String {
    return when (route) {
        Screen.Main.route -> "WE THE PEOPLE"
        Screen.Eagly.route -> "EAGLEY HQ"
        Screen.Missions.route -> "PATRIOT MISSIONS"
        Screen.Shop.route -> "SHOP"
        Screen.Debate.route -> "DEBATES"
        Screen.VoteSwipe.route -> "VOTE"
        Screen.Notifications.route -> "NOTIFICATIONS"
        else -> "WE THE PEOPLE"
    }
} 