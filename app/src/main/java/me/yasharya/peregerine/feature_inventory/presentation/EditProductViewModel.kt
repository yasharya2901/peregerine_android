package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.EditProductUiState
import kotlin.math.roundToLong

@OptIn(ExperimentalCoroutinesApi::class)
class EditProductViewModel(
    private val productId: String,
    private val inventoryUseCases: InventoryUseCases,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProductUiState())
    val uiState: StateFlow<EditProductUiState> = _uiState.asStateFlow()

    // Tracks whether the user has changed anything since the form was loaded.
    // Intentionally NOT part of the state snapshot — it doesn't drive any UI,
    // it is only consulted at the moment the user presses back.
    private var isDirty = false

    private var original: Product? = null

    private val _unitSearchQuery = MutableStateFlow("")
    val unitSearchQuery: StateFlow<String> = _unitSearchQuery.asStateFlow()

    val filteredUnits: StateFlow<List<MeasureUnit>> = _unitSearchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) inventoryUseCases.observeUnits()
            else inventoryUseCases.searchUnits(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            val product = inventoryUseCases.observeProduct(productId).filterNotNull().first()
            original = product
            _uiState.update {
                it.copy(
                    isLoading = false,
                    name = product.name,
                    barcode = product.barcode ?: "",
                    selectedUnit = MeasureUnit(id = "", name = product.unit, isPreset = true),
                    defaultMrp = product.defaultMRP?.fromPaise() ?: "",
                    defaultCostPrice = product.defaultCostPrice?.fromPaise() ?: "",
                    defaultSellingPrice = product.defaultSellingPrice?.fromPaise() ?: "",
                    lowStockThreshold = product.lowStockThreshold?.let { t ->
                        if (t == t.toLong().toDouble()) t.toLong().toString() else "%.2f".format(t)
                    } ?: "",
                    notes = product.notes ?: "",
                )
            }
            // isDirty stays false after the initial load — the user hasn't touched anything yet.
        }
    }

    // ── Field events ──────────────────────────────────────────────────────────

    fun onNameChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(name = v, nameError = null) }
    }

    fun onBarcodeChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(barcode = v) }
    }

    fun onMrpChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(defaultMrp = v) }
    }

    fun onCostPriceChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(defaultCostPrice = v) }
    }

    fun onSellingPriceChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(defaultSellingPrice = v) }
    }

    fun onThresholdChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(lowStockThreshold = v) }
    }

    fun onNotesChange(v: String) {
        isDirty = true
        _uiState.update { it.copy(notes = v) }
    }

    // ── Unit picker ───────────────────────────────────────────────────────────

    fun showUnitPicker() = _uiState.update { it.copy(showUnitPicker = true) }

    fun hideUnitPicker() {
        _uiState.update { it.copy(showUnitPicker = false) }
        _unitSearchQuery.value = ""
    }

    fun onUnitQueryChange(query: String) { _unitSearchQuery.value = query }

    fun onUnitSelected(unit: MeasureUnit) {
        isDirty = true
        _uiState.update { it.copy(selectedUnit = unit, unitError = null, showUnitPicker = false) }
        _unitSearchQuery.value = ""
    }

    fun onAddCustomUnit(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val newUnit = MeasureUnit(name = trimmed, id = Ids.newId(), isPreset = false)
        viewModelScope.launch {
            inventoryUseCases.insertUnit(newUnit)
            onUnitSelected(newUnit)
        }
    }

    fun handleBackPress(navigateBack: () -> Unit) {
        if (isDirty) {
            _uiState.update { it.copy(showDiscardConfirm = true) }
        } else {
            navigateBack()
        }
    }

    fun dismissDiscardConfirm() = _uiState.update { it.copy(showDiscardConfirm = false) }

    // ── Save ──────────────────────────────────────────────────────────────────

    fun save() {
        val state = _uiState.value
        val prod = original ?: return
        var hasError = false

        if (state.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Product name is required") }
            hasError = true
        }
        if (state.selectedUnit == null) {
            _uiState.update { it.copy(unitError = "Please select a unit") }
            hasError = true
        }
        if (hasError) return

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                inventoryUseCases.createProduct(
                    prod.copy(
                        name = state.name.trim(),
                        barcode = state.barcode.trim().ifEmpty { null },
                        unit = state.selectedUnit!!.name,
                        defaultMRP = state.defaultMrp.toPaiseOrNull(),
                        defaultCostPrice = state.defaultCostPrice.toPaiseOrNull(),
                        defaultSellingPrice = state.defaultSellingPrice.toPaiseOrNull(),
                        lowStockThreshold = state.lowStockThreshold.toDoubleOrNull(),
                        notes = state.notes.trim().ifEmpty { null },
                        updatedAt = Time.nowEpochMillis(),
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedProductId = productId) }
            } catch (_: Exception) {
                // TODO: surface error (e.g. duplicate barcode)
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun Long.fromPaise(): String {
        val rupees = this / 100.0
        return if (rupees == rupees.toLong().toDouble()) rupees.toLong().toString()
        else "%.2f".format(rupees)
    }

    private fun String.toPaiseOrNull(): Long? =
        trim().toDoubleOrNull()?.let { (it * 100).roundToLong() }
}