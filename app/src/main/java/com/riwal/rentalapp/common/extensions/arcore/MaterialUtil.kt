package com.riwal.rentalapp.common.extensions.arcore

import android.content.Context
import android.os.Build
import android.renderscript.Float4
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.rendering.Color
import com.google.ar.sceneform.rendering.Material
import kotlinx.coroutines.future.await

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setFloat4(name: String, value: Float4) = setFloat4(name, value.x, value.y, value.z, value.w)

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setBaseColor(@ColorInt value: Int) {
    setFloat4("baseColor", Color(value))
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setBaseColor(r: Float, g: Float, b: Float, a: Float) {
    setFloat4("baseColor", Color(r, g, b, a))
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setEmissive(value: Float4) {
    setFloat4("emissive", value.x, value.y, value.z, value.w)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setRoughness(value: Float) {
    setFloat("roughness", value)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setMetallic(value: Float) {
    setFloat("metallic", value)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setReflectance(value: Float) {
    setFloat("reflectance", value)
}

@RequiresApi(Build.VERSION_CODES.N)
fun Material.setAmbientOcclusion(value: Float) {
    setFloat("ambientOcclusion", value)
}

@RequiresApi(Build.VERSION_CODES.N)
suspend fun Context.getMaterial(materialRes: Int) = Material.builder()
        .setSource(this, materialRes)
        .build()
        .await()