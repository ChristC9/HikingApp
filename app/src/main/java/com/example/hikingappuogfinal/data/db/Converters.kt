package com.example.hikingappuogfinal.data.db

import androidx.room.TypeConverter
import com.example.hikingappuogfinal.data.model.Difficulty
import kotlinx.datetime.*

class Converters {
    @TypeConverter
    fun difficultyToString(d: Difficulty): String = d.name
    @TypeConverter
    fun stringToDifficulty(s: String): Difficulty = Difficulty.valueOf(s)

    @TypeConverter
    fun localDateToString(d: LocalDate): String = d.toString()
    @TypeConverter
    fun stringToLocalDate(s: String): LocalDate = LocalDate.parse(s)

    @TypeConverter
    fun localDateTimeToString(dt: LocalDateTime): String = dt.toString()
    @TypeConverter
    fun stringToLocalDateTime(s: String): LocalDateTime = LocalDateTime.parse(s)
}