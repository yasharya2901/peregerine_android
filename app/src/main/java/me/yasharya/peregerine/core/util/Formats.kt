package me.yasharya.peregerine.core.util

fun formatQty(qty: Double): String {
    if (qty == kotlin.math.floor(qty) && !qty.isInfinite()) {
        return qty.toInt().toString()
    }

    return qty.toString()
}

fun Long.fromPaise(): String {
    val rupees = this / 100.0
    return if (rupees == rupees.toLong().toDouble()) rupees.toLong().toString()
        else "%.2f".format(rupees)
}