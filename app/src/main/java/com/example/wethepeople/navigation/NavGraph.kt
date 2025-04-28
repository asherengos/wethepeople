package com.example.wethepeople.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wethepeople.ui.splash.SplashScreen
import com.example.wethepeople.ui.main.ScaffoldScreen
import com.example.wethepeople.navigation.Screen
import com.example.wethepeople.ui.screens.voting.VoteSwipeScreen
import com.example.wethepeople.ui.screens.voting.ProposeVoteScreen
import com.example.wethepeople.ui.screens.DebateScreen
import com.example.wethepeople.ui.screens.DebateDetailScreen
import com.example.wethepeople.ui.screens.leaderboard.LeaderboardScreen
import com.example.wethepeople.ui.screens.missions.MissionsScreen
import java.net.URLDecoder
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wethepeople.ui.screens.demo.AnimationDemoScreen
import com.example.wethepeople.ui.screens.shop.ShopScreen
import com.example.wethepeople.ui.screens.notification.NotificationScreen
import com.example.wethepeople.ui.viewmodel.MissionsViewModel
import com.example.wethepeople.ui.viewmodel.NotificationViewModel
import androidx.hilt.navigation.compose.hiltViewModel

private const val ANIMATION_DURATION = 300

// Define navigation argument keys
object NavArgs {
    const val CardId = "cardId"
    const val InitialTab = "initialTab"
}

@Composable
private fun MissionsScreenWrapper(navController: NavHostController) {
    val viewModel: MissionsViewModel = hiltViewModel()
    MissionsScreen(
        viewModel = viewModel,
        onBackClick = { navController.popBackStack() },
        appNavController = navController
    )
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        
        // Main route with optional initialTab parameter
        composable(
            route = "${Screen.Main.route}?${NavArgs.InitialTab}={${NavArgs.InitialTab}}",
            arguments = listOf(
                navArgument(NavArgs.InitialTab) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getString(NavArgs.InitialTab)
            ScaffoldScreen(
                appNavController = navController,
                initialTab = initialTab ?: Screen.Main.route
            )
        }
        
        composable(
            route = Screen.Debate.route + "?${NavArgs.CardId}={${NavArgs.CardId}}",
            arguments = listOf(navArgument(NavArgs.CardId) { 
                type = NavType.StringType
                nullable = true
                defaultValue = null 
            })
        ) {
            DebateScreen(navController = navController)
        }

        composable(
            Screen.DebateDetail.route,
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("description") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = URLDecoder.decode(backStackEntry.arguments?.getString("title") ?: "", "UTF-8")
            val description = URLDecoder.decode(backStackEntry.arguments?.getString("description") ?: "", "UTF-8")
            DebateDetailScreen(
                navController = navController,
                debateTitle = title,
                debateDescription = description
            )
        }

        composable(Screen.Voting.route) {
            VoteSwipeScreen(appNavController = navController)
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Missions.route) {
            MissionsScreenWrapper(navController = navController)
        }

        composable(Screen.Shop.route) {
            ShopScreen(navController = navController)
        }
        
        composable(Screen.AnimationDemo.route) {
            AnimationDemoScreen()
        }
        
        composable(Screen.ProposeVote.route) {
            ProposeVoteScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Notifications Screen
        composable(Screen.Notifications.route) {
            val notificationViewModel: NotificationViewModel = hiltViewModel()
            
            NotificationScreen(
                viewModel = notificationViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun NestedNavGraph(
    navController: NavHostController,
    appNavController: NavController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Nested screens here
    }
} 