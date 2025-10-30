package com.example.hikingappuogfinal.data.repo

import com.example.hikingappuogfinal.data.dao.HikeDao
import com.example.hikingappuogfinal.data.dao.ObservationDao
import com.example.hikingappuogfinal.data.model.Hike
import com.example.hikingappuogfinal.data.model.Observation
import kotlinx.coroutines.flow.Flow

class HikeRepository(
    private val hikeDao: HikeDao,
    private val obsDao: ObservationDao
) {
    fun allHikes() = hikeDao.observeAll()
    fun searchByNamePrefix(prefix: String) = hikeDao.searchByNamePrefix(prefix)
    fun advancedSearch(
        name: String?, location: String?, minLen: Double?, maxLen: Double?, startDateIso: String?, endDateIso: String?
    ) = hikeDao.advancedSearch(name, location, minLen, maxLen, startDateIso, endDateIso)

    suspend fun getHike(id: Long) = hikeDao.getById(id)
    suspend fun insertHike(h: Hike) = hikeDao.insert(h)
    suspend fun updateHike(h: Hike) = hikeDao.update(h)
    suspend fun deleteHike(h: Hike) = hikeDao.delete(h)
    suspend fun resetAll() = hikeDao.deleteAll()

    fun observationsFor(hikeId: Long): Flow<List<Observation>> = obsDao.observeForHike(hikeId)
    suspend fun insertObservation(o: Observation) = obsDao.insert(o)
    suspend fun updateObservation(o: Observation) = obsDao.update(o)
    suspend fun deleteObservation(o: Observation) = obsDao.delete(o)
}