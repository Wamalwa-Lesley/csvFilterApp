package com.example.csvfilterapp.utils

import android.content.Context
import android.net.Uri
import com.example.csvfilterapp.data.dao.CsvRowDao
import com.example.csvfilterapp.data.entity.CsvRowEntity
import kotlinx.coroutines.ensureActive
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream
import kotlin.coroutines.coroutineContext

object CsvZipImporter {

    private const val BATCH_SIZE = 200

    suspend fun import(
        context: Context,
        uri: Uri,
        dao: CsvRowDao,
        onProgress: (Int) -> Unit
    ) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            if (uri.toString().endsWith(".zip")) {
                ZipInputStream(inputStream).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(".csv")) {
                            val reader = BufferedReader(InputStreamReader(zip))
                            readCsv(reader, entry.name, dao, onProgress)
                        }
                        zip.closeEntry()
                        entry = zip.nextEntry
                    }
                }
            } else {
                val reader = BufferedReader(InputStreamReader(inputStream))
                readCsv(
                    reader,
                    uri.lastPathSegment ?: "file.csv",
                    dao,
                    onProgress
                )
            }
        }
    }

    private suspend fun readCsv(
        reader: BufferedReader,
        sourceFile: String,
        dao: CsvRowDao,
        onProgress: (Int) -> Unit
    ) {
        val headerLine = reader.readLine() ?: return
        val headers = headerLine.split(",")

        val batch = mutableListOf<CsvRowEntity>()
        var processed = 0

        var line: String?
        while (true) {
            coroutineContext.ensureActive() // ✅ cancellation-safe

            line = reader.readLine() ?: break

            val values = line.split(",")
            val json = buildJson(headers, values)

            batch.add(
                CsvRowEntity(
                    sourceFile = sourceFile,
                    dataJson = json
                )
            )

            processed++

            if (batch.size >= BATCH_SIZE) {
                dao.insertBatch(batch)
                batch.clear()
            }

            if (processed % 50 == 0) {
                onProgress((processed % 1000) / 10)
            }
        }

        if (batch.isNotEmpty()) {
            dao.insertBatch(batch)
        }

        onProgress(100)
    }

    /**
     * Lightweight JSON builder (NO JSONObject → NO OOM)
     */
    private fun buildJson(headers: List<String>, values: List<String>): String {
        val sb = StringBuilder("{")
        headers.forEachIndexed { index, key ->
            val value = values.getOrNull(index)
                ?.trim()
                ?.replace("\"", "\\\"")
                ?: ""

            sb.append("\"")
                .append(key.trim())
                .append("\":\"")
                .append(value)
                .append("\"")

            if (index < headers.lastIndex) sb.append(",")
        }
        sb.append("}")
        return sb.toString()
    }
}
