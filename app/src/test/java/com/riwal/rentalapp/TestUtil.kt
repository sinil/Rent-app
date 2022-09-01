package com.riwal.rentalapp

import io.kotlintest.properties.Gen
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.joda.time.DateTime.now
import org.joda.time.DateTimeUtils
import org.joda.time.Duration
import kotlin.reflect.KClass

inline fun <reified T : Any> relaxedMockk(name: String? = null, vararg moreInterfaces: KClass<*>, block: T.() -> Unit = {}): T = mockk(name, relaxed = true, moreInterfaces = *moreInterfaces, block = block)

infix fun <T> (() -> T).shouldThrow(exception: Throwable) {
    shouldThrow(exception, this)
}

fun shouldThrow(exception: Throwable, thunk: () -> Any?) {

    val e = try {
        thunk()
        null
    } catch (e: Throwable) {
        e
    }

    if (e == null) {
        throw AssertionError("Expected exception $exception but no exception was thrown")
    } else if (e != exception) {
        throw AssertionError("Expected exception $exception but exception $e was thrown")
    }
}

inline fun <reified E : Throwable> shouldThrow(thunk: () -> Any?) {

    val e = try {
        thunk()
        null
    } catch (e: Throwable) {
        e
    }

    if (e == null) {
        throw AssertionError("Expected exception of type ${E::class} but no exception was thrown")
    } else if (e !is E) {
        throw AssertionError("Expected exception of type ${E::class} but exception $e was thrown")
    }
}

infix fun <T> (suspend () -> T).shouldThrow(exception: Throwable) {
    coroutineShouldThrow(exception, this)
}

fun coroutineShouldThrow(exception: Throwable, thunk: suspend () -> Any?) {
    shouldThrow(exception) {
        runBlocking {
            thunk()
        }
    }
}

inline fun <reified T : Throwable> coroutineShouldThrow(noinline thunk: suspend () -> Any?) {
    io.kotlintest.shouldThrow<T> {
        runBlocking {
            thunk()
        }
    }
}

fun setNow(dateTime: DateTime, freezeTime: Boolean) {
    if (freezeTime) {
        DateTimeUtils.setCurrentMillisFixed(dateTime.millis)
    } else {
        DateTimeUtils.setCurrentMillisOffset(dateTime.millis - DateTimeUtils.currentTimeMillis())
    }
}

fun resetNow() = DateTimeUtils.setCurrentMillisSystem()

fun <T> eventually(timeout: Duration, f: () -> T): T {
    val end = now() + timeout
    var times = 0
    while (now() < end) {
        try {
            return f()
        } catch (e: Throwable) {
            times++
        }
    }
    throw AssertionError("Test failed after ${timeout.standardSeconds} seconds; attempted $times times")
}

fun <T> nonEmptyList() = mockk<List<T>>(relaxed = true) {
    every { isEmpty() } returns false
    every { isNotEmpty() } returns true
    every { size } returns 2
}

fun String.Companion.random() = Gen.string().random().first()