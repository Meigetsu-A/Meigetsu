package com.meigetsu.feature.reader

enum class ReaderMode {
    VERTICAL, HORIZONTAL, WEBTOON
}

sealed class ReaderState {
    object Loading : ReaderState()
    data class Success(val pages: List<String>, val mode: ReaderMode) : ReaderState()
    data class Error(val message: String) : ReaderState()
}
