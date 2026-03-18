package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit
import me.yasharya.peregerine.feature_inventory.domain.model.OpeningStock
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.AddProductUiState
import kotlin.math.roundToLong

@OptIn(ExperimentalCoroutinesApi::class)
class AddProductViewModel(
    private val inventoryUseCases: InventoryUseCases
): ViewModel() {
    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    private val _unitSearchQuery = MutableStateFlow("")
    val unitSearchQuery: StateFlow<String> = _unitSearchQuery.asStateFlow()

    val filteredUnits: StateFlow<List<MeasureUnit>> = _unitSearchQuery
        .flatMapLatest { query ->
            if (query.isEmpty()) inventoryUseCases.observeUnits()
            else inventoryUseCases.searchUnits(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }
    fun onBarcodeChange(value: String) = _uiState.update { it.copy(barcode = value) }

    fun showUnitPicker() = _uiState.update { it.copy(showUnitPicker = true) }
    fun hideUnitPicker() {
        _uiState.update {it.copy(showUnitPicker = false)}
        _unitSearchQuery.value = ""
    }

    fun onUnitQueryChange(query: String) {
        _unitSearchQuery.value = query
    }

    fun onUnitSelected(unit: MeasureUnit) {
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

    fun onMrpChange(value: String) = _uiState.update { it.copy(defaultMrp = value) }
    fun onCostPriceChange(value: String) = _uiState.update { it.copy(defaultCostPrice = value) }
    fun onSellingPriceChange(value: String) = _uiState.update { it.copy(defaultSellingPrice = value) }

    fun onThresholdChange(value: String) = _uiState.update { it.copy(lowStockThreshold = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value)  }

    fun onOpeningStockToggle(enabled: Boolean) = _uiState.update { it.copy(addOpeningStock = enabled) }
    fun onOpeningQtyChange(value: String) =
        _uiState.update { it.copy(openingQty = value, openingQtyError = null) }

    fun onOpeningMrpChange(value: String) = _uiState.update { it.copy(openingMrp = value, openingMrpError = null) }
    fun onOpeningCostChange(value: String) = _uiState.update { it.copy(openingCostPrice = value, openingCostPriceError = null) }
    fun onOpeningSellingChange(value: String) = _uiState.update { it.copy(openingSellingPrice = value, openingSellingPriceError = null) }

    fun save(){
        val state = _uiState.value

        // Validate basic fields
        var hasError = false

        if (state.name.isBlank()) {
            // TODO: Look for future implementation for getting strings from Android Resources
            _uiState.update { it.copy(nameError = "Product name is required") }
            hasError = true
        }

        if (state.selectedUnit == null) {
            _uiState.update { it.copy(unitError = "Please select a unit") }
            hasError = true
        }

        var openingStock: OpeningStock? = null
        if (state.addOpeningStock) {
            val qty = state.openingQty.toDoubleOrNull()
            val mrp = state.openingMrp.toPaiseOrNull()
            val costPrice = state.openingCostPrice.toPaiseOrNull()
            val sellingPrice = state.openingSellingPrice.toPaiseOrNull()

            if (qty == null || qty <= 0) {
                _uiState.update { it.copy(openingQtyError = "Enter a valid quantity") }
                hasError = true
            }

            if (mrp == null) {
                _uiState.update { it.copy(openingMrpError = "Required") }
                hasError = true
            }

            if (costPrice == null) {
                _uiState.update { it.copy(openingCostPriceError = "Required") }
                hasError = true
            }

            if (sellingPrice == null) {
                _uiState.update { it.copy(openingSellingPriceError = "Required") }
                hasError = true
            }

            if (!hasError && qty != null && mrp != null && costPrice != null && sellingPrice != null) {
                openingStock = OpeningStock(
                    qty = qty,
                    mrp = mrp,
                    costPrice = costPrice,
                    sellingPrice = sellingPrice
                )
            }

        }

        if (hasError) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val now = Time.nowEpochMillis()
                val productId = Ids.newId()
                val product = Product(
                    id = productId,
                    name = state.name.trim(),
                    barcode = state.barcode.trim().ifEmpty { null },
                    unit = state.selectedUnit!!.name,
                    defaultSellingPrice = state.defaultSellingPrice.toPaiseOrNull(),
                    defaultCostPrice = state.defaultCostPrice.toPaiseOrNull(),
                    defaultMRP = state.defaultMrp.toPaiseOrNull(),
                    lowStockThreshold = state.lowStockThreshold.toDoubleOrNull(),
                    notes = state.notes.trim().ifEmpty { null },
                    isActive = true,
                    createdAt = now,
                    updatedAt = now,
                )

                if (openingStock != null) {
                    inventoryUseCases.createProductWithOpeningStock(
                        product = product,
                        openingStock = openingStock
                    )
                } else {
                    inventoryUseCases.createProduct(product)
                }
                _uiState.update { it.copy(isLoading = false, savedProductId = productId) }
            } catch (e: Exception) {
                // TODO: Handle for unknown error
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun String.toPaiseOrNull(): Long? = this.trim().toDoubleOrNull()?.let {
        (it * 100).roundToLong()
    }

}