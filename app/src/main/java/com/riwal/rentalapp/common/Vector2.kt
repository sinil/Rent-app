package com.riwal.rentalapp.common

import android.graphics.PointF
import com.riwal.rentalapp.common.extensions.core.lerp
import kotlin.math.sqrt

data class Vector2(val x: Float, val y: Float)

fun lerp(a: Vector2, b: Vector2, fraction: Float) = Vector2(
        lerp(a.x, b.x, fraction),
        lerp(a.y, b.y, fraction)
)

val Vector2.length
    get() = sqrt(x * x + y * y)

fun Vector2.to(other: Vector2) = other - this

fun Vector2.directionTo(other: Vector2) = to(other).normalized()

fun Vector2.normalized(): Vector2 {
    val scale = 1f / length
    return Vector2(x * scale, y * scale)
}

fun Vector2.toPoint() = PointF(x, y)

operator fun Vector2.minus(other: Vector2) = Vector2(x - other.x, y - other.y)