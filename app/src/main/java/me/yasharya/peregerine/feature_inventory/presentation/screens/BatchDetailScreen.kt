package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yasharya.peregerine.core.ui.components.AdjustStockDialog
import me.yasharya.peregerine.core.ui.components.BatchCard
import me.yasharya.peregerine.core.ui.components.SectionCard
import me.yasharya.peregerine.core.util.formatQty
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.presentation.BatchDetailViewModel
import me.yasharya.peregerine.feature_inventory.presentation.model.previewText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val fieldShape = RoundedCornerShape(12.dp)
private val batchDetailDateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

@Composable
fun BatchDetailScreen(
    viewModel: BatchDetailViewModel,
    onBack: () -> Unit,
    onEditBatch: (batchId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val batch = uiState.batch
    val product = uiState.product

    LaunchedEffect(uiState.operationError) {
        uiState.operationError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearOperationError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarComponent(batch, onBack, onEditBatch)
        }
    ) { innerPadding ->

        if (batch == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BatchCard(
                batch = batch,
                unit = product?.unit ?: ""
            )

            SectionCard(title = "Purchase Info") {
                InfoRow(
                    label = "Purchase Date",
                    value = batchDetailDateFormatter.format(Date(batch.purchaseDate))
                )
                Spacer(Modifier.height(8.dp))
                InfoRow(label = "Purchase Qty", value = "${formatQty(batch.purchaseQty)} ${product?.unit ?: ""}")
            }

            SectionCard(title = "Pricing") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PriceDetail("MRP", batch.mrp)
                    PriceDetail("Cost", batch.costPrice)
                    PriceDetail("Sell", batch.sellingPrice)
                }
            }

            Button(
                onClick = viewModel::openAdjustDialog,
                shape = fieldShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Adjust Stock", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
        }

        if (uiState.showAdjustDialog) {
            val dialog = uiState.adjustDialog
            AdjustStockDialog(
                qtyInput = dialog.qtyInput,
                isAdding = dialog.isAdding,
                note = dialog.note,
                qtyError = dialog.qtyError,
                previewText = dialog.previewText(batch, product?.unit ?: ""),
                unit = product?.unit ?: "",
                isProductActive = product?.isActive ?: true,
                isLoading = uiState.isOperationLoading,
                isConfirmEnabled = !uiState.isOperationLoading
                        && dialog.qtyInput.isNotEmpty()
                        && dialog.qtyError == null,
                onQtyChange = viewModel::onAdjustQtyChange,
                onModeChange = viewModel::onAdjustModeChange,
                onNoteChange = viewModel::onAdjustNoteChange,
                onConfirm = viewModel::confirmAdjustStock,
                onDismiss = viewModel::closeAdjustDialog,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarComponent(batch: Batch?, onBack: () -> Unit, onEditBatch: (batchId: String) -> Unit) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Batch Detail",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (batch?.isActive == false){
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
            if (batch != null) {
                IconButton(onClick = {onEditBatch(batch.id)}){
                    Icon(Icons.Default.Edit, contentDescription = "Edit Batch")
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        windowInsets = WindowInsets(0)
    )
}


@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PriceDetail(label: String, paise: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "₹${paise / 100}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

