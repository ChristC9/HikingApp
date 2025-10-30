@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.hikingappuogfinal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hikingappuogfinal.data.model.Difficulty
import com.example.hikingappuogfinal.ui.HikeFormViewModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
fun HikeFormScreen(
    vm: HikeFormViewModel,
    editId: Long?,
    onDone: (Long)->Unit,
    onCancel: ()->Unit
) {
    // Just delegate to the fixed compact screen to avoid duplicate logic
    HikeFormScreenCompact(vm = vm, editId = editId, onDone = onDone, onCancel = onCancel)
}

@Composable
fun HikeFormScreenCompact(
    vm: HikeFormViewModel,
    editId: Long?,
    onDone: (Long)->Unit,
    onCancel: ()->Unit
) {
    LaunchedEffect(editId) { if (editId != null) vm.loadForEdit(editId) }
    val form by vm.form.collectAsState()
    var step by remember { mutableStateOf(1) } // 1=edit, 2=confirm
    val validation = vm.validate()
    val canContinue = validation.errors.isEmpty()

    fun err(key: String) = validation.errors[key]

    Scaffold(topBar = { TopAppBar(title = { Text(if (editId == null) "New Hike" else "Edit Hike") }) }) { pad ->
        Column(Modifier.padding(pad).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (step == 1) {
                OutlinedTextField(
                    value = form.name,
                    onValueChange = { s -> vm.update { f -> f.copy(name = s) } },
                    label = { Text("Name*") },
                    isError = err("name") != null,
                    singleLine = true
                )
                if (err("name") != null) Text(err("name")!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = form.location,
                    onValueChange = { s -> vm.update { f -> f.copy(location = s) } },
                    label = { Text("Location*") },
                    isError = err("location") != null,
                    singleLine = true
                )
                if (err("location") != null) Text(err("location")!!, color = MaterialTheme.colorScheme.error)

                var showDatePicker by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = form.date?.toString().orEmpty(),
                    onValueChange = {},
                    label = { Text("Date*") },
                    isError = err("date") != null,
                    readOnly = true,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )
                if (err("date") != null) Text(err("date")!!, color = MaterialTheme.colorScheme.error)
                if (showDatePicker) {
                    val state = rememberDatePickerState(
                        initialSelectedDateMillis = form.date?.toEpochMillis()
                    )
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val selectedDate = state.selectedDateMillis?.toLocalDate()
                                    if (selectedDate != null) {
                                        vm.update { f -> f.copy(date = selectedDate) }
                                    }
                                    showDatePicker = false
                                }
                            ) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                        }
                    ) {
                        DatePicker(state = state)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FilterChip(
                        selected = form.parking == true,
                        onClick = { vm.update { f -> f.copy(parking = true) } },
                        label = { Text("Parking Yes") }
                    )
                    FilterChip(
                        selected = form.parking == false,
                        onClick = { vm.update { f -> f.copy(parking = false) } },
                        label = { Text("Parking No") }
                    )
                }
                if (err("parking") != null) Text(err("parking")!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = form.lengthKm,
                    onValueChange = { s -> vm.update { f -> f.copy(lengthKm = s) } },
                    label = { Text("Length (km)*") },
                    isError = err("length") != null,
                    singleLine = true
                )
                if (err("length") != null) Text(err("length")!!, color = MaterialTheme.colorScheme.error)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Difficulty.EASY, Difficulty.MODERATE, Difficulty.HARD).forEach { d ->
                        FilterChip(
                            selected = form.difficulty == d,
                            onClick = { vm.update { f -> f.copy(difficulty = d) } },
                            label = { Text(d.name) }
                        )
                    }
                }
                if (err("difficulty") != null) Text(err("difficulty")!!, color = MaterialTheme.colorScheme.error)

                OutlinedTextField(
                    value = form.description,
                    onValueChange = { s -> vm.update { f -> f.copy(description = s) } },
                    label = { Text("Description (optional)") },
                    singleLine = false
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = form.elevationGainM,
                        onValueChange = { s -> vm.update { f -> f.copy(elevationGainM = s) } },
                        label = { Text("Elevation gain (m)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = form.groupSize,
                        onValueChange = { s -> vm.update { f -> f.copy(groupSize = s) } },
                        label = { Text("Group size") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (err("elev") != null) Text("Elevation: ${err("elev")}", color = MaterialTheme.colorScheme.error)
                if (err("group") != null) Text("Group size: ${err("group")}", color = MaterialTheme.colorScheme.error)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    Button(onClick = { if (canContinue) step = 2 }, enabled = canContinue) { Text("Review") }
                }
            } else {
                Text("Confirm details", style = MaterialTheme.typography.titleMedium)
                Divider()
                Text("Name: ${form.name}")
                Text("Location: ${form.location}")
                Text("Date: ${form.date}")
                Text("Parking: ${if (form.parking == true) "Yes" else "No"}")
                Text("Length: ${form.lengthKm} km")
                Text("Difficulty: ${form.difficulty}")
                if (form.description.isNotBlank()) Text("Description: ${form.description}")
                if (form.elevationGainM.isNotBlank()) Text("Elevation gain: ${form.elevationGainM} m")
                if (form.groupSize.isNotBlank()) Text("Group size: ${form.groupSize}")

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = { step = 1 }) { Text("Back to edit") }
                    Button(onClick = { vm.save(onDone) }) { Text(if (form.id == null) "Save" else "Update") }
                }
            }
        }
    }
}

private fun LocalDate.toEpochMillis(): Long =
    this.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

private fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.currentSystemDefault()).date
