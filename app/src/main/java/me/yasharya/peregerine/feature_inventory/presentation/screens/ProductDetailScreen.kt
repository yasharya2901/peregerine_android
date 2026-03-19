package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yasharya.peregerine.core.ui.components.BatchCard
import me.yasharya.peregerine.core.ui.components.LedgerEntryRow
import me.yasharya.peregerine.core.ui.components.SectionCard
import me.yasharya.peregerine.core.ui.components.TodayOrEarlierDates
import me.yasharya.peregerine.core.util.formatQty
import me.yasharya.peregerine.core.util.fromPaise
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.presentation.ProductDetailViewModel
import me.yasharya.peregerine.feature_inventory.presentation.model.AddBatchDialogState
import me.yasharya.peregerine.feature_inventory.presentation.model.AdjustStockDialogState
import me.yasharya.peregerine.feature_inventory.presentation.model.activeBatchCount
import me.yasharya.peregerine.feature_inventory.presentation.model.previewText
import me.yasharya.peregerine.feature_inventory.presentation.model.totalStock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val cardShape = RoundedCornerShape(16.dp)
private val fieldShape = RoundedCornerShape(12.dp)
private val dateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
private const val MAX_BATCHES_SHOWN = 5
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBack: () -> Unit,
    onEditProduct: (productId: String) -> Unit,
    onFullLedger: (productId: String) -> Unit,
    onViewAllBatches: (productId: String) -> Unit,
    onBatchClick: (batchId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show one-shot errors as snackbars
    LaunchedEffect(uiState.operationError) {
        uiState.operationError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearOperationError()
        }
    }

    val product = uiState.product

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = product?.name ?: "",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (product?.isActive == false) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "Inactive",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (product != null) {
                        IconButton(onClick = { onEditProduct(product.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Product")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0)
            )
        }
    ) { innerPadding ->
        if (product == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Stock Header
            StockHeaderCard(
                totalStock = uiState.totalStock,
                unit = product.unit,
                threshold = product.lowStockThreshold,
                defaultSellingPrice = product.defaultSellingPrice,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Action Buttons
            val isActive = product.isActive
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    label = "Adjust Stock",
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::openAdjustStockDialog
                )
                ActionButton(
                    label = "Add Batch",
                    enabled = isActive,
                    disabledHint = "Activate product to add stock",
                    modifier = Modifier.weight(1f),
                    onClick = viewModel::openAddBatchDialog
                )
                ActionButton(
                    label = "Full Ledger",
                    enabled = true,
                    modifier = Modifier.weight(1f),
                    onClick = { onFullLedger(product.id) }
                )
            }

            Spacer(Modifier.height(16.dp))

            // Pricing
            if (product.defaultMRP != null || product.defaultCostPrice != null || product.defaultSellingPrice != null) {
                SectionCard(
                    title = "Pricing",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        PriceColumn("MRP", product.defaultMRP)
                        PriceColumn("Cost", product.defaultCostPrice)
                        PriceColumn("Selling", product.defaultSellingPrice)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Batches
            val batchesToShow = uiState.batches.take(MAX_BATCHES_SHOWN)
            val hasMoreBatches = uiState.batches.size > MAX_BATCHES_SHOWN

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Batches (${uiState.activeBatchCount} active)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                if (hasMoreBatches) {
                    TextButton(onClick = {onViewAllBatches(product.id)}) {
                        Text(
                            text = "View All (${uiState.batches.size})",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if (uiState.batches.isEmpty()) {
                Text(
                    text = "No batches yet. Use \"Add Batch\" to receive stock.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    batchesToShow.forEach { batch ->
                        BatchCard(
                            batch = batch,
                            unit = product.unit,
                            onClick = { onBatchClick(batch.id) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Recent Activity
            if (uiState.recentLedger.isNotEmpty()) {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    uiState.recentLedger.forEachIndexed { index, entry ->
                        LedgerEntryRow(
                            type = entry.type,
                            deltaQty = entry.deltaQty,
                            note = entry.note,
                            createdAt = entry.createdAt,
                            unit = product.unit
                        )
                        if (index < uiState.recentLedger.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Notes
            if (!product.notes.isNullOrBlank()) {
                SectionCard (
                    title = "Notes",
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = product.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            // Deactivate / Activate Button
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))

            if (isActive) {
                Button(
                    onClick = viewModel::openDeactivateConfirmDialog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp)
                ) {
                    Text("Deactivate Product", fontWeight = FontWeight.SemiBold)
                }
            } else {
                Button(
                    onClick = viewModel::openActivateConfirmDialog,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp)
                ) {
                    Text("Activate Product", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    // Dialogs

    if (uiState.showAdjustStockDialog) {
        AdjustStockDialog(
            dialogState = uiState.adjustStockDialog,
            batches = uiState.batches.filter { it.isActive },
            unit = product?.unit ?: "",
            isLoading = uiState.isOperationLoading,
            isProductActive = product?.isActive ?: true,
            onSelectBatch = viewModel::onAdjustSelectBatch,
            onQtyChange = viewModel::onAdjustQtyChange,
            onModeChange = viewModel::onAdjustModeChange,
            onNoteChange = viewModel::onAdjustNoteChange,
            onConfirm = viewModel::confirmAdjustStock,
            onDismiss = viewModel::closeAdjustStockDialog
        )
    }

    if (uiState.showAddBatchDialog) {
        AddBatchDialog(
            dialogState = uiState.addBatchDialog,
            unit = product?.unit ?: "",
            isLoading = uiState.isOperationLoading,
            onDateClick = { viewModel.onAddBatchShowDatePicker(true) },
            onMrpChange = viewModel::onAddBatchMrpChange,
            onCostChange = viewModel::onAddBatchCostChange,
            onSellingChange = viewModel::onAddBatchSellingChange,
            onQtyChange = viewModel::onAddBatchQtyChange,
            onConfirm = viewModel::confirmAddBatch,
            onDismiss = viewModel::closeAddBatchDialog
        )
    }

    if (uiState.addBatchDialog.showDatePicker) {
        BatchDatePickerDialog(
            initialMillis = uiState.addBatchDialog.purchaseDateMillis,
            onDateSelected = viewModel::onAddBatchDateChange,
            onDismiss = { viewModel.onAddBatchShowDatePicker(false) }
        )
    }

    if (uiState.showDeactivateConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeDeactivateConfirmDialog,
            title = { Text("Deactivate Product?") },
            text = {
                Text(
                    "This product will be hidden from invoice search and no new stock can be added. " +
                            "Existing batches and ledger history are preserved. " +
                            "You can activate it again at any time."
                )
            },
            confirmButton = {
                Button(
                    onClick = viewModel::confirmDeactivate,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Deactivate") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeDeactivateConfirmDialog) { Text("Cancel") }
            }
        )
    }

    if (uiState.showActivateConfirmDialog) {
        AlertDialog(
            onDismissRequest = viewModel::closeActivateConfirmDialog,
            title = { Text("Activate Product?") },
            text = {
                Text(
                    "This product will appear in invoice search and new stock can be added again."
                )
            },
            confirmButton = {
                Button(onClick = viewModel::confirmActivate) { Text("Activate") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::closeActivateConfirmDialog) { Text("Cancel") }
            }
        )
    }
}


@Composable
private fun StockHeaderCard(
    totalStock: Double,
    unit: String,
    threshold: Double?,
    defaultSellingPrice: Long?,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = cardShape
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Total Stock",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = formatQty(totalStock),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    threshold?.let {
                        Column(
                            horizontalAlignment = Alignment.End,
                        ) {
                            Text(
                                text = "Low Stock Threshold",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "${formatQty(it)} $unit",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceColumn(label: String, paise: Long?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (paise != null) "₹${paise.fromPaise()}" else "—",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ActionButton(
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    disabledHint: String? = null,
    onClick: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = fieldShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
        if (!enabled && disabledHint != null) {
            Text(
                text = disabledHint,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun AdjustStockDialog(
    dialogState: AdjustStockDialogState,
    batches: List<Batch>,
    unit: String,
    isLoading: Boolean,
    isProductActive: Boolean,
    onSelectBatch: (Batch) -> Unit,
    onQtyChange: (String) -> Unit,
    onModeChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 640.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 16.dp, end = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Adjust Stock", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Inactive product banner for add attempt
                if (!isProductActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Product is inactive. Only stock reduction is allowed. Activate the product to add stock.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // SELECT BATCH
                Text(
                    text = "SELECT BATCH",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                if (batches.isEmpty()) {
                    Text(
                        text = "No active batches. Add a batch first.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        batches.forEach { batch ->
                            val isSelected = dialogState.selectedBatch?.id == batch.id
                            val borderColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            val bgColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                            else
                                MaterialTheme.colorScheme.surface

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgColor)
                                    .clickable { onSelectBatch(batch) }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = dateFormatter.format(Date(batch.purchaseDate)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Cost ₹${batch.costPrice / 100} · MRP ₹${batch.mrp / 100}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = formatQty(batch.qtyOnHand),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$unit left",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // QUANTITY
                Text(
                    text = "QUANTITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Add / Remove toggle
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ModeChip(
                            label = "+ Add",
                            selected = dialogState.isAdding,
                            enabled = isProductActive,
                            modifier = Modifier.height(56.dp), // Same as OutlinedTextField
                            onClick = { if (isProductActive) onModeChange(true) }
                        )
                        ModeChip(
                            label = "− Remove",
                            selected = !dialogState.isAdding,
                            enabled = true,
                            modifier = Modifier.height(56.dp),
                            onClick = { onModeChange(false) }
                        )
                    }

                    OutlinedTextField(
                        value = dialogState.qtyInput,
                        onValueChange = onQtyChange,
                        singleLine = true,
                        isError = dialogState.qtyError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = fieldShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Inline preview / error
                val preview = dialogState.previewText(unit)
                val errorOrPreview = dialogState.qtyError ?: preview
                if (errorOrPreview != null) {
                    Text(
                        text = errorOrPreview,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dialogState.qtyError != null)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // NOTE
                Text(
                    text = "NOTE (optional)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                OutlinedTextField(
                    value = dialogState.note,
                    onValueChange = onNoteChange,
                    placeholder = { Text("e.g. Damaged goods, expired...") },
                    singleLine = true,
                    shape = fieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading
                                && dialogState.selectedBatch != null
                                && dialogState.qtyInput.isNotEmpty()
                                && dialogState.qtyError == null,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Confirm")
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ModeChip(label: String, selected: Boolean, enabled: Boolean, modifier: Modifier = Modifier,onClick: () -> Unit) {
    val bg = when {
        selected -> MaterialTheme.colorScheme.primary
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when {
        selected -> MaterialTheme.colorScheme.onPrimary
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight()
        ){
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun AddBatchDialog(
    dialogState: AddBatchDialogState,
    unit: String,
    isLoading: Boolean,
    onDateClick: () -> Unit,
    onMrpChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onSellingChange: (String) -> Unit,
    onQtyChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.94f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Batch", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Purchase Date
                Text(
                    "PURCHASE DATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Surface(
                    shape = fieldShape,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(fieldShape)
                        .clickable(onClick = onDateClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = dateFormatter.format(Date(dialogState.purchaseDateMillis)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // MRP / COST / SELL
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BatchPriceField(
                        label = "MRP (₹)",
                        value = dialogState.mrp,
                        onValueChange = onMrpChange,
                        error = dialogState.mrpError,
                        modifier = Modifier.weight(1f)
                    )
                    BatchPriceField(
                        label = "COST (₹)",
                        value = dialogState.costPrice,
                        onValueChange = onCostChange,
                        error = dialogState.costPriceError,
                        modifier = Modifier.weight(1f)
                    )
                    BatchPriceField(
                        label = "SELL (₹)",
                        value = dialogState.sellingPrice,
                        onValueChange = onSellingChange,
                        error = dialogState.sellingPriceError,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Quantity
                Text(
                    "QUANTITY ($unit)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = dialogState.qty,
                    onValueChange = onQtyChange,
                    singleLine = true,
                    isError = dialogState.qtyError != null,
                    supportingText = dialogState.qtyError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = fieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }

                    Button(
                        onClick = onConfirm,
                        enabled = !isLoading,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else Text("Add Batch")
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchPriceField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it, style = MaterialTheme.typography.labelSmall) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = fieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BatchDatePickerDialog(
    initialMillis: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = TodayOrEarlierDates
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onDateSelected(it) } ?: onDismiss()
            }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = state)
    }
}