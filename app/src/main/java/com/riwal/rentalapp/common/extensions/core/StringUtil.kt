package com.riwal.rentalapp.common.extensions.core

import android.net.Uri
import android.util.Base64
import android.util.Patterns
import com.riwal.rentalapp.common.extensions.core.StringCapitalizationOptions.*
import com.riwal.rentalapp.common.extensions.core.StringCompareOptions.NUMERIC
import java.util.*
import java.util.regex.Pattern


inline fun <reified T : Enum<T>> String.enum() = tryValueOf<T>(this)

val String.isEmail: Boolean
    get() {
        // Temporary solution to make the unit test work-_-
        val emailPattern = Patterns.EMAIL_ADDRESS
                ?: Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+")
        return this.matches(emailPattern)
    }

val String.isPhoneNumber
    get() = this.matches("^[+]?(?:[-0-9\\s]|\\(\\d+\\))*$")

fun String?.ifNullOrEmpty(defaultValue: String) = if (isNullOrEmpty()) defaultValue else this
fun String.ifEmpty(defaultValue: String) = if (isEmpty()) defaultValue else this
fun String?.ifNullOrBlank(defaultValue: String) = if (isNullOrBlank()) defaultValue else this
fun String.ifBlank(defaultValue: String) = if (isBlank()) defaultValue else this

fun String.matches(regex: String) = this.matches(Pattern.compile(regex))
fun String.matches(pattern: Pattern) = pattern.matcher(this).matches()

fun String.toHours() = toIntOrNull()?.toHours()

fun String.replace(oldValues: List<String>, newValue: String) = oldValues.fold(this) { result, oldValue ->
    result.replace(oldValue, newValue)
}

fun String.mapCharacters(transform: (Char) -> Char) = this.map(transform).joinToString(separator = "")

fun String.replaceCharIf(predicate: (Char) -> Boolean, replacement: Char) = this.mapCharacters { if (predicate(it)) replacement else it }

fun String.remove(oldValues: List<String>) = replace(oldValues, "")
fun String.remove(vararg oldValues: String) = remove(oldValues.toList())

fun String.contains(strings: List<String>, ignoreCase: Boolean = false) = strings.all { this.contains(it, ignoreCase = ignoreCase) }

fun String.decodeBase64() = String(Base64.decode(this, Base64.DEFAULT), Charsets.UTF_8)

fun String.toUri() = try {
    Uri.parse(this)
} catch (e: Exception) {
    null
}

fun String.withCapitalization(locale: Locale = Locale.getDefault(), options: Set<StringCapitalizationOptions>): String {

    val isGerman = locale.language == Locale.GERMAN.language

    return when {
        START_OF_SENTENCE in options -> toLowerCase().capitalize()
        MID_SENTENCE in options && NOUN in options && isGerman -> toLowerCase().capitalize()
        MID_SENTENCE in options -> toLowerCase()
        else -> this
    }
}

enum class StringCapitalizationOptions {
    NOUN,
    MID_SENTENCE,
    START_OF_SENTENCE
}

enum class StringCompareOptions {
    NUMERIC
}

fun String.compareTo(other: String, options: Set<StringCompareOptions>): Int {

    if (options.contains(NUMERIC)) {
        val (thisNormalized, otherNormalized) = listOf(this, other).normalizeNumbers()
        return thisNormalized.compareTo(otherNormalized)
    }

    return this.compareTo(other)
}

fun List<String>.normalizeNumbers(): List<String> {

    val highestNumberOfDigits = this.joinToString(separator = " ")
            .splitWhere { c1, c2 -> c1.isDigit() != c2.isDigit() }
            .filter { it.isInt() }
            .map { it.length }
            .max() ?: 0

    return this.map { it.normalizeNumbers(highestNumberOfDigits) }
}

/*
 * Add zeroes in front of the numbers in this string so they are all the same length.
 * Can be used for number aware lexicographic comparison
 */
fun String.normalizeNumbers(numberOfDigits: Int) = this
        .splitWhere { c1, c2 -> c1.isDigit() != c2.isDigit() }
        .joinToString {
            if (it.isInt()) {
                val numberOfDigitsInNumber = it.length
                val remainingNumberOfDigits = numberOfDigits - numberOfDigitsInNumber
                "0".replicate(remainingNumberOfDigits) + it
            } else {
                it
            }
        }

fun String.splitWhere(delimitingCondition: (a: Char, b: Char) -> Boolean) = this
        .chunked(1)
        .reduceNeighborsWhere({ acc, b -> !delimitingCondition(acc.last(), b.single()) }, String::plus)

// Same as repeat, bus does not throw when n < 0
fun String.replicate(n: Int) = if (n < 0) this else repeat(n)

fun String.isInt() = this.toIntOrNull() != null

fun String.pathExtension() = this.substring(this.lastIndexOf("."))

fun String.preapend(supplier: () -> String) = supplier() + this
