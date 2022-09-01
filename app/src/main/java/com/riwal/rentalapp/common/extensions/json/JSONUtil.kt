package com.riwal.rentalapp.common.extensions.json

import com.riwal.rentalapp.common.extensions.core.enum
import com.riwal.rentalapp.common.extensions.datetime.hours
import com.riwal.rentalapp.common.extensions.datetime.toDateTime

// Basic JSON types

fun Map<String, Any?>.string(key: String) = this[key] as? String?

fun Map<String, Any?>.number(key: String) = this[key] as? Number?

fun Map<String, Any?>.long(key: String) = number(key)?.toLong()

fun Map<String, Any?>.double(key: String) = number(key)?.toDouble()

fun Map<String, Any?>.boolean(key: String) = this[key] as? Boolean?

fun Map<String, Any?>.list(key: String) = this[key] as? List<*>?

inline fun <reified U> Map<String, Any?>.listOf(key: String) = list(key)?.filterIsInstance<U>()

fun Map<String, Any?>.json(key: String): Map<String, Any?>? = (this[key] as? Map<*, *>?)
        ?.filterKeys { it is String }
        ?.mapKeys { (key, _) -> key as String }

// Extra basic Kotlin types

fun Map<String, Any?>.int(key: String) = number(key)?.toInt()

fun Map<String, Any?>.float(key: String) = number(key)?.toFloat()

// Other types

fun Map<String, Any?>.dateTime(key: String) = string(key)?.toDateTime()

fun Map<String, Any?>.hours(key: String) = long(key)?.hours()

inline fun <reified T : Enum<T>> Map<String, Any?>.enum(key: String) = this[key].toString().enum<T>()

inline fun <reified T : Enum<T>> Map<String, Any?>.enumList(key: String) = list(key)?.mapNotNull { it.toString().enum<T>() }