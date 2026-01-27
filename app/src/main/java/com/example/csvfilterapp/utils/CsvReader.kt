package com.example.csvfilterapp.utils

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.zip.ZipInputStream
import com.example.csvfilterapp.User


object CsvReader {

    fun readUsersFromUri(
        context: Context,
        uri: Uri,
        gender: String
    ): List<User> {

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return emptyList()

        return if (uri.toString().endsWith(".zip")) {
            readFromZip(inputStream)
        } else {
            readFromCsv(inputStream)
        }
    }

    private fun readFromCsv(inputStream: java.io.InputStream): List<User> {
        val users = mutableListOf<User>()

        val reader = BufferedReader(
            InputStreamReader(inputStream, Charsets.UTF_8)
        )

        reader.readLine() // skip header

        reader.forEachLine { line ->
            val row = line.split(",")

            if (row.size < 12) return@forEachLine

            users.add(
                User(
                    firstName = row[0],
                    lastName = row[1],
                    country = row[7],
                    city = row[9],
                    followers = row[5].toIntOrNull() ?: 0,
                    birthYear = row[11].toIntOrNull() ?: 0
                )
            )
        }

        reader.close()
        return users
    }

    private fun readFromZip(inputStream: java.io.InputStream): List<User> {
        val users = mutableListOf<User>()

        val zipInput = ZipInputStream(inputStream)
        var entry = zipInput.nextEntry

        while (entry != null) {
            if (entry.name.endsWith(".csv")) {
                val reader = BufferedReader(
                    InputStreamReader(zipInput, Charsets.UTF_8)
                )

                reader.readLine() // header

                reader.forEachLine { line ->
                    val row = line.split(",")

                    if (row.size < 12) return@forEachLine

                    users.add(
                        User(
                            firstName = row[0],
                            lastName = row[1],
                            country = row[7],
                            city = row[9],
                            followers = row[5].toIntOrNull() ?: 0,
                            birthYear = row[11].toIntOrNull() ?: 0
                        )
                    )
                }
            }
            entry = zipInput.nextEntry
        }

        zipInput.close()
        return users
    }
}
