package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.yasharya.peregerine.feature_inventory.domain.model.Product
import me.yasharya.peregerine.feature_inventory.domain.model.StockChangeType
import me.yasharya.peregerine.feature_inventory.domain.model.StockLedgerEntryWithProduct
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.StockLedgerUiState

@OptIn(ExperimentalCoroutinesApi::class)
class StockLedgerViewModel(
    private val inventoryUseCases: InventoryUseCases,
    private val initialProductId: String = ""
): ViewModel() {

    private val _uiState = MutableStateFlow(StockLedgerUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (initialProductId.isNotEmpty()) {
            inventoryUseCases.observeProduct(initialProductId)
                .onEach { product ->
                    if (_uiState.value.selectedProduct == null && product != null) {
                        _uiState.update { it.copy(selectedProduct = product) }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    val searchResults: Flow<PagingData<Product>> = _uiState
        .map { it.productQuery }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            inventoryUseCases.searchProducts(query)
        }
        .cachedIn(viewModelScope)


    val ledgerEntries: Flow<PagingData<StockLedgerEntryWithProduct>> =
        combine(
            _uiState.map {it.selectedProduct?.id }.distinctUntilChanged(),
            _uiState.map {it.selectedType}.distinctUntilChanged()
        ) { productId, type -> productId to type }
            .flatMapLatest { (productId, type) ->
                inventoryUseCases.observeAllStockLedger(productId, type)
            }
            .cachedIn(viewModelScope)

    fun setSearchBarVisibility(visible: Boolean) {
        _uiState.update { it.copy(isSearchBarVisible = visible) }
    }

    fun setProductQuery(query: String) {
        _uiState.update { it.copy(productQuery = query) }
    }

    fun selectProduct(product: Product) {
        _uiState.update { it.copy(
            selectedProduct = product,
            isSearchBarVisible = false,
            productQuery = "",
            selectedType = null
        )}
    }

    fun clearSelectedProduct() {
        _uiState.update { it.copy(
            selectedProduct = null,
            selectedType = null
        ) }
    }

    fun setTypeFilter(type: StockChangeType?) {
        _uiState.update { it.copy(selectedType = type) }
    }
}