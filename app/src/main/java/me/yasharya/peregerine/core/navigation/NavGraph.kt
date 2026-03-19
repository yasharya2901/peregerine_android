package me.yasharya.peregerine.core.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import me.yasharya.peregerine.App
import me.yasharya.peregerine.feature_inventory.presentation.AddProductViewModel
import me.yasharya.peregerine.feature_inventory.presentation.BarcodeScannerViewModel
import me.yasharya.peregerine.feature_inventory.presentation.BatchDetailViewModel
import me.yasharya.peregerine.feature_inventory.presentation.BatchEditViewModel
import me.yasharya.peregerine.feature_inventory.presentation.BatchListViewModel
import me.yasharya.peregerine.feature_inventory.presentation.EditProductViewModel
import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel
import me.yasharya.peregerine.feature_inventory.presentation.ProductDetailViewModel
import me.yasharya.peregerine.feature_inventory.presentation.StockLedgerViewModel
import me.yasharya.peregerine.feature_inventory.presentation.screens.AddProductScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.BarcodeScannerScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.BatchDetailScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.BatchEditScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.BatchListScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.EditProductScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.InventoryListScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.ProductDetailScreen
import me.yasharya.peregerine.feature_inventory.presentation.screens.StockLedgerScreen

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavGraph() {
    val context = LocalContext.current
    val container = (context.applicationContext as App).container

    val backStack = rememberNavBackStack(AppRoute.Inventory)
    val currentRoute = backStack.lastOrNull() as? AppRoute

    val showTabs = currentRoute?.isRoot() == true

    Scaffold(
        bottomBar = {
            // Rest of the pages will open without tabs
            AnimatedVisibility(
                visible = showTabs,
                enter = slideInVertically(initialOffsetY = {it}),
                exit = slideOutVertically(targetOffsetY = {it})
            ) {
                NavigationBar {
                    BottomNavItem.all.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                val existingRoute = backStack.firstOrNull { it == item.route }
                                if (existingRoute != null) {
                                    backStack.remove(existingRoute)
                                    backStack.add(existingRoute)
                                } else {
                                    backStack.add(item.route)
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = stringResource(item.labelRes)
                                )
                            },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        var pendingScanCallback by remember {mutableStateOf<((String) -> Unit)?>(null)}
        NavDisplay(
            backStack = backStack,
            onBack = {
                val current = backStack.lastOrNull() as? AppRoute
                when {
                    current?.isRoot() == true && current != AppRoute.Inventory -> {
                        backStack.removeAll { (it as? AppRoute)?.isRoot() == true}
                        backStack.add(AppRoute.Inventory)
                    }
                    backStack.size > 1 -> backStack.removeLastOrNull()
                }
            },
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            },
            popTransitionSpec = {
                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            },
            modifier = Modifier.padding(innerPadding),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<AppRoute.Inventory> {
                    val viewModel: InventoryViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer { InventoryViewModel(container.inventoryUseCases) }
                        }
                    )
                    InventoryListScreen(
                        viewModel = viewModel,
                        onAddProduct = {backStack.add(AppRoute.AddProduct)},
                        onProductClick = {productId ->
                            backStack.add(AppRoute.ProductDetail(productId))
                        },
                        onScanBarcode = {
                            pendingScanCallback = viewModel::setSearchQuery
                            backStack.add(AppRoute.ScanBarcode)
                        }
                    )
                }
                entry<AppRoute.AddProduct> {
                    val viewModel: AddProductViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer { AddProductViewModel(container.inventoryUseCases) }
                        }
                    )

                    AddProductScreen(
                        viewModel = viewModel,
                        onBack = {backStack.removeLastOrNull()},
                        onNavigateToProductDetail = {productId ->
                            backStack.removeLastOrNull()
                            backStack.add(AppRoute.ProductDetail(productId))
                        },
                        onScanBarcode = {
                            pendingScanCallback = viewModel::onBarcodeChange
                            backStack.add(AppRoute.ScanBarcode)
                        }
                    )
                }
                entry<AppRoute.ProductDetail> {route ->
                    val viewModel: ProductDetailViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                ProductDetailViewModel(
                                    productId = route.productId,
                                    inventoryUseCases = container.inventoryUseCases
                                )
                            }
                        }
                    )

                    ProductDetailScreen(
                        viewModel = viewModel,
                        onBack = {backStack.removeLastOrNull()},
                        onEditProduct = { productId ->
                            backStack.add(AppRoute.EditProduct(productId))
                        },
                        onFullLedger = { productId ->
                            backStack.add(AppRoute.StockLedger(productId))
                        },
                        onViewAllBatches = { productId ->
                            backStack.add(AppRoute.BatchList(productId))
                        },
                        onBatchClick = { batchId ->
                            backStack.add(AppRoute.BatchDetail(batchId, route.productId))
                        }
                    )
                }
                entry<AppRoute.EditProduct> {route ->
                    val viewModel: EditProductViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                EditProductViewModel(
                                    productId = route.productId,
                                    inventoryUseCases = container.inventoryUseCases
                                )
                            }
                        }
                    )
                    EditProductScreen(
                        viewModel = viewModel,
                        onBack = {backStack.removeLastOrNull()},
                        onScanBarcode = {
                            pendingScanCallback = viewModel::onBarcodeChange
                            backStack.add(AppRoute.ScanBarcode)
                        }
                    )
                }
                entry<AppRoute.ScanBarcode> {
                    val viewModel: BarcodeScannerViewModel = viewModel {
                        BarcodeScannerViewModel()
                    }

                    BarcodeScannerScreen(
                        viewModel = viewModel,
                        onBarcodeScanned = {barcode ->
                            pendingScanCallback?.invoke(barcode)
                            pendingScanCallback = null
                            backStack.removeLastOrNull()
                        },
                        onBack = {
                            pendingScanCallback = null
                            backStack.removeLastOrNull()
                        }
                    )
                }
                entry<AppRoute.BatchList> { route ->
                    val viewModel: BatchListViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                BatchListViewModel(
                                    productId = route.productId,
                                    inventoryUseCases = container.inventoryUseCases
                                )
                            }
                        }
                    )

                    BatchListScreen(
                        viewModel = viewModel,
                        onBack = { backStack.removeLastOrNull() },
                        onBatchClick = { batchId ->
                            backStack.add(AppRoute.BatchDetail(batchId = batchId, productId = route.productId))
                        }
                    )
                }
                entry<AppRoute.BatchDetail> { route ->
                    val viewModel: BatchDetailViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                BatchDetailViewModel(
                                    batchId = route.batchId,
                                    productId = route.productId,
                                    inventoryUseCases = container.inventoryUseCases
                                )
                            }
                        }
                    )
                    BatchDetailScreen(
                        viewModel = viewModel,
                        onBack = { backStack.removeLastOrNull() },
                        onEditBatch = {batchId ->
                            backStack.add(AppRoute.EditBatch(batchId = batchId, productId = route.productId))
                        }
                    )
                }
                entry<AppRoute.EditBatch>{ route ->
                    val viewModel: BatchEditViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                BatchEditViewModel(
                                    batchId = route.batchId,
                                    inventoryUseCases = container.inventoryUseCases
                                )
                            }
                        }
                    )

                    BatchEditScreen(viewModel = viewModel, onBack = { backStack.removeLastOrNull() })
                }

                entry<AppRoute.StockLedger> { entry ->
                    val viewModel: StockLedgerViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                StockLedgerViewModel(inventoryUseCases = container.inventoryUseCases, initialProductId = entry.productId)
                            }
                        }
                    )

                    StockLedgerScreen(
                        viewModel = viewModel,
                        onScanBarcode = {
                            pendingScanCallback = viewModel::setProductQuery
                            backStack.add(AppRoute.ScanBarcode)
                        }
                    )
                }

                entry<AppRoute.PurchaseOrder> {
                    Text("Purchase Order")
                }
                entry<AppRoute.Settings> {
                    Text("Settings")
                }
            }
        )
    }

}