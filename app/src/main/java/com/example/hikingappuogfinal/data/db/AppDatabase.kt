package com.example.hikingappuogfinal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hikingappuogfinal.data.dao.HikeDao
import com.example.hikingappuogfinal.data.dao.ObservationDao
import com.example.hikingappuogfinal.data.model.Hike
import com.example.hikingappuogfinal.data.model.Observation

@Database(
    entities = [Hike::class, Observation::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hikeDao(): HikeDao
    abstract fun observationDao(): ObservationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mhike.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
    }
}