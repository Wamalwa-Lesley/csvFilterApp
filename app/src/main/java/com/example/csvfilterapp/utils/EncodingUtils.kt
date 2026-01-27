package com.example.csvfilterapp.utils

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

object EncodingUtils {

    fun openUtf8Reader(input: InputStream): BufferedReader {
        return BufferedReader(
            InputStreamReader(input, Charset.forName("UTF-8")),
            64 * 1024 // 64KB buffer (safe)
        )
    }
}
