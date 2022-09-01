package com.riwal.rentalapp.common.extensions.json

import com.riwal.rentalapp.common.extensions.datetime.*
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.joda.time.*
import java.lang.reflect.Type

val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(DateTimeAdapter())
        .add(LocalDateTimeAdapter())
        .add(LocalDateAdapter())
        .add(LocalTimeAdapter())
        .add(DurationAdapter())
        .build()

inline fun <reified T> T.toJson() = moshi.adapter(T::class.java).toJson(this)

inline fun <reified T> List<T>.toJson(): String {
    val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    return adapter.toJson(this)
}

inline fun <reified T> fromJson(json: String?, throwOnError: Boolean = false): T? = if (json == null) null else try {
    moshi.adapter(T::class.java).fromJson(json)
} catch (e: Exception) {
    if (throwOnError) {
        throw e
    } else {
        null
    }
}

inline fun <reified T> fromListJson(json: String?, throwOnError: Boolean = false): List<T>? = if (json == null) null else try {
    val type: Type = Types.newParameterizedType(List::class.java, T::class.java)
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    adapter.fromJson(json)
} catch (e: Exception) {
    if (throwOnError) {
        throw e
    } else {
        null
    }
}

inline fun <reified K, reified V> fromMapJson(json: String?, throwOnError: Boolean = false): Map<K, V>? = if (json == null) null else try {
    val type: Type = Types.newParameterizedType(Map::class.java, K::class.java, V::class.java)
    val adapter: JsonAdapter<Map<K, V>> = moshi.adapter(type)
    adapter.fromJson(json)
} catch (e: Exception) {
    if (throwOnError) {
        throw e
    } else {
        null
    }
}

fun String.toJsonObject(throwOnError: Boolean = false): Map<String, Any>? = fromMapJson(this, throwOnError)
fun String.toJsonList(throwOnError: Boolean = false) = fromListJson<Any>(this, throwOnError)

class DateTimeAdapter {

    @ToJson
    fun toJson(value: DateTime) = value.isoString

    @FromJson
    fun fromJson(jsonString: String) = jsonString.toDateTime()

}

class LocalDateTimeAdapter {

    @ToJson
    fun toJson(value: LocalDateTime) = value.isoString

    @FromJson
    fun fromJson(jsonString: String) = jsonString.toLocalDateTime()

}

class LocalDateAdapter {

    @ToJson
    fun toJson(value: LocalDate) = value.isoString

    @FromJson
    fun fromJson(jsonString: String) = jsonString.toLocalDate()

}

class LocalTimeAdapter {

    @ToJson
    fun toJson(value: LocalTime) = value.isoString

    @FromJson
    fun fromJson(jsonString: String) = jsonString.toLocalTime()

}

class DurationAdapter {

    @ToJson
    fun toJson(value: Duration) = value.millis

    @FromJson
    fun fromJson(millis: Long) = Duration(millis)

}