package com.example.csvfilterapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.csvfilterapp.data.dao.CsvRowDao
import com.example.csvfilterapp.data.entity.CsvRowEntity

@Database(
    entities = [CsvRowEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun csvRowDao(): CsvRowDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "csv_db"
                ).build().also { INSTANCE = it }
            }
    }
}
