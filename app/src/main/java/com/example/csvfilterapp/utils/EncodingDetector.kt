package com.example.csvfilterapp.utils

import org.mozilla.universalchardet.UniversalDetector
import java.io.InputStream

object EncodingDetector {

    fun detect(inputStream: InputStream): String {
        val detector = UniversalDetector(null)
        val buffer = ByteArray(4096)
        var nread: Int

        while (inputStream.read(buffer).also { nread = it } > 0 && !detector.isDone) {
            detector.handleData(buffer, 0, nread)
        }

        detector.dataEnd()
        val encoding = detector.detectedCharset
        detector.reset()

        return encoding ?: "UTF-8"
    }
}
