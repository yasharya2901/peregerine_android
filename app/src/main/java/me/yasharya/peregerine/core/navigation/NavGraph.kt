package me.yasharya.peregerine.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.yasharya.peregerine.App
import me.yasharya.peregerine.feature_inventory.presentation.TestInventoryViewModel
import me.yasharya.peregerine.feature_inventory.presentation.screens.TestInventoryScreen

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as App).container

    // Test ViewModel with all use cases
    val testVm = TestInventoryViewModel(
        useCases = container.inventoryUseCases
    )

    NavHost(navController = navController, startDestination = Routes.INVENTORY_LIST) {
        composable(Routes.INVENTORY_LIST) {
            TestInventoryScreen(testVm)
        }
    }
}