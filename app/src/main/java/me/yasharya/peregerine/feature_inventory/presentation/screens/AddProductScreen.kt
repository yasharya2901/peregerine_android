package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.presentation.AddProductViewModel

private val fieldShape = RoundedCornerShape(12.dp)
private val cardShape = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel,
    onBack: () -> Unit,
    onNavigateToProductDetail: (productId: String) -> Unit,
    onScanBarcode: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val unitSearchQuery by viewModel.unitSearchQuery.collectAsStateWithLifecycle()
    val filteredUnits by viewModel.filteredUnits.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.savedProductId) {
        uiState.savedProductId?.let{onNavigateToProductDetail(it)}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // TODO: Strings from resources
                    Text(
                        text = "Add Product",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                windowInsets = WindowInsets(0),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isLoading,
                    shape = fieldShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        // TODO: Strings from resources
                        Text(
                            text = "Save Product",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Basic Info
            SectionCard(title = "Basic Info") {
                AppTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    label = "Product Name *",
                    placeholder = "e.g. Hooli Signature Box 2",
                    isError = uiState.nameError != null,
                    errorMessage = uiState.nameError,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                AppTextField(
                    value = uiState.barcode,
                    onValueChange = viewModel::onBarcodeChange,
                    label = "Barcode",
                    placeholder = "Scan or enter manually",
                    trailingIcon = {
                        IconButton(
                            onClick = onScanBarcode
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.QrCodeScanner,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Scan Barcode"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Measurement Unit
            SectionCard(title = "Measurement Unit") {
                UnitSelectorRow(
                    selectedUnit = uiState.selectedUnit,
                    error = uiState.unitError,
                    onClick = viewModel::showUnitPicker
                )
            }

            // Pricing Details
            SectionCard(title = "Pricing Details") {
                Text(
                    text = "Used as defaults when creating new batches",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PriceInputField(
                        label = "MRP",
                        value = uiState.defaultMrp,
                        onValueChange = viewModel::onMrpChange,
                        modifier = Modifier.weight(1f)
                    )
                    PriceInputField(
                        label = "Cost",
                        value = uiState.defaultCostPrice,
                        onValueChange = viewModel::onCostPriceChange,
                        modifier = Modifier.weight(1f)
                    )
                    PriceInputField(
                        label = "Selling",
                        value = uiState.defaultSellingPrice,
                        onValueChange = viewModel::onSellingPriceChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Stock Settings
            SectionCard(title = "Stock Settings") {
                AppTextField(
                    value = uiState.lowStockThreshold,
                    onValueChange = viewModel::onThresholdChange,
                    label = "Low Stock Alert at",
                    placeholder = "0",
                    suffix = uiState.selectedUnit?.name,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(10.dp))

                AppTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    label = "Notes",
                    placeholder = "Any additional information...",
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done
                    ),
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Opening Stock
            SectionCard(title = "Opening Stock") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // TODO: Strings from resources
                        Text(
                            text = "Add Opening Stock",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Creates an initial batch and ledger entry",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Switch(
                        checked = uiState.addOpeningStock,
                        onCheckedChange = viewModel::onOpeningStockToggle
                    )
                }

                AnimatedVisibility(
                    visible = uiState.addOpeningStock,
                    enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                    exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut()
                ) {
                    Column {
                        Spacer(Modifier.height(16.dp))

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Spacer(Modifier.height(16.dp))

                        AppTextField(
                            value = uiState.openingQty,
                            onValueChange = viewModel::onOpeningQtyChange,
                            label = "Quantity *",
                            placeholder = "0",
                            suffix = uiState.selectedUnit?.name,
                            isError = uiState.openingQtyError != null,
                            errorMessage = uiState.openingQtyError,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "Batch pricing",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PriceInputField(
                                label = "MRP *",
                                value = uiState.openingMrp,
                                onValueChange = viewModel::onOpeningMrpChange,
                                error = uiState.openingMrpError,
                                modifier = Modifier.weight(1f)
                            )
                            PriceInputField(
                                label = "Cost *",
                                value = uiState.openingCostPrice,
                                onValueChange = viewModel::onOpeningCostChange,
                                error = uiState.openingCostPriceError,
                                modifier = Modifier.weight(1f)
                            )
                            PriceInputField(
                                label = "Selling *",
                                value = uiState.openingSellingPrice,
                                onValueChange = viewModel::onOpeningSellingChange,
                                error = uiState.openingSellingPriceError,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    // Unit Picker Dialog
    if (uiState.showUnitPicker) {
        UnitPickerDialog(
            query = unitSearchQuery,
            units = filteredUnits,
            selectedUnit = uiState.selectedUnit,
            onQueryChange = viewModel::onUnitQueryChange,
            onUnitSelected = viewModel::onUnitSelected,
            onAddCustom = viewModel::onAddCustomUnit,
            onDismiss = viewModel::hideUnitPicker
        )
    }
}

// Section Card

@Composable
private fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                content = content
            )
        }
    }
}

// Text Fields

@Composable
private fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    suffix: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            Text(
                placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        suffix = suffix?.let {
            {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = errorMessage?.let { msg -> { Text(msg) } },
        keyboardOptions = keyboardOptions,
        singleLine = maxLines == 1,
        minLines = minLines,
        maxLines = maxLines,
        shape = fieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    )
}

@Composable
private fun PriceInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        prefix = { Text("₹", style = MaterialTheme.typography.bodyMedium) },
        isError = error != null,
        supportingText = error?.let { msg ->
            { Text(msg, style = MaterialTheme.typography.labelSmall) }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        shape = fieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = modifier
    )
}

// Unit Selector

@Composable
private fun UnitSelectorRow(
    selectedUnit: MeasureUnit?,
    error: String?,
    onClick: () -> Unit
) {
    val borderColor = if (error != null)
        MaterialTheme.colorScheme.error
    else
        MaterialTheme.colorScheme.outlineVariant

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(fieldShape)
                .border(width = 1.dp, color = borderColor, shape = fieldShape)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selectedUnit != null) {
                Column {
                    Text(
                        text = "Unit",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedUnit.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = "Select Unit *",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// Unit Picker Dialog

@Composable
private fun UnitPickerDialog(
    query: String,
    units: List<MeasureUnit>,
    selectedUnit: MeasureUnit?,
    onQueryChange: (String) -> Unit,
    onUnitSelected: (MeasureUnit) -> Unit,
    onAddCustom: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val showAddCustom = query.isNotBlank() && units.none {
        it.name.equals(query.trim(), ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 520.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    text = "Select Unit",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        start = 20.dp, top = 20.dp, end = 20.dp, bottom = 12.dp
                    )
                )

                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search or type a custom unit...") },
                    singleLine = true,
                    shape = fieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(8.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(units, key = { it.id }) { unit ->
                        UnitRow(
                            unit = unit,
                            isSelected = selectedUnit?.id == unit.id,
                            onClick = { onUnitSelected(unit) }
                        )
                    }

                    if (units.isEmpty() && query.isNotBlank()) {
                        item {
                            Text(
                                text = "No units match \"$query\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    horizontal = 20.dp, vertical = 12.dp
                                )
                            )
                        }
                    }
                }

                AnimatedVisibility(visible = showAddCustom) {
                    Column {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onAddCustom(query) }
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Add \"${query.trim()}\" as custom unit",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun UnitRow(
    unit: MeasureUnit,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else
                    Color.Transparent
            )
            .padding(horizontal = 20.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = unit.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
            )
            if (!unit.isPreset) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = "custom",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}