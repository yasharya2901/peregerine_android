package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import me.yasharya.peregerine.core.ui.components.FilterChipRow
import me.yasharya.peregerine.core.ui.components.ProductCard
import me.yasharya.peregerine.core.ui.components.StatCard
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary
import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(viewModel: InventoryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val pagedProducts: LazyPagingItems<ProductInventorySummary> = viewModel.pagedProducts.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(title = {Text("Inventory")})
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                StatCard(
                    label = "Total Products",
                    count = uiState.totalCount,
                    dotColor = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    label = "Low Stock",
                    count = uiState.lowStockCount,
                    dotColor = Color(0xFFFF9800),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Out of Stock",
                    count = uiState.outOfStockCount,
                    dotColor = Color(0xFFF44336),
                    modifier = Modifier.weight(1f)
                )
            }
            FilterChipRow(
                selectedFilter = uiState.filter,
                onFilterSelected = viewModel::setFilter
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Product List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    count = pagedProducts.itemCount,
                    key = pagedProducts.itemKey { it.product.id }
                ) { index ->
                    val item = pagedProducts[index]
                    if (item != null) {
                        ProductCard(
                            item = item,
                            onAddToPO = {
                                //TODO: Implement Add to PO
                            }
                        )
                    }
                }
            }

        }
    }
}
