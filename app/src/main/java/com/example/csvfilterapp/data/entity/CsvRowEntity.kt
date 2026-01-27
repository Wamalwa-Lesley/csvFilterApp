package com.example.csvfilterapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rows")
data class CsvRowEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceFile: String,
    val dataJson: String
)
