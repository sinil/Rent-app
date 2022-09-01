package com.riwal.rentalapp.common.extensions.android

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.extensions.json.toJson
import com.riwal.rentalapp.common.extensions.json.toJsonObject

inline operator fun <reified T> SharedPreferences.get(key: String): T? {

    if (key !in this) {
        return null
    }

    return try {
        when (T::class) {
            Boolean::class -> getBoolean(key, false) as T
            Int::class -> getInt(key, 0) as T
            Long::class -> getLong(key, 0L) as T
            Float::class -> getFloat(key, 0f) as T
            String::class -> getString(key, null) as T
            else -> fromJson<T>(getString(key, null), throwOnError = false)
        }
    } catch (exception: ClassCastException) {
        null
    }
}

inline operator fun <reified T> SharedPreferences.set(key: String, value: T?) {

    if (value == null) {
        remove(key)
        return
    }

    when (value) {
        is Boolean -> edit().putBoolean(key, value).apply()
        is Int -> edit().putInt(key, value).apply()
        is Long -> edit().putLong(key, value).apply()
        is Float -> edit().putFloat(key, value).apply()
        is String -> edit().putString(key, value).apply()
        else -> edit().putString(key, value.toJson()).apply()
    }
}

fun SharedPreferences.remove(key: String) {
    edit().remove(key).apply()
}

fun SharedPreferences.string(key: String): String? = this[key]
fun SharedPreferences.bool(key: String): Boolean? = this[key]
fun SharedPreferences.int(key: String): Int? = this[key]
fun SharedPreferences.long(key: String): Long? = this[key]
fun SharedPreferences.float(key: String): Float? = this[key]
fun SharedPreferences.`object`(key: String): Any? = this[key]
fun SharedPreferences.jsonObject(key: String): Map<String, Any>? = this[key]
fun SharedPreferences.jsonList(key: String): List<Any>? = this[key]

// @formatter:off
fun SharedPreferences.whateverIsAt(key: String): Any? {
    if (key !in this) { return null }
    try { return getBoolean(key, false) } catch (e: ClassCastException) { }
    try { return getInt(key, 0) } catch (e: ClassCastException) { }
    try { return getLong(key, 0L) } catch (e: ClassCastException) { }
    try { return getFloat(key, 0f) } catch (e: ClassCastException) { }
    try { return getString(key, null) } catch (e: ClassCastException) { }
    return null
}
// @formatter:on

fun SharedPreferences.renameKey(from: String, to: String) {
    this[to] = whateverIsAt(from)
    remove(from)
}

fun SharedPreferences.toJsonObject() = this.all.mapValues { (_, value) ->
    when (value) {
        is String -> value.toJsonObject() ?: value
        is Number -> value.toDouble()
        else -> value
    }
}

operator fun SharedPreferences.plusAssign(entries: Map<String, Any>) {
    entries.forEach { (key, value) ->
        this[key] = value
    }
}