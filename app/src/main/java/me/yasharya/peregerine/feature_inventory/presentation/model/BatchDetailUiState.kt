package me.yasharya.peregerine.feature_inventory.presentation.model

import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.Product

data class BatchDetailUiState(
    val batch: Batch? = null,
    val product: Product? = null,
    val showAdjustDialog: Boolean = false,
    val adjustDialog: BatchAdjustDialogState = BatchAdjustDialogState(),
    val operationError: String? = null,
    val isOperationLoading: Boolean = false,
)

data class BatchAdjustDialogState(
    val qtyInput: String = "",
    val isAdding: Boolean = true,
    val note: String = "",
    val qtyError: String? = null,
)

fun BatchAdjustDialogState.previewText(batch: Batch, unit: String): String? {
    val qty = qtyInput.toDoubleOrNull()?.takeIf { it > 0 } ?: return null
    val delta = if (isAdding) qty else -qty
    val result = batch.qtyOnHand + delta

    if (result < 0) return null

    val fmt: (Double) -> String = { v ->
        if (v == kotlin.math.floor(v)) v.toInt().toString() else v.toString()
    }

    return "${fmt(batch.qtyOnHand)} $unit → ${fmt(result)} $unit"
}