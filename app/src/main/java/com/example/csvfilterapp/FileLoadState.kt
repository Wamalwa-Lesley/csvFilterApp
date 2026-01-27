package com.example.csvfilterapp

data class FileLoadState(
    val fileName: String,
    var progress: Int = 0,
    var status: Status = Status.LOADING
) {
    enum class Status {
        LOADING,
        LOADED,
        CANCELLED,
        ERROR
    }
}
