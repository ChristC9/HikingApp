package com.example.hikingappuogfinal.data.model

import androidx.room.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

@Entity(
    tableName = "hikes",
    indices = [Index(value = ["name"], unique = false)]
)
data class Hike(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                 // Required
    val location: String,             // Required
    val date: LocalDate,              // Required
    val parkingAvailable: Boolean,    // Required
    val lengthKm: Double,             // Required
    val difficulty: Difficulty,       // Required
    val description: String?,         // Optional

    // Two custom fields (feature a):
    val elevationGainM: Int?,         // Optional but shown and validated if entered
    val groupSize: Int?               // Optional
)

enum class Difficulty { EASY, MODERATE, HARD }

@Entity(
    tableName = "observations",
    foreignKeys = [
        ForeignKey(
            entity = Hike::class,
            parentColumns = ["id"],
            childColumns = ["hikeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hikeId")]
)
data class Observation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hikeId: Long,
    val observation: String,              // Required
    val observedAt: LocalDateTime,        // Required (defaults to now in UI)
    val comments: String?                 // Optional
)

data class HikeWithObsCount(
    @Embedded val hike: Hike,
    @ColumnInfo(name = "obsCount") val obsCount: Int
)