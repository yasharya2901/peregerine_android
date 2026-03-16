package me.yasharya.peregerine.core.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import me.yasharya.peregerine.R

@Serializable
sealed interface AppRoute: NavKey {
    @Serializable data object Inventory: AppRoute
    @Serializable data object PurchaseOrder: AppRoute
    @Serializable data object Settings: AppRoute

    @Serializable data object AddProduct: AppRoute
    @Serializable data class ProductDetail(val productId: String): AppRoute
    @Serializable data class EditProduct(val productId: String): AppRoute

    @Serializable data class BatchList(val productId: String): AppRoute
    @Serializable data class BatchDetail(val batchId: String, val productId: String): AppRoute
    @Serializable data class EditBatch(val batchId: String, val productId: String): AppRoute
}

fun AppRoute.isRoot(): Boolean = when(this) {
    is AppRoute.Inventory -> true
    is AppRoute.PurchaseOrder -> true
    is AppRoute.Settings -> true
    else -> false
}

data class BottomNavItem(
    val route: AppRoute,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    companion object {
        val all = listOf(
            BottomNavItem(AppRoute.Inventory, R.string.tab_inventory, Icons.Outlined.Inventory2),
            BottomNavItem(AppRoute.PurchaseOrder,  R.string.tab_purchase_order,  Icons.Outlined.Receipt),
            BottomNavItem(AppRoute.Settings,  R.string.tab_settings,  Icons.Outlined.Settings)
        )
    }
}