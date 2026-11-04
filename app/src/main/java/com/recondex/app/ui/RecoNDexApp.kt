package com.recondex.app.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.recondex.app.core.ProgressStore
import com.recondex.app.core.TOOLS
import com.recondex.app.ui.screens.ApkAnalyzerScreen
import com.recondex.app.ui.screens.BleScannerScreen
import com.recondex.app.ui.screens.DisclaimerScreen
import com.recondex.app.ui.screens.HashLabScreen
import com.recondex.app.ui.screens.HomeScreen
import com.recondex.app.ui.screens.NetworkScannerScreen
import com.recondex.app.ui.screens.NfcToolsScreen
import com.recondex.app.ui.screens.ProfileScreen
import com.recondex.app.ui.screens.SettingsScreen
import com.recondex.app.ui.screens.WebReconScreen

private data class BottomDest(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomDests = listOf(
    BottomDest("home", "Inicio", Icons.Filled.Home),
    BottomDest("profile", "Perfil", Icons.Filled.Person),
    BottomDest("settings", "Ajustes", Icons.Filled.Settings),
)

@Composable
fun RecoNDexApp(progress: ProgressStore) {
    val navController = rememberNavController()

    if (!progress.disclaimerAccepted) {
        DisclaimerScreen(onAccept = { progress.acceptDisclaimer() })
        return
    }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val onMainTab = bottomDests.any { dest ->
                currentDestination?.hierarchy?.any { it.route == dest.route } == true
            }
            if (onMainTab) {
                NavigationBar {
                    bottomDests.forEach { dest ->
                        val selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { androidx.compose.material3.Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = androidx.compose.ui.Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it / 4 }, animationSpec = tween(280)) + fadeIn(tween(280))
            },
            exitTransition = {
                fadeOut(tween(200))
            },
            popEnterTransition = {
                fadeIn(tween(220))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it / 4 }, animationSpec = tween(220)) + fadeOut(tween(220))
            }
        ) {
            composable("home") {
                HomeScreen(progress = progress, tools = TOOLS, onToolClick = { navController.navigate(it.route) })
            }
            composable("profile") { ProfileScreen(progress = progress) }
            composable("settings") { SettingsScreen() }

            composable("network") { NetworkScannerScreen(progress) }
            composable("apk") { ApkAnalyzerScreen(progress) }
            composable("ble") { BleScannerScreen(progress) }
            composable("nfc") { NfcToolsScreen(progress) }
            composable("webrecon") { WebReconScreen(progress) }
            composable("hashlab") { HashLabScreen(progress) }
        }
    }
}
