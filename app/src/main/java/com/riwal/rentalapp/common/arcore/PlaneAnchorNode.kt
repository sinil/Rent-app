package com.riwal.rentalapp.common.arcore

import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.Plane.Type.HORIZONTAL_DOWNWARD_FACING
import com.google.ar.core.Plane.Type.HORIZONTAL_UPWARD_FACING
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.riwal.rentalapp.common.arcore.PlaneAnchorNode.Alignment.HORIZONTAL
import com.riwal.rentalapp.common.arcore.PlaneAnchorNode.Alignment.VERTICAL
import com.riwal.rentalapp.common.extensions.arcore.Vector3
import com.riwal.rentalapp.common.extensions.arcore.rotation

class PlaneAnchorNode(
        val anchor: Anchor,
        private val plane: Plane
) : Node() {

    enum class Alignment {
        HORIZONTAL,
        VERTICAL
    }

    val center
        get() = Vector3(anchor.pose.translation)

    val extent
        get() = Vector3(plane.extentX, 0f, plane.extentZ)

    val alignment
        get() = if (plane.type.isHorizontal) HORIZONTAL else VERTICAL


    override fun onUpdate(frameTime: FrameTime) {
        worldPosition = Vector3(anchor.pose.translation)

        if (alignment == VERTICAL) {
            val planeNormal = Quaternion.rotateVector(plane.centerPose.rotation, Vector3.down())
            worldRotation = Quaternion.lookRotation(planeNormal, Vector3.up())
        }
    }
}

val PlaneAnchorNode.squareMeters
    get() = extent.x * extent.z

val Plane.Type.isHorizontal
    get() = this in listOf(HORIZONTAL_DOWNWARD_FACING, HORIZONTAL_UPWARD_FACING)