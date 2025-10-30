package com.example.hikingappuogfinal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hikingappuogfinal.data.model.Difficulty
import com.example.hikingappuogfinal.data.model.Hike
import com.example.hikingappuogfinal.data.model.HikeWithObsCount
import com.example.hikingappuogfinal.data.model.Observation
import com.example.hikingappuogfinal.data.repo.HikeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class HikeListViewModel(private val repo: HikeRepository): ViewModel() {
    private val searchPrefix = MutableStateFlow<String>("")
    private val advanced = MutableStateFlow<AdvancedQuery?>(null)

    data class AdvancedQuery(
        val name: String? = null,
        val location: String? = null,
        val minLen: Double? = null,
        val maxLen: Double? = null,
        val startDate: LocalDate? = null,
        val endDate: LocalDate? = null
    )

    val hikes: StateFlow<List<HikeWithObsCount>> =
        combine(searchPrefix, advanced) { q, adv ->
            if (adv != null) {
                repo.advancedSearch(
                    adv.name?.ifBlank { null },
                    adv.location?.ifBlank { null },
                    adv.minLen,
                    adv.maxLen,
                    adv.startDate?.toString(),
                    adv.endDate?.toString()
                )
            } else if (q.isNotBlank()) {
                repo.searchByNamePrefix(q)
            } else repo.allHikes()
        }.flatMapLatest { it }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setPrefix(q: String) { searchPrefix.value = q; advanced.value = null }
    fun setAdvanced(q: AdvancedQuery?) { advanced.value = q }
    fun resetDb() = viewModelScope.launch { repo.resetAll() }
}

class HikeFormViewModel(private val repo: HikeRepository): ViewModel() {
    data class Form(
        val id: Long? = null,
        val name: String = "",
        val location: String = "",
        val date: LocalDate? = null,
        val parking: Boolean? = null,
        val lengthKm: String = "",
        val difficulty: Difficulty? = null,
        val description: String = "",
        val elevationGainM: String = "",
        val groupSize: String = ""
    )
    data class Validation(val errors: Map<String,String>)

    private val _form = MutableStateFlow(Form())
    val form: StateFlow<Form> = _form

    fun loadForEdit(id: Long) = viewModelScope.launch {
        repo.getHike(id)?.let { h ->
            _form.value = Form(
                id = h.id,
                name = h.name,
                location = h.location,
                date = h.date,
                parking = h.parkingAvailable,
                lengthKm = h.lengthKm.toString(),
                difficulty = h.difficulty,
                description = h.description.orEmpty(),
                elevationGainM = h.elevationGainM?.toString().orEmpty(),
                groupSize = h.groupSize?.toString().orEmpty()
            )
        }
    }

    fun update(transform: (Form)->Form) { _form.value = transform(_form.value) }

    fun validate(): Validation {
        val f = _form.value
        val errs = mutableMapOf<String,String>()
        if (f.name.isBlank()) errs["name"] = "Required"
        if (f.location.isBlank()) errs["location"] = "Required"
        if (f.date == null) errs["date"] = "Required"
        if (f.parking == null) errs["parking"] = "Required"
        val len = f.lengthKm.toDoubleOrNull()
        if (len == null || len <= 0) errs["length"] = "Enter a positive number"
        if (f.difficulty == null) errs["difficulty"] = "Required"
        f.elevationGainM.takeIf { it.isNotBlank() }?.toIntOrNull() ?: if (f.elevationGainM.isNotBlank()) errs["elev"] = "Integer" else 0;
        f.groupSize.takeIf { it.isNotBlank() }?.toIntOrNull() ?: if (f.groupSize.isNotBlank()) errs["group"] = "Integer" else 0;
        return Validation(errs)
    }

    fun save(onSaved: (Long)->Unit) = viewModelScope.launch {
        val f = _form.value
        val hike = Hike(
            id = f.id ?: 0,
            name = f.name.trim(),
            location = f.location.trim(),
            date = f.date!!,
            parkingAvailable = f.parking!!,
            lengthKm = f.lengthKm.toDouble(),
            difficulty = f.difficulty!!,
            description = f.description.trim().ifBlank { null },
            elevationGainM = f.elevationGainM.trim().ifBlank { null }?.toInt(),
            groupSize = f.groupSize.trim().ifBlank { null }?.toInt()
        )
        val id = if (hike.id == 0L) repo.insertHike(hike) else { repo.updateHike(hike); hike.id }
        onSaved(id)
    }
}

class HikeDetailViewModel(private val repo: HikeRepository): ViewModel() {
    private val hikeId = MutableStateFlow<Long?>(null)
    val observations = hikeId.filterNotNull().flatMapLatest { repo.observationsFor(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun start(id: Long) { hikeId.value = id }

    fun addObservation(hikeId: Long, text: String, comments: String?) = viewModelScope.launch {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        repo.insertObservation(Observation(hikeId = hikeId, observation = text, observedAt = now, comments = comments))
    }
    fun deleteObservation(obs: Observation) = viewModelScope.launch {
        repo.deleteObservation(obs)
    }
}

@Suppress("UNCHECKED_CAST")
class VmFactory(private val repo: HikeRepository): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T =
        when (modelClass) {
            HikeListViewModel::class.java -> HikeListViewModel(repo) as T
            HikeFormViewModel::class.java -> HikeFormViewModel(repo) as T
            HikeDetailViewModel::class.java -> HikeDetailViewModel(repo) as T
            else -> throw IllegalArgumentException("Unknown VM")
        }
}