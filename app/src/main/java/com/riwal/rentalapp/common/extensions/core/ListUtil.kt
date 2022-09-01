package com.riwal.rentalapp.common.extensions.core

import java.util.*

val <T> List<T>.randomItem
    get() = this[Random().nextInt(this.size)]

fun <T> List<T>.toggle(element: T) = if (element in this) this - element else this + element

fun <T> List<T>.isNoneNull() = none { it == null }

fun <T> List<T>.replaceElement(old: T, new: T): List<T> {
    val index = indexOf(old)
    if (index == -1) {
        return this + new
    }
    return slice(0 until indexOf(old)) + new + slice((indexOf(old) + 1)..lastIndex)
}

fun <T> List<T>.replaceFirstWhere(predicate: (T) -> Boolean, new: T): List<T> {
    val index = indexOfFirst(predicate)
    if (index == -1) {
        return this + new
    }
    return replaceElement(this[index], new)
}

fun <T> List<T>.elementAfter(other: T, wrap: Boolean = false): T? {
    var index = indexOf(other)
    if (index == -1) {
        return null
    }
    index += 1
    return if (index in indices) this[index] else if (wrap) this[0] else null
}

val <T> List<T>.indices
    get() = 0 until size

fun <T> Collection<T>.tail() = drop(1)

fun <T> listOf(count: Int, generator: (Int) -> T) = (0 until count).map { generator(it) }

fun <T, P> List<T>.pairUp(transform: (T, T) -> P): List<P> {

    if (size.isOdd) {
        throw IllegalArgumentException("Input must not be odd")
    }

    var pairs = listOf<P>()
    var i = 0
    while (i < size) {
        pairs += transform(this[i], this[i + 1])
        i += 2
    }
    return pairs
}

/**
 * Groups together subsequent items as long as the condition holds. At the moment the condition does
 * not hold between the current element and the next, the next element is put in a new group.
 *
 * E.g. listOf(1, 2, 4, 5, 6, 10, 12).accumulateWhile { a, b -> a.isEven != b.isEven }, groups
 * together 2 subsequent items if the one is even and the other is uneven:
 * listOf(
 *   listOf(1, 2),
 *   listOf(4, 5, 6),
 *   listOf(10),
 *   listOf(12)
 * )
 */
fun <T> List<T>.groupNeighborsWhere(condition: (current: T, next: T) -> Boolean): List<List<T>> {
    return when (this.size) {
        0 -> emptyList()
        1 -> listOf(this)
        else -> {
            val indexWhereConditionStoppedHolding = (1 until size).find { i -> !condition(this[i - 1], this[i]) } ?: size
            val (matchingChain, rest) = splitBefore(indexWhereConditionStoppedHolding)
            return listOf(matchingChain) + rest.groupNeighborsWhere(condition)
        }
    }
}

fun <T> List<T>.splitBefore(index: Int) = Pair(take(index), drop(index))

fun <T> List<T>.groupNeighborsWhereEqual(vararg propertySelectors: T.() -> Any?) = groupNeighborsWhere { a, b -> propertySelectors.all { a.it() == b.it() } }

fun <T> List<T>.reduceNeighborsWhere(condition: (subResult: T, nextElement: T) -> Boolean, operation: (subResult: T, nextElement: T) -> T): List<T> {

    if (size < 2) {
        return this
    }

    var subResult = this.first()

    for (i in 1 until size) {
        val nextElement = this[i]
        if (condition(subResult, nextElement)) {
            subResult = operation(subResult, nextElement)
        } else {
            return listOf(subResult) + drop(i).reduceNeighborsWhere(condition, operation)
        }
    }

    return listOf(subResult)
}