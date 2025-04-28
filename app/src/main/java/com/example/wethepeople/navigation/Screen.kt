package com.example.wethepeople.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen destinations for app navigation
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    val showInBottomNav: Boolean = false
) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Register : Screen("register", "Register")
    object Main : Screen("main", "Main") // This might represent your ScaffoldScreen
    object Home : Screen("home", "Home", Icons.Default.Home, true)
    object Voting : Screen("voting", "Voting", Icons.Default.HowToVote, true)
    object VoteSwipe : Screen("voteSwipe", "Vote Swipe")
    object Profile : Screen("profile", "Profile")
    object Eagly : Screen("eagly", "Eagly") // Or Patriot Missions
    object Shop : Screen("shop", "Shop", Icons.Default.ShoppingCart, true) // Renamed from Facts to Shop
    object Debate : Screen("debate", "Debates", Icons.Default.Chat, true)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Default.EmojiEvents, true)
    object Missions : Screen("missions", "Missions", Icons.Default.Flag, true)
    object AnimationDemo : Screen("animation_demo", "Animation Demo") // New screen for animations
    object ProposeVote : Screen("propose_vote", "Propose Vote") // New screen for proposing votes
    object DebateDetail : Screen("debate_detail/{title}/{description}", "Debate Detail") {
        fun createRoute(title: String, description: String): String {
            val encodedTitle = java.net.URLEncoder.encode(title, "UTF-8")
            val encodedDescription = java.net.URLEncoder.encode(description, "UTF-8")
            return "debate_detail/$encodedTitle/$encodedDescription"
        }
    }
    object Scaffold : Screen("scaffold", "Scaffold") // Main screen with bottom nav
    object ActivePolls : Screen("active_polls", "Active Polls")
    object PollDetails : Screen("poll_details/{pollId}", "Poll Details") {
        fun createRoute(pollId: String) = "poll_details/$pollId"
    }
    object Notifications : Screen("notifications", "Notifications")
    object PatriotPath : Screen("patriot_path", "Patriot Path")

    // Extension function to create route with arguments
    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                if (!route.contains("{$arg}")) {
                    // If the route doesn't already have this parameter, add it as a query parameter
                    if (!contains("?")) {
                        append("?$arg={$arg}")
                    } else {
                        append("&$arg={$arg}")
                    }
                }
            }
        }
    }

    companion object {
        // Function to map route strings back to Screen objects if needed
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Splash.route -> Splash
                Login.route -> Login
                Register.route -> Register
                Main.route -> Main
                Home.route -> Home
                Voting.route -> Voting
                VoteSwipe.route -> VoteSwipe
                Profile.route -> Profile
                Eagly.route -> Eagly
                Shop.route -> Shop
                Debate.route -> Debate
                Leaderboard.route -> Leaderboard
                Missions.route -> Missions
                AnimationDemo.route -> AnimationDemo
                ProposeVote.route -> ProposeVote
                DebateDetail.route -> DebateDetail
                Scaffold.route -> Scaffold
                ActivePolls.route -> ActivePolls
                PollDetails.route -> PollDetails
                Notifications.route -> Notifications
                PatriotPath.route -> PatriotPath
                else -> Splash // Default to Splash if unknown or null
            }
        }

        val bottomNavItems = listOf(
            Home,
            Debate,
            Voting,
            Leaderboard,
            Missions,
            Shop
        )
    }

    // Helper function to create routes with arguments
    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

// REMOVING THIS - Using the NavArgs from NavGraph.kt instead
// object NavArgs {
//     const val CardId = "cardId"
// }

// Remove or comment out the old unused routes
// fun fromRoute(route: String): Screen {
//     return when (route) {
//         "home" -> Screen.Home
//         "shop" -> Screen.Shop
//         "battleground" -> Screen.Battleground
//         "vote_detail" -> Screen.VoteDetail
//         "profile" -> Screen.Profile
//         "achievements" -> Screen.Achievements
//         "leaderboard" -> Screen.Leaderboard
//         "bipartisan_match" -> Screen.BipartisanMatch
//         "election_history" -> Screen.ElectionHistory
//         "controversy_radar" -> Screen.ControversyRadar
//         "profile_customization" -> Screen.ProfileCustomization
//         "title" -> Screen.Title
//         "eagle_companion" -> Screen.EagleCompanion
//         "debate" -> Screen.Debate
//         "vote" -> Screen.Vote
//         "missions" -> Screen.Missions
//         "report_dissent" -> Screen.ReportDissent
//         "settings" -> Screen.Settings
//         else -> Screen.Home // Default to Home if unknown route
//     }
// } 