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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.yasharya.peregerine.feature_inventory.domain.model.Batch
import me.yasharya.peregerine.feature_inventory.domain.usecase.InventoryUseCases
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchFilter
import me.yasharya.peregerine.feature_inventory.presentation.model.BatchListUiState

@OptIn(ExperimentalCoroutinesApi::class)
class BatchListViewModel(
    private val productId: String,
    private val inventoryUseCases: InventoryUseCases
): ViewModel() {
    private val _uiState = MutableStateFlow(BatchListUiState())
    val uiState: StateFlow<BatchListUiState> = _uiState.asStateFlow()

    val pagedBatches: Flow<PagingData<Batch>> = _uiState
        .map{ it.filter }
        .distinctUntilChanged()
        .flatMapLatest { filter ->
            inventoryUseCases.observePagedBatchesForProduct(
                productId = productId,
                showActive = filter == BatchFilter.ALL || filter == BatchFilter.ACTIVE,
                showInactive = filter == BatchFilter.ALL || filter == BatchFilter.INACTIVE
            )
        }
        .cachedIn(viewModelScope)

    init {
        inventoryUseCases.observeProduct(productId)
            .onEach { product -> _uiState.update { it.copy(product = product) } }
            .launchIn(viewModelScope)
    }

    fun setFilter(filter: BatchFilter) {
        _uiState.update { it.copy(filter = filter) }
    }
}