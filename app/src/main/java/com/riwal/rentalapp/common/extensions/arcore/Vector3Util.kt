package com.riwal.rentalapp.common.extensions.arcore

import com.google.ar.sceneform.math.Vector3
import com.riwal.rentalapp.common.Vector2

fun Vector3(floatArray: FloatArray) = Vector3(floatArray[0], floatArray[1], floatArray[2])

val Vector3.xy
    get() = Vector2(x, y)

val Vector3.xz
    get() = Vector2(x, z)

fun Vector3.to(other: Vector3) = other - this

fun Vector3.distanceTo(other: Vector3) = this.to(other).length()

fun Vector3.directionTo(other: Vector3) = this.to(other).normalized()

operator fun Vector3.minus(other: Vector3) = Vector3.subtract(this, other)

operator fun Vector3.plus(other: Vector3) = Vector3.add(this, other)

operator fun Vector3.times(scalar: Float) = this.scaled(scalar)