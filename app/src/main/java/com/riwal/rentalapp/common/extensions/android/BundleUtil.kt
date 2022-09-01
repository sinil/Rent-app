package com.riwal.rentalapp.common.extensions.android

import android.os.Bundle
import android.os.Parcelable
import com.riwal.rentalapp.common.extensions.json.fromJson
import com.riwal.rentalapp.common.extensions.json.toJson

inline operator fun <reified T : Any> Bundle.get(key: String): T? {

    if (!containsKey(key)) {
        return null
    }

    return when (T::class) {
        Boolean::class,
        Byte::class,
        Char::class,
        Short::class,
        Int::class,
        Long::class,
        Float::class,
        Double::class,
        CharSequence::class,
        Parcelable::class,
        String::class -> get(key) as T
        else -> fromJson<T>(getString(key, null), throwOnError = true)
    }
}

inline operator fun <reified T : Any> Bundle.set(key: String, value: T?) {

    if (value == null) {
        remove(key)
        return
    }

    when (value) {
        is Boolean -> putBoolean(key, value)
        is Byte -> putByte(key, value)
        is Char -> putChar(key, value)
        is Short -> putShort(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Float -> putFloat(key, value)
        is Double -> putDouble(key, value)
        is CharSequence -> putCharSequence(key, value)
        is String -> putString(key, value)
        is Parcelable -> putParcelable(key, value)
        else -> putString(key, value.toJson())
    }
}

operator fun Bundle.plusAssign(values: Map<String, Any?>) {
    values.forEach { (key, value) ->
        this[key] = value
    }
}

fun Map<String, Any>.toBundle(): Bundle {
    val bundle = Bundle()
    bundle += this
    return bundle
}