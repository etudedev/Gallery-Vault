package dev.etude.vault.navigation

sealed class Screen(val route: String) {
    data object Auth : Screen("auth")
    data object VaultList : Screen("vault_list")
    data object VaultDetail : Screen("vault_detail/{vaultId}") {
        fun createRoute(vaultId: String) = "vault_detail/$vaultId"
    }
    data object CreateVault : Screen("create_vault")
    data object ImportVault : Screen("import_vault")
    data object Settings : Screen("settings")
}