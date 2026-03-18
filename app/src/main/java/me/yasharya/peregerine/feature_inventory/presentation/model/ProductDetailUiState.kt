package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntry

data class ProductDetailUiState (
    val product: Product? = null,
    val batches: List<Batch> = emptyList(),
    val recentLedger: List<StockLedgerEntry> = emptyList(),

    val showAdjustStockDialog: Boolean = false,
    val showAddBatchDialog: Boolean = false,
    val showDeactivateConfirmDialog: Boolean = false,
    val showActivateConfirmDialog: Boolean = false,

    val adjustStockDialog: AdjustStockDialogState = AdjustStockDialogState(),
    val addBatchDialog: AddBatchDialogState = AddBatchDialogState(),

    val operationError: String? = null,
    val isOperationLoading: Boolean = false

)


data class AdjustStockDialogState(
    val selectedBatch: Batch? = null,
    val qtyInput: String = "",
    val isAdding: Boolean = true, // true = Add, false = Remove
    val note: String = "",
    val qtyError: String? = null,
)

fun AdjustStockDialogState.previewText(unit: String): String? {
    val current = selectedBatch?.qtyOnHand ?: return null
    val qty = qtyInput.toDoubleOrNull()?.takeIf{it > 0} ?: return null
    val delta = if (isAdding) qty else -qty
    val result = current + delta
    if (result < 0) return null
    val fmt: (Double) -> String = { v ->
        if (v == kotlin.math.floor(v)) v.toInt().toString() else v.toString()
    }
    return "${fmt(current)} $unit → ${fmt(result)} $unit"
}

data class AddBatchDialogState(
    val purchaseDateMillis: Long = System.currentTimeMillis(),
    val showDatePicker: Boolean = false,
    val mrp: String = "",
    val costPrice: String = "",
    val sellingPrice: String = "",
    val qty: String = "",
    val mrpError: String? = null,
    val costPriceError: String? = null,
    val sellingPriceError: String? = null,
    val qtyError: String? = null
)
