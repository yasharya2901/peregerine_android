package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel

@Composable
fun InventoryListScreen(viewModel: InventoryViewModel) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Inventory List Screen")
    }
}
