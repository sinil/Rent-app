package com.riwal.rentalapp.common.extensions.core

inline fun <reified T : Enum<T>> tryValueOf(value: String?): T? {
    return if (value == null) null else try {
        enumValueOf<T>(value)
    } catch (error: IllegalArgumentException) {
        null
    }
}
