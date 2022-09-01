package com.riwal.rentalapp.common.extensions.arcore

import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Vector3

val Matrix.m11
    get() = data[0]

val Matrix.m12
    get() = data[1]

val Matrix.m13
    get() = data[2]

val Matrix.m14
    get() = data[3]

val Matrix.m21
    get() = data[4]

val Matrix.m22
    get() = data[5]

val Matrix.m23
    get() = data[6]

val Matrix.m24
    get() = data[7]

val Matrix.m31
    get() = data[8]

val Matrix.m32
    get() = data[9]

val Matrix.m33
    get() = data[10]

val Matrix.m34
    get() = data[11]

val Matrix.m41
    get() = data[12]

val Matrix.m42
    get() = data[13]

val Matrix.m43
    get() = data[14]

val Matrix.m44
    get() = data[15]

operator fun Matrix.times(other: Vector3) = Vector3(
        m11 * other.x + m21 * other.y + m31 * other.z + m41 * 1,
        m12 * other.x + m22 * other.y + m32 * other.z + m42 * 1,
        m13 * other.x + m23 * other.y + m33 * other.z + m43 * 1
)
