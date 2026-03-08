package me.yasharya.peregerine.feature_inventory.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.yasharya.peregerine.feature_inventory.domain.model.ProductInventorySummary
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryFilter
import me.yasharya.peregerine.feature_inventory.presentation.model.InventoryUiState


//
@OptIn(ExperimentalCoroutinesApi::class)
class InventoryViewModel(
    private val inventoryUseCases: InventoryUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    val pagedProducts : Flow<PagingData<ProductInventorySummary>> = _uiState
        .map { it.filter to it.searchQuery }
        .distinctUntilChanged()
        .flatMapLatest { (filter, query) ->
            when(filter) {
                InventoryFilter.ALL -> inventoryUseCases.observeProductInventorySummary(activeOnly = true)
                InventoryFilter.LOW_STOCK -> inventoryUseCases.observeLowStockProducts()
                InventoryFilter.OUT_OF_STOCK -> inventoryUseCases.observeOutOfStockProducts()
                InventoryFilter.INACTIVE -> inventoryUseCases.observeProductInventorySummary(activeOnly = false)
            }
        }
        .cachedIn(viewModelScope)

    fun setFilter(filter: InventoryFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
