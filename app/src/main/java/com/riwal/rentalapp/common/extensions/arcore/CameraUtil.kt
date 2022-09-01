package com.riwal.rentalapp.common.extensions.arcore

import com.google.ar.core.Camera
import com.google.ar.sceneform.math.Matrix
import com.google.ar.sceneform.math.Vector3

val Camera.worldTransform: Matrix
    get() {
        val matrix = FloatArray(16)
        pose.toMatrix(matrix, 0)
        return Matrix(matrix)
    }

val Camera.direction: Vector3
    get() {
        val matrix = worldTransform
        return Vector3(-1 * matrix.m31, -1 * matrix.m32, -1 * matrix.m33).normalized()
    }

val Camera.worldPosition
    get() = Vector3(pose.translation)