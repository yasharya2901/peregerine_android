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
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchAdjustDialogState
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchDetailUiState

class BatchDetailViewModel(
    private val batchId: String,
    private val productId: String,
    private val inventoryUseCases: InventoryUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(BatchDetailUiState())
    val uiState: StateFlow<BatchDetailUiState> = _uiState.asStateFlow()

    init {
        inventoryUseCases.observeBatchById(batchId)
            .onEach { batch ->  _uiState.update { it.copy(batch = batch) } }
            .launchIn(viewModelScope)

        inventoryUseCases.observeProduct(productId)
            .onEach { product ->  _uiState.update { it.copy(product = product) } }
            .launchIn(viewModelScope)
    }

    fun openAdjustDialog() {
        val isProductActive = _uiState.value.product?.isActive ?: true
        _uiState.update {
            it.copy(
                showAdjustDialog = true,
                // Default to Remove for inactive products; Add otherwise
                adjustDialog = BatchAdjustDialogState(isAdding = isProductActive)
            )
        }
    }

    fun closeAdjustDialog() {
        _uiState.update { it.copy(showAdjustDialog = false) }
    }

    fun onAdjustQtyChange(value: String) {
        val sanitised = value.filter { c -> c.isDigit() || c == '.' }
        _uiState.update { state ->
            val dialog = state.adjustDialog
            val batch = state.batch
            val qty = sanitised.toDoubleOrNull()
            val error = when {
                sanitised.isNotEmpty() && qty == null -> "Invalid number"
                qty != null && qty <= 0 -> "Must be greater than 0"
                qty != null && !dialog.isAdding && batch != null && batch.qtyOnHand - qty < 0 ->
                    "Cannot remove more than ${batch.qtyOnHand} ${state.product?.unit ?: ""}"
                else -> null
            }
            state.copy(adjustDialog = dialog.copy(qtyInput = sanitised, qtyError = error))
        }
    }

    fun onAdjustModeChange(isAdding: Boolean) {
        _uiState.update { state ->
            state.copy(adjustDialog = state.adjustDialog.copy(isAdding = isAdding, qtyError = null))
        }
        // Re-validate qty under new mode
        onAdjustQtyChange(_uiState.value.adjustDialog.qtyInput)
    }

    fun onAdjustNoteChange(value: String) {
        _uiState.update { it.copy(adjustDialog = it.adjustDialog.copy(note = value)) }
    }

    fun confirmAdjustStock() {
        val state = _uiState.value
        val batch = state.batch ?: return
        val dialog = state.adjustDialog
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
                _uiState.update { it.copy(showAdjustDialog = false, isOperationLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(operationError = e.message ?: "An unexpected error occurred", isOperationLoading = false)
                }
            }
        }
    }

    fun clearOperationError() {
        _uiState.update { it.copy(operationError = null) }
    }

}