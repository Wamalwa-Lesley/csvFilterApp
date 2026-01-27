package com.example.csvfilterapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.csvfilterapp.data.entity.CsvRowEntity

@Dao
interface CsvRowDao {

    @Insert
    suspend fun insertBatch(rows: List<CsvRowEntity>)

    @Query("""
        SELECT * FROM rows
        ORDER BY id ASC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun loadPaged(
        limit: Int,
        offset: Int
    ): List<CsvRowEntity>

    @Query("DELETE FROM rows WHERE sourceFile = :file")
    suspend fun deleteBySourceFile(file: String)

    @Query("SELECT dataJson FROM rows LIMIT 1")
    suspend fun getAnyJsonRow(): String?
}
