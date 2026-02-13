package dev.etude.vault.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            // Placeholder - we'll build real AuthScreen later
            Text("Authentication Screen (PIN / Biometric)")
        }

        composable(Screen.VaultDetail.route) {
            // Placeholder
            Text("Vault Detail Screen - Media Grid + Breadcrumb")
        }

        // Add more as we implement them
    }
}