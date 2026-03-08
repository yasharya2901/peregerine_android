package me.yasharya.peregerine.feature_inventory.presentation.screens
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import me.yasharya.peregerine.feature_inventory.presentation.InventoryViewModel
//
//@Composable
//fun InventoryListScreen(vm: InventoryViewModel) {
//    val state by vm.state.collectAsState()
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//            OutlinedTextField(
//                value = state.query,
//                onValueChange = vm::setQuery,
//                modifier = Modifier.weight(1f),
//                label = { Text("Search products") }
//            )
//            Button(onClick = vm::createSampleProduct) {
//                Text("Add sample")
//            }
//        }
//
//        Spacer(Modifier.height(8.dp))
//
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//            Text("Products: ${state.products.size}")
//            TextButton(onClick = vm::toggleActiveOnly) {
//                Text(if (state.showActiveOnly) "Active only" else "All")
//            }
//        }
//
//        Spacer(Modifier.height(8.dp))
//
//        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//            items(state.products, key = { it.id }) { p ->
//                Card(Modifier.fillMaxWidth()) {
//                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
//                        Text(p.name, style = MaterialTheme.typography.titleMedium)
//                        Text("Stock: ${p.stockQty} ${p.unit}")
//                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
//                            Button(onClick = { vm.adjust(p.id, +1.0, "Restock") }) { Text("+1") }
//                            Button(onClick = { vm.adjust(p.id, -1.0, "Adjustment") }) { Text("-1") }
//                            OutlinedButton(onClick = { vm.deactivate(p.id) }) { Text("Deactivate") }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}