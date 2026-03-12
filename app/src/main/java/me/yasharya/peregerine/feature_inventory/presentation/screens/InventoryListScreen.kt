package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import me.yasharya.peregerine.R
import me.yasharya.peregerine.core.ui.components.FilterChipRow
import me.yasharya.peregerine.core.ui.components.ProductCard
import me.yasharya.peregerine.core.ui.components.SearchProductCard
import me.yasharya.peregerine.core.ui.components.StatCard
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary
import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel
import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryFilter

private data class EmptyStateRes(
    val titleRes: Int,
    val subtitleRes: Int
)

private fun emptyStateResFor(filter: InventoryFilter) = when (filter) {
    InventoryFilter.ALL -> EmptyStateRes(
        titleRes    = R.string.empty_all_title,
        subtitleRes = R.string.empty_all_subtitle
    )
    InventoryFilter.LOW_STOCK -> EmptyStateRes(
        titleRes    = R.string.empty_low_stock_title,
        subtitleRes = R.string.empty_low_stock_subtitle
    )
    InventoryFilter.OUT_OF_STOCK -> EmptyStateRes(
        titleRes    = R.string.empty_out_of_stock_title,
        subtitleRes = R.string.empty_out_of_stock_subtitle
    )
    InventoryFilter.INACTIVE -> EmptyStateRes(
        titleRes    = R.string.empty_inactive_title,
        subtitleRes = R.string.empty_inactive_subtitle
    )

    InventoryFilter.NOT_STOCKED -> EmptyStateRes(
        titleRes    = R.string.empty_not_stocked_title,
        subtitleRes = R.string.empty_not_stocked_subtitle
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryListScreen(viewModel: InventoryViewModel, onAddProduct: () -> Unit) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedProducts: LazyPagingItems<ProductInventorySummary> = viewModel.pagedProducts.collectAsLazyPagingItems()

    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val isSearching = uiState.searchQuery.isNotEmpty()


    val isEmpty = if (isSearching)
            searchResults.loadState.refresh is LoadState.NotLoading && searchResults.itemCount == 0
        else
            pagedProducts.loadState.refresh is LoadState.NotLoading && pagedProducts.itemCount == 0

    var isSearchBarVisible by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {Text(stringResource(R.string.tab_inventory))},
            windowInsets = WindowInsets(0),
            actions = {
                if (!isSearchBarVisible) {
                    IconButton(onClick = {
                        isSearchBarVisible = true
                    }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search")
                    }
                } else {
                    IconButton(
                        onClick = {
                            isSearchBarVisible = false
                            viewModel.setSearchQuery("")
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Search")
                    }
                }
            }
        )

        // Search Bar
        AnimatedVisibility(
            visible = isSearchBarVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::setSearchQuery,
                placeholder = {Text("Search by name of barcode...")},
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()){
                        IconButton(onClick = {viewModel.setSearchQuery("")}) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .focusRequester(focusRequester)
            )
        }
        // Stat Cards
        AnimatedVisibility(
            visible = !isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                StatCard(
                    label = stringResource(R.string.stat_total_products),
                    count = uiState.totalCount,
                    dotColor = Color(0xFF4CAF50),
                    selected = uiState.filter == InventoryFilter.ALL,
                    onClick = {viewModel.setFilter(InventoryFilter.ALL)},
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    label = stringResource(R.string.stat_low_stock),
                    count = uiState.lowStockCount,
                    dotColor = Color(0xFFFF9800),
                    selected = uiState.filter == InventoryFilter.LOW_STOCK,
                    onClick = {viewModel.setFilter(InventoryFilter.LOW_STOCK)},
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = stringResource(R.string.stat_out_of_stock),
                    count = uiState.outOfStockCount,
                    dotColor = Color(0xFFF44336),
                    selected = uiState.filter == InventoryFilter.OUT_OF_STOCK,
                    onClick = {viewModel.setFilter(InventoryFilter.OUT_OF_STOCK)},
                    modifier = Modifier.weight(1f)
                )
            }
        }

        AnimatedVisibility(
            visible = !isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            FilterChipRow(
                selectedFilter = uiState.filter,
                onFilterSelected = viewModel::setFilter
            )
        }

        if (!isSearching) Spacer(modifier = Modifier.height(8.dp))

        // Product List
        Box(modifier = Modifier.fillMaxSize()) {
            if (isEmpty) {
                val res = if (isSearching)
                    EmptyStateRes(R.string.empty_search_title, R.string.empty_search_subtitle)
                else
                    emptyStateResFor(uiState.filter)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(res.titleRes),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(res.subtitleRes),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                if (isSearching) {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = searchResults.itemCount,
                            key = searchResults.itemKey { it.id }
                        ) { index ->
                            val product = searchResults[index]
                            if (product != null) {
                                SearchProductCard(
                                    product = product,
                                    onClick = { /* TODO: navigate to product detail */ }
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 96.dp
                        ),
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
                                        // TODO: Implement Add to PO
                                    }
                                )
                            }
                        }
                    }
                }
            }
            ExtendedFloatingActionButton(
                onClick = onAddProduct,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                icon = {Icon(Icons.Default.Add, contentDescription = "Add Product")},
                text = {Text("New", fontWeight = FontWeight.SemiBold)},
                containerColor = MaterialTheme.colorScheme.primary
            )
        }


    }
}
