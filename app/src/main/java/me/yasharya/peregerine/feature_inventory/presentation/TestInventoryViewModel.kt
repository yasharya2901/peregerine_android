package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.yasharya.peregerine.core.util.Ids
import me.yasharya.peregerine.core.util.Time
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases

data class TestInventoryState(
    val products: List<Product> = emptyList(),
    val batches: Map<String, List<Batch>> = emptyMap(),
    val selectedProductId: String? = null,
    val message: String = ""
)

class TestInventoryViewModel(
    private val useCases: InventoryUseCases
) : ViewModel() {

    private val _state = MutableStateFlow(TestInventoryState())
    val state: StateFlow<TestInventoryState> = _state

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            useCases.observeAllProducts(active = true, inactive = true)
                .collect { _ ->
                    // For testing, we'll just show a message
                    // In real app, you'd use Paging 3 properly
                    _state.value = _state.value.copy(
                        message = "Products loaded (using PagingData)"
                    )
                }
        }
    }

    fun createTestProduct() {
        viewModelScope.launch {
            try {
                val now = Time.nowEpochMillis()
                val productId = Ids.newId()

                val product = Product(
                    id = productId,
                    name = "Test Product ${System.currentTimeMillis() % 1000}",
                    barcode = "TEST${System.currentTimeMillis() % 10000}",
                    unit = "pcs",
                    defaultSellingPrice = 10000L, // 100.00 rupees (10000 paise)
                    defaultCostPrice = 7500L,     // 75.00 rupees
                    defaultMRP = 12000L,          // 120.00 rupees
                    lowStockThreshold = 10.0,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                useCases.createProduct(product)
                _state.value = _state.value.copy(
                    message = "✅ Product created: ${product.name}",
                    selectedProductId = productId
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    message = "❌ Error creating product: ${e.message}"
                )
            }
        }
    }

    fun createTestBatch(productId: String? = null) {
        viewModelScope.launch {
            try {
                val targetProductId = productId ?: _state.value.selectedProductId
                if (targetProductId == null) {
                    _state.value = _state.value.copy(
                        message = "❌ No product selected. Create a product first!"
                    )
                    return@launch
                }

                val now = Time.nowEpochMillis()
                val batch = Batch(
                    id = Ids.newId(),
                    productId = targetProductId,
                    purchaseDate = now,
                    mrp = 12000L,         // 120.00 rupees
                    costPrice = 7500L,    // 75.00 rupees
                    sellingPrice = 10000L, // 100.00 rupees
                    qtyOnHand = 50.0,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                useCases.upsertBatch(batch)
                _state.value = _state.value.copy(
                    message = "✅ Batch created: 50 units @ ₹100.00 each"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    message = "❌ Error creating batch: ${e.message}"
                )
            }
        }
    }

    fun createProductWithBatch() {
        viewModelScope.launch {
            try {
                val now = Time.nowEpochMillis()
                val productId = Ids.newId()

                // Create product
                val product = Product(
                    id = productId,
                    name = "Complete Test Product ${System.currentTimeMillis() % 1000}",
                    barcode = "FULL${System.currentTimeMillis() % 10000}",
                    unit = "kg",
                    defaultSellingPrice = 25000L, // 250.00 rupees
                    defaultCostPrice = 18000L,    // 180.00 rupees
                    defaultMRP = 30000L,          // 300.00 rupees
                    lowStockThreshold = 5.0,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                useCases.createProduct(product)

                // Create batch
                val batch = Batch(
                    id = Ids.newId(),
                    productId = productId,
                    purchaseDate = now,
                    mrp = 30000L,         // 300.00 rupees
                    costPrice = 18000L,   // 180.00 rupees
                    sellingPrice = 25000L, // 250.00 rupees
                    qtyOnHand = 100.0,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                useCases.upsertBatch(batch)

                _state.value = _state.value.copy(
                    message = "✅ Created ${product.name} with 100 ${product.unit}",
                    selectedProductId = productId
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    message = "❌ Error: ${e.message}"
                )
            }
        }
    }

    fun observeProduct(productId: String) {
        viewModelScope.launch {
            useCases.observeProduct(productId).collect { product ->
                if (product != null) {
                    _state.value = _state.value.copy(
                        message = "📦 ${product.name} - ${product.unit}"
                    )
                }
            }
        }
    }

    fun observeBatches(productId: String) {
        viewModelScope.launch {
            useCases.observeBatchesForProduct(productId).collect { batches ->
                _state.value = _state.value.copy(
                    batches = _state.value.batches + (productId to batches),
                    message = "📦 Found ${batches.size} batches for product"
                )
            }
        }
    }
}

