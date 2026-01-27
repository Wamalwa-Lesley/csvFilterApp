package com.example.csvfilterapp.utils

import java.io.InputStream
import java.util.zip.ZipInputStream

object ZipCsvReader {

    fun readZip(
        zipStream: InputStream,
        onCsvStream: (InputStream) -> Unit
    ) {
        val zis = ZipInputStream(zipStream)
        var entry = zis.nextEntry

        while (entry != null) {
            if (!entry.isDirectory && entry.name.endsWith(".csv")) {
                onCsvStream(zis)
            }
            zis.closeEntry()
            entry = zis.nextEntry
        }
    }
}
