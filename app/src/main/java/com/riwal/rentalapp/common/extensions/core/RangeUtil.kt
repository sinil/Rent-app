package com.riwal.rentalapp.common.extensions.core

typealias FloatRange = ClosedRange<Float>

fun <T> ClosedRange<T>.toFloatRange(): FloatRange where T : Number, T : Comparable<T> {
    return start.toFloat()..endInclusive.toFloat()
}

fun ClosedRange<Float>.toBoundingIntRange() = start.roundDown().toInt()..endInclusive.roundUp().toInt()

operator fun <T> ClosedRange<T>.contains(other: ClosedRange<T>): Boolean where T : Comparable<T> {
    return other.start in this && other.endInclusive in this
}

infix fun <T> ClosedRange<T>.overlaps(other: ClosedRange<T>): Boolean where T : Comparable<T> {
    return !isDisjoint(this, other)
}

fun <T> isDisjoint(lhs: ClosedRange<T>, rhs: ClosedRange<T>): Boolean where T : Comparable<T> {
    return rhs.endInclusive < lhs.start || rhs.start > lhs.endInclusive
}