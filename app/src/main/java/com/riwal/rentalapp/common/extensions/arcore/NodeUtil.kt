package com.riwal.rentalapp.common.extensions.arcore

import android.util.Log
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.collision.Sphere
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3.zero

var Node.isVisible
    get() = isEnabled
    set(value) {
        isEnabled = value
    }

val Node.center
    get() = when (val collisionShape = renderable?.collisionShape) {
        is Box -> collisionShape.center
        is Sphere -> collisionShape.center
        else -> zero()
    }

val Node.worldTransform
    get() = worldModelMatrix

val Node.worldCenter
    get() = worldTransform * center

fun Node.convertPositionToScreenSpace(localPosition: Vector3, camera: Camera): Vector3 {
    val localPositionInWorld = localToWorldPoint(localPosition)
    return camera.worldToScreenPoint(localPositionInWorld)
}