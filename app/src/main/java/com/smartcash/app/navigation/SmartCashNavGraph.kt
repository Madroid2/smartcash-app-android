package com.smartcash.app.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smartcash.app.feature.earn.EarnScreen
import com.smartcash.app.feature.earn.EarnViewModel
import com.smartcash.app.feature.earn.WebViewScreen
import com.smartcash.app.feature.home.HomeScreen
import com.smartcash.app.feature.home.HomeViewModel
import com.smartcash.app.feature.profile.ProfileScreen
import com.smartcash.app.feature.profile.ProfileViewModel
import com.smartcash.app.feature.wallet.WalletScreen
import com.smartcash.app.feature.wallet.WalletViewModel

@Composable
fun SmartCashNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController)
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = androidx.compose.animation.core.tween(300),
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = androidx.compose.animation.core.tween(300),
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = androidx.compose.animation.core.tween(300),
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = androidx.compose.animation.core.tween(300),
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
            },
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onPortalClick = { portal ->
                        navController.navigate(Screen.WebView.routeFor(portal.url, portal.name))
                    },
                )
            }

            composable(Screen.Earn.route) {
                val viewModel: EarnViewModel = hiltViewModel()
                EarnScreen(
                    viewModel = viewModel,
                    onPortalClick = { portal ->
                        navController.navigate(Screen.WebView.routeFor(portal.url, portal.name))
                    },
                )
            }

            composable(Screen.Wallet.route) {
                val viewModel: WalletViewModel = hiltViewModel()
                WalletScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = hiltViewModel()
                ProfileScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.WebView.ROUTE,
                enterTransition = {
                    slideIntoContainer(
                        towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = androidx.compose.animation.core.tween(300),
                    ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(300))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = androidx.compose.animation.core.tween(300),
                    ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(300))
                },
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                val title = backStackEntry.arguments?.getString("title") ?: ""
                val decodedUrl = Screen.WebView.decodeUrl(url)
                val decodedTitle = Screen.WebView.decodeTitle(title)

                WebViewScreen(
                    url = decodedUrl,
                    title = decodedTitle,
                    onBackPress = { navController.popBackStack() },
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navItems = listOf(
        Triple(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
        Triple(Screen.Earn, Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn),
        Triple(Screen.Wallet, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
        Triple(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person),
    )

    NavigationBar {
        navItems.forEach { (screen, filledIcon, outlinedIcon) ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = screen.route,
                    )
                },
                label = { Text(screen.route.replaceFirstChar(Char::uppercase)) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
