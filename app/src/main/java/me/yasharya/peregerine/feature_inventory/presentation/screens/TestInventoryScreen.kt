package me.yasharya.peregerine.feature_inventory.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.yasharya.peregerine.feature_inventory.presentation.TestInventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestInventoryScreen(vm: TestInventoryViewModel) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Inventory - Create Products & Batches") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status message
            if (state.message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Quick Actions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Quick Test Actions",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Button(
                        onClick = { vm.createTestProduct() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("➕ Create Test Product")
                    }

                    Button(
                        onClick = { vm.createTestBatch() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = state.selectedProductId != null
                    ) {
                        Text("📦 Create Batch for Selected Product")
                    }

                    Button(
                        onClick = { vm.createProductWithBatch() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("✅ Create Complete Product + Batch")
                    }
                }
            }

            // Selected Product Info
            state.selectedProductId?.let { productId ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Selected Product",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "ID: ${productId.take(8)}...",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { vm.observeProduct(productId) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("View Product")
                            }
                            OutlinedButton(
                                onClick = { vm.observeBatches(productId) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("View Batches")
                            }
                        }
                    }
                }
            }

            // Batch display
            if (state.batches.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Batches Found",
                            style = MaterialTheme.typography.titleMedium
                        )

                        state.batches.forEach { (productId, batches) ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "Product: ${productId.take(8)}...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                batches.forEach { batch ->
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.surfaceVariant,
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Text("Qty: ${batch.qtyOnHand} units")
                                            Text("Price: ₹${batch.sellingPrice.toRupees()}")
                                            Text("Cost: ₹${batch.costPrice.toRupees()}")
                                            Text("Status: ${if (batch.isActive) "Active" else "Inactive"}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Instructions
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Test Instructions",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "1. Click 'Create Test Product' to create a product\n" +
                        "2. Click 'Create Batch' to add inventory to the product\n" +
                        "3. Or use 'Create Complete' to do both at once\n" +
                        "4. Check database to verify data is saved correctly",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

// Helper extension function
private fun Long.toRupees(): String {
    return "%.2f".format(this / 100.0)
}

