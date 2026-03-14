package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit

data class EditProductUiState(
    val isLoading: Boolean = true,

    val name: String = "",
    val barcode: String = "",
    val selectedUnit: MeasureUnit? = null,
    val defaultMrp: String = "",
    val defaultCostPrice: String = "",
    val defaultSellingPrice: String = "",
    val lowStockThreshold: String = "",
    val notes: String = "",

    val showUnitPicker: Boolean = false,
    val showDiscardConfirm: Boolean = false,

    val isSaving: Boolean = false,
    // Non-null on successful save — screen observes this to pop back.
    val savedProductId: String? = null,

    val nameError: String? = null,
    val unitError: String? = null,
)