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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import me.yasharya.peregerine.R
import me.yasharya.peregerine.core.ui.components.LedgerEntryRow
import me.yasharya.peregerine.core.ui.components.SearchProductCard
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.presentation.StockLedgerViewModel

private val typeFilterOptions: List<StockChangeType?> =
    listOf(null) + StockChangeType.entries

private fun StockChangeType?.label(): String = when (this) {
    null                             -> "All"
    StockChangeType.OPENING          -> "Opening"
    StockChangeType.PURCHASE_RECEIPT -> "Purchase"
    StockChangeType.ADJUSTMENT       -> "Adjustment"
    StockChangeType.SALE             -> "Sale"
    StockChangeType.RETURN           -> "Return"
    StockChangeType.VOID             -> "Void"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockLedgerScreen(
    viewModel: StockLedgerViewModel,
    onScanBarcode: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val ledgerEntries = viewModel.ledgerEntries.collectAsLazyPagingItems()

    val isSearching = uiState.productQuery.isNotEmpty()
    val hasProductSelected = uiState.selectedProduct != null
    val focusRequester = remember { FocusRequester() }

    val isLedgerEmpty = !isSearching
            && ledgerEntries.loadState.refresh is LoadState.NotLoading
            && ledgerEntries.itemCount == 0

    val isSearchEmpty = isSearching
            && searchResults.loadState.refresh is LoadState.NotLoading
            && searchResults.itemCount == 0

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.tab_ledger)) },
            windowInsets = WindowInsets(0),
            actions = {
                if (!uiState.isSearchBarVisible) {
                    IconButton(onClick = { viewModel.setSearchBarVisibility(true) }) {
                        Icon(Icons.Outlined.Search, contentDescription = "Search Products")
                    }
                } else {
                    IconButton(
                        onClick = {
                            viewModel.setSearchBarVisibility(false)
                            viewModel.setProductQuery("")
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close Search")
                    }
                }
            }
        )

        // Search Bar
        AnimatedVisibility(
            visible = uiState.isSearchBarVisible,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = uiState.productQuery,
                onValueChange = viewModel::setProductQuery,
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.productQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setProductQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    } else {
                        IconButton(onClick = onScanBarcode) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                contentDescription = "Scan Barcode",
                                tint = MaterialTheme.colorScheme.primary
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

        AnimatedVisibility(
            visible = !isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            uiState.selectedProduct?.let { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            IconButton(
                                onClick = viewModel::clearSelectedProduct,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear product filter",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = hasProductSelected && !isSearching,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(typeFilterOptions) { type ->
                    FilterChip(
                        selected = uiState.selectedType == type,
                        onClick = { viewModel.setTypeFilter(type) },
                        label = { Text(type.label()) }
                    )
                }
            }
        }

        // Body
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                // Search mode: show product search results
                isSearching -> {
                    if (isSearchEmpty) {
                        EmptyState(
                            title = "No products found",
                            subtitle = "Try a different name or barcode"
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp
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
                                        onClick = { viewModel.selectProduct(product) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Product selected, but ledger is empty for the current filter
                isLedgerEmpty -> {
                    EmptyState(
                        title = "No entries found",
                        subtitle = if (uiState.selectedType != null)
                            "No ${uiState.selectedType!!.label().lowercase()} entries for this product"
                        else
                            "No stock history recorded yet"
                    )
                }

                // Product selected — show ledger
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp, top = 4.dp, bottom = 32.dp
                        )
                    ) {
                        // Entry count summary below the list header
                        item {
                            if (ledgerEntries.itemCount > 0) {
                                Text(
                                    text = "${ledgerEntries.itemCount} entries",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }

                        items(
                            count = ledgerEntries.itemCount,
                            key = ledgerEntries.itemKey { it.id }
                        ) { index ->
                            val entry = ledgerEntries[index]
                            if (entry != null) {
                                LedgerEntryRow(
                                    type = entry.type,
                                    deltaQty = entry.deltaQty,
                                    note = entry.note,
                                    createdAt = entry.createdAt,
                                    unit = entry.productUnit,
                                    productName = if (uiState.selectedProduct == null) entry.productName else null
                                )
                                if (index < ledgerEntries.itemCount - 1) {
                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}