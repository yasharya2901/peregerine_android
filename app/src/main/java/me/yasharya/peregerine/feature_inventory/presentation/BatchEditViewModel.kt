package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.core.util.fromPaise
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchEditUiState
import kotlin.math.roundToLong

class BatchEditViewModel(
    private val batchId: String,
    private val inventoryUseCases: InventoryUseCases
): ViewModel() {

    private val _uiState = MutableStateFlow(BatchEditUiState())
    val uiState: StateFlow<BatchEditUiState> = _uiState.asStateFlow()

    private var original: Batch? = null
    private var isDirty = false

    init {
        viewModelScope.launch {
            val batch = inventoryUseCases.observeBatchById(batchId).filterNotNull().first()
            original = batch
            _uiState.update {
                it.copy(
                    isLoading = false,
                    purchaseDateMillis = batch.purchaseDate,
                    mrp = batch.mrp.toString(),
                    costPrice = batch.costPrice.fromPaise(),
                    sellingPrice = batch.sellingPrice.fromPaise()
                )
            }
        }
    }

    fun onDateChange(millis: Long) {
        isDirty = true
        _uiState.update { it.copy(purchaseDateMillis = millis, showDatePicker = false) }
    }

    fun onShowDatePicker(show: Boolean) {
        _uiState.update { it.copy(showDatePicker = show) }
    }

    fun onMrpChange(value: String) {
        isDirty = true
        _uiState.update { it.copy(mrp = value, mrpError = null) }
    }

    fun onCostChange(value: String) {
        isDirty = true
        _uiState.update { it.copy(costPrice = value, costPriceError = null) }
    }

    fun onSellingChange(value : String) {
        isDirty = true
        _uiState.update { it.copy(sellingPrice = value, sellingPriceError = null) }
    }

    fun handleBackPress(navigateBack: () -> Unit) {
        if (isDirty) _uiState.update { it.copy(showDiscardConfirm = true) }
        else navigateBack()
    }

    fun dismissDiscardConfirm() {
        _uiState.update { it.copy(showDiscardConfirm = false) }
    }

    fun save() {
        val state = _uiState.value
        val orig = original ?: return

        var hasError = false
        val mrp = state.mrp.toPaiseOrNull()
        val cost = state.costPrice.toPaiseOrNull()
        val sell = state.sellingPrice.toPaiseOrNull()

        if (mrp == null) {
            _uiState.update { it.copy(mrpError = "Required") }
            hasError = true
        }

        if (cost == null) {
            _uiState.update { it.copy(costPriceError = "Required") }
            hasError = true
        }

        if (sell == null) {
            _uiState.update { it.copy(sellingPriceError = "Required") }
            hasError = true
        }

        if (hasError) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                inventoryUseCases.upsertBatch(
                    orig.copy(
                        purchaseDate = state.purchaseDateMillis,
                        mrp = mrp!!,
                        costPrice = cost!!,
                        sellingPrice = sell!!,
                        updatedAt = Time.nowEpochMillis()
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedBatchId = batchId) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun String.toPaiseOrNull(): Long? =
        trim().toDoubleOrNull()?.let { (it * 100).roundToLong() }
}