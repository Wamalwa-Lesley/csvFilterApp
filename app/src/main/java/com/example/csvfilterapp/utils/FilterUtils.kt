package com.example.csvfilterapp.utils

import org.json.JSONObject

object FilterUtils {

    fun matches(json: String, filters: List<DynamicFilter>): Boolean {
        val obj = JSONObject(json)
        return filters.all {
            obj.optString(it.column).contains(it.value, ignoreCase = true)
        }
    }
}
