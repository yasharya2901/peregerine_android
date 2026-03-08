package me.yasharya.peregerine.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings


sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    data object Inventory: BottomNavItem(Routes.INVENTORY, "Inventory", Icons.Outlined.Inventory2)
    data object Purchase: BottomNavItem(Routes.PURCHASE_ORDER, "Purchase Order", Icons.Outlined.Receipt)
    data object Settings: BottomNavItem(Routes.SETTINGS, "Settings", Icons.Outlined.Settings)

    companion object {
        val all = listOf(Inventory, Purchase, Settings)
    }
}