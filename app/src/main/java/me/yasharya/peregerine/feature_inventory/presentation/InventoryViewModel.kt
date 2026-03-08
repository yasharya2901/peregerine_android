package me.yasharya.peregerine.feature_inventory.presentation

//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.combine
//import kotlinx.coroutines.launch
//import me.yasharya.peregerine.core.util.Ids
//import me.yasharya.peregerine.core.util.Time
//import me.yasharya.peregerine.feature_inventory.domain.model.Product
//import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
//import me.yasharya.peregerine.feature_inventory.domain.usecase.AdjustStock
//import me.yasharya.peregerine.feature_inventory.domain.usecase.CreateProduct
//import me.yasharya.peregerine.feature_inventory.domain.usecase.DeactivateProduct
//import me.yasharya.peregerine.feature_inventory.domain.usecase.GetAllProducts
//import me.yasharya.peregerine.feature_inventory.domain.usecase.ObserveProduct
//import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryUiState
//
//class InventoryViewModel(
//    private val createProduct: CreateProduct,
//
//    private val observeProduct: ObserveProduct
//) : ViewModel() {
//    private val query = MutableStateFlow("")
//    private val activeOnly = MutableStateFlow(true)
//
//    // 1. Removed the entire broken 'uiState' block that caused the crash.
//
//    private val _state = MutableStateFlow(InventoryUiState())
//    val state: StateFlow<InventoryUiState> = _state
//
//    init {
//        observe()
//    }
//
//    private fun observe() {
//        viewModelScope.launch {
//            combine(query, activeOnly) { q, act -> q to act }.collect { (q, act) ->
//                val flow = if (q.isBlank()) getAllProducts(act) else searchProducts(q, act)
//                flow.collect { products ->
//                    _state.value = _state.value.copy(query = q, products = products, showActiveOnly = act)
//                }
//            }
//        }
//    }
//
//    fun setQuery(q: String) { query.value = q }
//
//    // 2. Fixed boolean logic: Assign the inverted value back to the state
//    fun toggleActiveOnly() {
//        activeOnly.value = !activeOnly.value
//    }
//
//    fun createSampleProduct() {
//        viewModelScope.launch {
//            val now = Time.nowEpochMillis()
//            val p = Product(
//                id = Ids.newId(),
//                name = "Sample Product",
//                barcode = null,
//                sellingPrice = 1000,
//                costPrice = null,
//                unit = "pcs",
//                stockQty = 0.0,
//                lowStockThreshold = 4.0,
//                notes = null,
//                isActive = true,
//                createdAt = now,
//                updatedAt = now
//            )
//            createProduct(p)
//        }
//    }
//
//    fun adjust(productId: String, delta: Double, note: String?) {
//        viewModelScope.launch {
//            val type = if (delta >= 0) StockChangeType.PURCHASE_RECEIPT else StockChangeType.ADJUSTMENT
//            adjustStock(productId, delta, type, note)
//        }
//    }
//
//    fun deactivate(productId: String) {
//        viewModelScope.launch {
//            // 3. Fixed StackOverflowError: call the UseCase, not the function itself
//            deactivateProduct(productId)
//        }
//    }
//}