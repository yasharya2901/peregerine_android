package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.MeasureUnit

data class AddProductUiState (
    val name: String = "",
    val barcode: String = "",

    val selectedUnit: MeasureUnit? = null,

    // Stored as rupee strings, converted to paise on save
    val defaultMrp: String = "",
    val defaultCostPrice: String = "",
    val defaultSellingPrice: String = "",

    val lowStockThreshold: String = "",
    val notes: String = "",

    val addOpeningStock: Boolean = false,
    val openingQty: String = "",
    val openingMrp: String = "",
    val openingCostPrice: String = "",
    val openingSellingPrice: String = "",

    val showUnitPicker: Boolean = false,

    val isLoading: Boolean = false,
    val isSaved: Boolean = false,

    val nameError: String? = null,
    val unitError: String? = null,
    val openingQtyError: String? = null,
    val openingMrpError: String? = null,
    val openingCostPriceError: String? = null,
    val openingSellingPriceError: String? = null,
)