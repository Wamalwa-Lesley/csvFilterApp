package com.example.csvfilterapp.utils

import org.json.JSONObject

object JsonUtils {
    fun jsonToMap(json: String): Map<String, String> {
        val obj = JSONObject(json)
        val map = mutableMapOf<String, String>()
        val keys = obj.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = obj.optString(key, "")
        }
        return map
    }
}
