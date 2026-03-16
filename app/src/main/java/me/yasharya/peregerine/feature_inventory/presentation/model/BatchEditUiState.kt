package me.yasharya.peregerine.feature_inventory.presentation.model

data class BatchEditUiState (
    val isLoading: Boolean = true,
    val purchaseDateMillis: Long = System.currentTimeMillis(),
    val showDatePicker: Boolean = false,
    val mrp: String = "",
    val costPrice: String = "",
    val sellingPrice: String = "",
    val mrpError: String? = null,
    val costPriceError: String? = null,
    val sellingPriceError: String? = null,
    val isSaving: Boolean = false,
    val savedBatchId: String? = null,
    val showDiscardConfirm: Boolean = false,
)