package me.yasharya.peregerine.core.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.yasharya.peregerine.App
import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel
import me.yasharya.peregerine.feature_inventory.presentation.screens.InventoryListScreen

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val container = (context.applicationContext as App).container

    // Manual ViewModel construction (no DI tool)
    val vm = InventoryViewModel(
        createProduct = container.createProduct,
        updateProduct = container.updateProduct,
        deactivateProduct = container.deactivateProduct,
        adjustStock = container.adjustStock,
        observeProducts = container.observeProducts,
        searchProducts = container.searchProducts
    )

    NavHost(navController = navController, startDestination = Routes.INVENTORY_LIST) {
        composable(Routes.INVENTORY_LIST) {
            InventoryListScreen(vm)
        }
    }
}