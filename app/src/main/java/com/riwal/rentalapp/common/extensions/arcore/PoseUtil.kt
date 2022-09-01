package com.riwal.rentalapp.common.extensions.arcore

import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Quaternion

val Pose.rotation
    get() = Quaternion(qx(), qy(), qz(), qw())