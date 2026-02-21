package com.meigetsu.core.common.util

fun String.sanitizeTitle(): String {
    return this.replace(Regex("[^A-Za-z0-9 ]"), "")
        .replace("  ", " ")
        .trim()
}
