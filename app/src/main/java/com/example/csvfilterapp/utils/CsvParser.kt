package com.example.csvfilterapp.utils

import java.io.BufferedReader

object CsvParser {

    fun parse(
        reader: BufferedReader,
        onRow: (List<String>) -> Unit
    ) {
        var line: String?

        while (true) {
            line = reader.readLine() ?: break

            if (line.isBlank()) continue

            // Simple CSV split (no quotes handling for now)
            val columns = line.split(',')

            onRow(columns)
        }
    }
}
