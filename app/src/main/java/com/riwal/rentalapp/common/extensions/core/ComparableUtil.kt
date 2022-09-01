package com.riwal.rentalapp.common.extensions.core

fun <T : Comparable<T>> clamp(value: T, range: ClosedRange<T>) = minOf(maxOf(value, range.start), range.endInclusive)
fun <T : Comparable<T>> T.clampTo(range: ClosedRange<T>) = clamp(this, range)