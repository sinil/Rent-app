package com.riwal.rentalapp.common.extensions.arcore

import android.content.Context
import com.google.ar.core.ArCoreApk
import com.google.ar.sceneform.rendering.Renderable

val Renderable.materials
    get() = (0 until submeshCount).map { getMaterial(it) }


 fun isArSupported(context: Context): Boolean {
     return when {
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.SUPPORTED_INSTALLED -> true
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> false
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> true
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> true
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.UNKNOWN_CHECKING -> false
         ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.UNKNOWN_ERROR -> false
         else -> true
     }
 }