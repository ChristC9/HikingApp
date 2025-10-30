package com.example.hikingappuogfinal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hikingappuogfinal.data.model.HikeWithObsCount
import com.example.hikingappuogfinal.ui.HikeListViewModel
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeListScreen(
    vm: HikeListViewModel,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onEdit: (Long) -> Unit
) {
    val hikes by vm.hikes.collectAsState()
    var q by remember { mutableStateOf("") }
    var showAdvanced by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M-Hike") },
                actions = {
                    IconButton(onClick = { showAdvanced = true }) { Icon(Icons.Default.Search, contentDescription = "Advanced search") }
                    var showReset by remember { mutableStateOf(false) }
                    IconButton(onClick = { showReset = true }) { Icon(Icons.Default.Delete, contentDescription = "Reset DB") }
                    if (showReset) {
                        AlertDialog(
                            onDismissRequest = { showReset = false },
                            confirmButton = { TextButton(onClick = { vm.resetDb(); showReset = false }) { Text("Delete All") } },
                            dismissButton = { TextButton(onClick = { showReset = false }) { Text("Cancel") } },
                            title = { Text("Reset database?") },
                            text = { Text("This will delete all hikes and observations.") }
                        )
                    }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Icon(Icons.Default.Add, contentDescription = "Add") } }
    ) { pad ->
        Column(Modifier.padding(pad).padding(12.dp)) {
            OutlinedTextField(
                value = q, onValueChange = {
                    q = it; vm.setPrefix(it)
                },
                label = { Text("Search by name") },
                singleLine = true, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            HikeList(hikes = hikes, onOpen = onOpen, onEdit = onEdit)
        }
    }

    if (showAdvanced) {
        AdvancedSearchDialog(
            onDismiss = { showAdvanced = false },
            onApply = { vm.setAdvanced(it); showAdvanced = false }
        )
    }
}

@Composable
private fun HikeList(
    hikes: List<HikeWithObsCount>,
    onOpen: (Long)->Unit,
    onEdit: (Long)->Unit
) {
    if (hikes.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hikes yet. Tap + to add.") }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(hikes, key = { it.hike.id }) { item ->
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().clickable { onOpen(item.hike.id) }
                ) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(Modifier.weight(1f)) {
                            Text(item.hike.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("${item.hike.location} • ${item.hike.date}")
                            Text("${item.hike.lengthKm} km • ${item.hike.difficulty} • Parking: ${if (item.hike.parkingAvailable) "Yes" else "No"}")
                        }
                        TextButton(onClick = { onEdit(item.hike.id) }) { Text("Edit") }
                    }
                    if (item.obsCount > 0) {
                        Divider()
                        Text("${item.obsCount} observation(s)", modifier = Modifier.padding(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedSearchDialog(
    onDismiss: ()->Unit,
    onApply: (HikeListViewModel.AdvancedQuery)->Unit
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var minLen by remember { mutableStateOf("") }
    var maxLen by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") } // yyyy-MM-dd
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    HikeListViewModel.AdvancedQuery(
                        name = name.ifBlank { null },
                        location = location.ifBlank { null },
                        minLen = minLen.toDoubleOrNull(),
                        maxLen = maxLen.toDoubleOrNull(),
                        startDate = startDate.takeIf { it.matches(Regex("""\d{4}-\d{2}-\d{2}""")) }?.let(LocalDate::parse),
                        endDate = endDate.takeIf { it.matches(Regex("""\d{4}-\d{2}-\d{2}""")) }?.let(LocalDate::parse)
                    )
                )
            }) { Text("Apply") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Advanced search") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(name, { name = it }, label={Text("Name starts with")}, singleLine=true)
                OutlinedTextField(location, { location = it }, label={Text("Location starts with")}, singleLine=true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(minLen, { minLen = it }, modifier = Modifier.weight(1f), label={Text("Min km")}, singleLine=true)
                    OutlinedTextField(maxLen, { maxLen = it }, modifier = Modifier.weight(1f), label={Text("Max km")}, singleLine=true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(startDate, { startDate = it }, modifier = Modifier.weight(1f), label={Text("Start date yyyy-MM-dd")})
                    OutlinedTextField(endDate, { endDate = it }, modifier = Modifier.weight(1f), label={Text("End date yyyy-MM-dd")})
                }
            }
        }
    )
}