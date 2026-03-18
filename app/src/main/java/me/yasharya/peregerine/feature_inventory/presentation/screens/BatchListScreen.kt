package me.yasharya.peregerine.feature_inventory.presentation.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import me.yasharya.peregerine.core.ui.components.BatchCard
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.presentation.BatchListViewModel
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchListScreen(
    viewModel: BatchListViewModel,
    onBack: () -> Unit,
    onBatchClick: (batchId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagedBatches: LazyPagingItems<Batch> = viewModel.pagedBatches.collectAsLazyPagingItems()
    val product = uiState.product

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "All Batches",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (product != null) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Chips
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BatchFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.filter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = {
                            Text(
                                text = when (filter) {
                                    BatchFilter.ALL -> "All"
                                    BatchFilter.ACTIVE -> "Active"
                                    BatchFilter.INACTIVE -> "Inactive"
                                }
                            )
                        }
                    )
                }
            }

            // Content
            when {
                pagedBatches.loadState.refresh is LoadState.Loading && pagedBatches.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                pagedBatches.loadState.refresh is LoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Failed to load batches.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                pagedBatches.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = when (uiState.filter) {
                                BatchFilter.ALL -> "No batches yet."
                                BatchFilter.ACTIVE -> "No active batches."
                                BatchFilter.INACTIVE -> "No inactive batches."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            count = pagedBatches.itemCount,
                            key = pagedBatches.itemKey { it.id }
                        ) { index ->
                            val batch = pagedBatches[index]
                            if (batch != null) {
                                BatchCard(
                                    batch = batch,
                                    unit = product?.unit ?: "",
                                    onClick = { onBatchClick(batch.id) }
                                )
                            }
                        }

                        // Spinner at the bottom while the next page is loading
                        if (pagedBatches.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }

                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}