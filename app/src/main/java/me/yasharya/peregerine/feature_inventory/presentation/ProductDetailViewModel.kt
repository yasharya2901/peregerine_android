package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.repository.InventoryRepository
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.AddBatchDialogState
import me.yasharya.peregerine.feature_inventory.presentation.model.AdjustStockDialogState
import me.yasharya.peregerine.feature_inventory.presentation.model.ProductDetailUiState
import kotlin.math.roundToLong

class ProductDetailViewModel(private val productId: String, private val inventoryUseCases: InventoryUseCases): ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        inventoryUseCases.observeProduct(productId)
            .onEach {product ->
                _uiState.update {it.copy(product = product)}
            }
            .launchIn(viewModelScope)

        inventoryUseCases.observeBatchesForProduct(productId)
            .onEach { batches ->
                val sorted = batches.sortedWith (
                    compareByDescending<Batch> {it.isActive}.thenBy {it.purchaseDate}
                )
                _uiState.update {it.copy(batches = sorted)}
            }
            .launchIn(viewModelScope)

        inventoryUseCases.observeRecentStockLedgerForProduct(productId, limit = 5)
            .onEach { entries -> _uiState.update { it.copy(recentLedger = entries) } }
            .launchIn(viewModelScope)
    }


    fun openAdjustStockDialog() {
        _uiState.update {
            it.copy(
                showAdjustStockDialog = true,
                adjustStockDialog = AdjustStockDialogState()
            )
        }
    }

    fun closeAdjustStockDialog() {
        _uiState.update { it.copy(showAdjustStockDialog = false) }
    }

    fun onAdjustSelectBatch(batch: Batch) {
        _uiState.update {
            it.copy(adjustStockDialog = it.adjustStockDialog.copy(selectedBatch = batch, qtyError = null))
        }
    }

    fun onAdjustQtyChange(value: String) {
        val sanitised = value.filter {c -> c.isDigit() || c == '.'}
        _uiState.update { state ->
            val dialog = state.adjustStockDialog
            val qty = sanitised.toDoubleOrNull()
            val error = when {
                sanitised.isNotEmpty() && qty == null -> "Invalid Number"
                qty != null && qty <= 0 -> "Must be greater than 0"
                qty != null && !dialog.isAdding && dialog.selectedBatch != null && dialog.selectedBatch.qtyOnHand - qty < 0 -> "Cannot remove more than ${dialog.selectedBatch.qtyOnHand} ${state.product?.unit ?: ""}"
                else -> null
            }
            state.copy(
                adjustStockDialog = dialog.copy(qtyInput = sanitised, qtyError = error)
            )
        }
    }


    fun onAdjustModeChange(isAdding: Boolean) {
        _uiState.update { state ->
            state.copy(adjustStockDialog = state.adjustStockDialog.copy(isAdding = isAdding, qtyError = null))
        }
        onAdjustQtyChange(_uiState.value.adjustStockDialog.qtyInput)
    }

    fun onAdjustNoteChange(value: String) {
        _uiState.update { it.copy(adjustStockDialog = it.adjustStockDialog.copy(note = value)) }
    }

    fun confirmAdjustStock() {
        val state = _uiState.value
        val dialog = state.adjustStockDialog
        val batch = dialog.selectedBatch ?: return
        val qty = dialog.qtyInput.toDoubleOrNull()?.takeIf { it > 0 } ?: return

        if (dialog.qtyError != null) return

        val delta = if (dialog.isAdding) qty else -qty

        _uiState.update { it.copy(isOperationLoading = true) }

        viewModelScope.launch {
            try {
                inventoryUseCases.adjustStock(
                    productId = productId,
                    batchId = batch.id,
                    deltaQty = delta,
                    type = StockChangeType.ADJUSTMENT,
                    referenceId = null,
                    note = dialog.note.trim().ifEmpty { null }
                )

                _uiState.update { it.copy(showAdjustStockDialog = false, isOperationLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(operationError = e.message ?: "An unexpected error occurred", isOperationLoading = false) }
            }
        }
    }

    fun openAddBatchDialog() {
        _uiState.update {
            it.copy(
                showAddBatchDialog = true,
                addBatchDialog = AddBatchDialogState()
            )
        }
    }

    fun closeAddBatchDialog() {
        _uiState.update { it.copy(showAddBatchDialog = false) }
    }

    fun onAddBatchDateChange(millis: Long) {
        _uiState.update {
            it.copy(addBatchDialog = it.addBatchDialog.copy(purchaseDateMillis = millis, showDatePicker = false))
        }
    }

    fun onAddBatchShowDatePicker(show: Boolean) {
        _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(showDatePicker = show))}
    }

    fun onAddBatchMrpChange(v: String) {
        _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(mrp = v, mrpError = null)) }
    }

    fun onAddBatchCostChange(v: String) {
        _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(costPrice = v, costPriceError = null)) }
    }

    fun onAddBatchSellingChange(v: String) {
        _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(sellingPrice = v, sellingPriceError = null)) }
    }

    fun onAddBatchQtyChange(v: String) {
        _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(qty = v, qtyError = null)) }
    }

    fun confirmAddBatch() {
        val state = _uiState.value
        val dialog = state.addBatchDialog

        var hasError = false

        val mrp = dialog.mrp.toPaiseOrNull()
        val cost = dialog.costPrice.toPaiseOrNull()
        val sell = dialog.costPrice.toPaiseOrNull()
        val qty = dialog.qty.toDoubleOrNull()?.takeIf { it > 0 }

        if (mrp == null) {
            _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(mrpError = "Required")) }
            hasError = true
        }
        if (cost == null) {
            _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(costPriceError = "Required")) }
            hasError = true
        }
        if (sell == null) {
            _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(sellingPriceError = "Required")) }
            hasError = true
        }
        if (qty == null) {
            _uiState.update { it.copy(addBatchDialog = it.addBatchDialog.copy(qtyError = "Enter a valid quantity")) }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isOperationLoading = true) }

        viewModelScope.launch {
            try {
                val now = Time.nowEpochMillis()
                val batch = Batch(
                    id = Ids.newId(),
                    productId = productId,
                    purchaseDate = dialog.purchaseDateMillis,
                    mrp = mrp!!,
                    costPrice = cost!!,
                    sellingPrice = sell!!,
                    purchaseQty = qty!!,
                    qtyOnHand = qty,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                inventoryUseCases.addBatch(batch)
                _uiState.update { it.copy(showAddBatchDialog = false, isOperationLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        operationError = e.message ?: "An unexpected error occurred",
                        isOperationLoading = false
                    )
                }
            }
        }
    }

    fun openDeactivateConfirmDialog() {
        _uiState.update { it.copy(showDeactivateConfirmDialog = true) }
    }

    fun closeDeactivateConfirmDialog() {
        _uiState.update { it.copy(showDeactivateConfirmDialog = false) }
    }

    fun confirmDeactivate() {
        _uiState.update { it.copy(showDeactivateConfirmDialog = false, isOperationLoading = true) }
        viewModelScope.launch {
            try {
                inventoryUseCases.deactivateProduct(productId)
                _uiState.update { it.copy(isOperationLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isOperationLoading = false, operationError = e.message ?: "Failed to deactivate product")
                }
            }
        }
    }

    fun openActivateConfirmDialog() {
        _uiState.update { it.copy(showActivateConfirmDialog = true) }
    }

    fun closeActivateConfirmDialog() {
        _uiState.update { it.copy(showActivateConfirmDialog = false) }
    }

    fun confirmActivate() {
        _uiState.update { it.copy(showActivateConfirmDialog = false, isOperationLoading = true) }
        viewModelScope.launch {
            try {
                inventoryUseCases.activateProduct(productId)
                _uiState.update { it.copy(isOperationLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isOperationLoading = false, operationError = e.message ?: "Failed to activate product")
                }
            }
        }
    }

    fun clearOperationError() {
        _uiState.update { it.copy(operationError = null) }
    }



    private fun String.toPaiseOrNull(): Long? =
        this.trim().toDoubleOrNull()?.let { (it * 100).roundToLong() }
}