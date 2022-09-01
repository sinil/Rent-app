package com.riwal.rentalapp.common.extensions.core

fun <K, V> Map<K, V>.firstKey(predicate: (key: K, value: V?) -> Boolean) = keys.first { key -> predicate(key, this[key]) }

fun <K, V> Map<K, V>.firstKeyWithValue(value: V) = firstKey { _, v -> value == v }

fun <K, V> Map<K, V>.firstKeyOrNull(predicate: (key: K, value: V?) -> Boolean) = keys.firstOrNull { key -> predicate(key, this[key]) }

fun <K, V> Map<K, V>.firstKeyWithValueOrNull(value: V) = firstKeyOrNull { _, v -> value == v }

fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = this.filterValues { it != null } as Map<K, V>