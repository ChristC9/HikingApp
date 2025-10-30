package com.example.hikingappuogfinal.data.dao
import androidx.room.*
import com.example.hikingappuogfinal.data.model.Observation
import kotlinx.coroutines.flow.Flow

@Dao
interface ObservationDao {
    @Insert suspend fun insert(obs: Observation): Long
    @Update suspend fun update(obs: Observation)
    @Delete suspend fun delete(obs: Observation)

    @Query("SELECT * FROM observations WHERE hikeId=:hikeId ORDER BY observedAt DESC")
    fun observeForHike(hikeId: Long): Flow<List<Observation>>
}