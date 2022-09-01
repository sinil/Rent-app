package com.riwal.rentalapp.common

// https://medium.com/@joshfein/handling-null-in-rxjava-2-0-10abd72afa0b

data class Optional<out T>(val value: T?)

fun <T> T?.toOptional() = Optional(this)