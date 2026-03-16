package me.yasharya.peregerine.feature_inventory.presentation.screens

import android.app.AlertDialog
import android.app.DatePickerDialog
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yasharya.peregerine.core.ui.components.BatchPriceField
import me.yasharya.peregerine.core.ui.components.TodayOrEarlierDates
import me.yasharya.peregerine.feature_inventory.presentation.BatchEditViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val batchEditFieldShape = RoundedCornerShape(12.dp)
private val batchEditDateFormatter = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchEditScreen(
    viewModel: BatchEditViewModel,
    onBack: () -> Unit,
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.savedBatchId) {
        uiState.savedBatchId?.let {onBack()}
    }

    BackHandler { viewModel.handleBackPress(onBack) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit Batch",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.handleBackPress(onBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.background){
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving && !uiState.isLoading,
                    shape = batchEditFieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Save Batch",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PURCHASE DATE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                shape = batchEditFieldShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(batchEditFieldShape)
                    .clickable {viewModel.onShowDatePicker(true)}
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Text(text = batchEditDateFormatter.format(Date(uiState.purchaseDateMillis)), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Text(
                text = "PRICING",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BatchPriceField(
                    label = "MRP (₹)",
                    value = uiState.mrp,
                    onValueChange = viewModel::onMrpChange,
                    error = uiState.mrpError,
                    modifier = Modifier.weight(1f)
                )
                BatchPriceField(
                    label = "Cost (₹)",
                    value = uiState.costPrice,
                    onValueChange = viewModel::onCostChange,
                    error = uiState.costPriceError,
                    modifier = Modifier.weight(1f)
                )
                BatchPriceField(
                    label = "Sell (₹)",
                    value = uiState.sellingPrice,
                    onValueChange = viewModel::onSellingChange,
                    error = uiState.sellingPriceError,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

        }
    }

    if (uiState.showDatePicker) {
        BatchEditDatePickerDialog(
            initialMillis = uiState.purchaseDateMillis,
            onDateSelected = viewModel::onDateChange,
            onDismiss = {viewModel.onShowDatePicker(false)}
        )
    }

    if (uiState.showDiscardConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDiscardConfirm,
            title = { Text("Discard Changes?") },
            text = { Text("Your unsaved changes will be lost.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.dismissDiscardConfirm()
                    onBack()
                }) { Text("Discard") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDiscardConfirm) {
                    Text("Keep Editing")
                }
            }
        )
    }
}

@Composable
private fun BatchEditDatePickerDialog(
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
            }) { Text("OK")}
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") }}
    ){
        DatePicker(state = state)
    }
}