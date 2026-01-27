package com.example.csvfilterapp.utils

import java.io.*
import java.nio.charset.Charset

object CsvStreamParser {

    fun parse(
        inputStream: InputStream,
        encoding: String,
        onRow: (List<String>) -> Unit
    ) {
        val reader = BufferedReader(
            InputStreamReader(inputStream, Charset.forName(encoding))
        )

        reader.useLines { lines ->
            lines.drop(1).forEach { line -> // skip header
                val row = line.split(",")
                onRow(row)
            }
        }
    }
}
