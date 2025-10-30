package com.example.hikingappuogfinal.data.dao

import androidx.room.*
import com.example.hikingappuogfinal.data.model.Hike
import com.example.hikingappuogfinal.data.model.HikeWithObsCount
import kotlinx.coroutines.flow.Flow

@Dao
interface HikeDao {
    @Insert suspend fun insert(hike: Hike): Long
    @Update suspend fun update(hike: Hike)
    @Delete suspend fun delete(hike: Hike)
    @Query("DELETE FROM hikes") suspend fun deleteAll()

    @Query("""
    SELECT h.*, (SELECT COUNT(*) FROM observations o WHERE o.hikeId = h.id) AS obsCount
    FROM hikes h
    ORDER BY h.date DESC
  """)
    fun observeAll(): Flow<List<HikeWithObsCount>>

    @Query("""
    SELECT h.*, (SELECT COUNT(*) FROM observations o WHERE o.hikeId = h.id) AS obsCount
    FROM hikes h
    WHERE h.name LIKE :prefix || '%'
    ORDER BY h.name COLLATE NOCASE ASC
  """)
    fun searchByNamePrefix(prefix: String): Flow<List<HikeWithObsCount>>

    // Advanced search (all nullable filters)
    @Query("""
    SELECT h.*, (SELECT COUNT(*) FROM observations o WHERE o.hikeId = h.id) AS obsCount
    FROM hikes h
    WHERE (:name IS NULL OR h.name LIKE :name || '%')
      AND (:location IS NULL OR h.location LIKE :location || '%')
      AND (:minLen IS NULL OR h.lengthKm >= :minLen)
      AND (:maxLen IS NULL OR h.lengthKm <= :maxLen)
      AND (:startDate IS NULL OR h.date >= :startDate)
      AND (:endDate IS NULL OR h.date <= :endDate)
    ORDER BY h.date DESC
  """)
    fun advancedSearch(
        name: String?,
        location: String?,
        minLen: Double?,
        maxLen: Double?,
        startDate: String?,   // LocalDate as ISO string
        endDate: String?
    ): Flow<List<HikeWithObsCount>>

    @Query("SELECT * FROM hikes WHERE id = :id")
    suspend fun getById(id: Long): Hike?
}