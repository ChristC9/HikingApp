@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hikingappuogfinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hikingappuogfinal.data.model.Observation
import com.example.hikingappuogfinal.ui.HikeDetailViewModel

@Composable
fun HikeDetailScreen(
    id: Long,
    vm: HikeDetailViewModel,
    onBack: ()->Unit
) {
    val list by vm.observations.collectAsState()
    var obsText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hike #$id") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Observations", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = obsText,
                    onValueChange = { obsText = it },
                    label = { Text("Observation*") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Comments") },
                    modifier = Modifier.weight(1f)
                )
            }
            Button(
                onClick = {
                    if (obsText.isNotBlank()) {
                        vm.addObservation(id, obsText.trim(), comments.trim().ifBlank { null })
                        obsText = ""
                        comments = ""
                    }
                },
                enabled = obsText.isNotBlank()
            ) { Text("Add") }

            Divider()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(list, key = { it.id }) { o ->
                    ElevatedCard {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(o.observation, style = MaterialTheme.typography.titleMedium)
                            Text("Time: ${o.observedAt}")
                            if (!o.comments.isNullOrBlank()) Text("Notes: ${o.comments}")
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                TextButton(onClick = { vm.deleteObservation(o) }) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }
}
