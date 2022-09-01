package com.riwal.rentalapp.common.extensions.core

fun <T : Comparable<T>> Iterable<T>.range(): ClosedRange<T>? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var min = iterator.next()
    var max = min
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (min > e) min = e
        if (max < e) max = e
    }
    return min..max
}