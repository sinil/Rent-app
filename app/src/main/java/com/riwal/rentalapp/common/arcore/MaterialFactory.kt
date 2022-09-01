package com.riwal.rentalapp.common.arcore

import android.content.Context
import android.graphics.Color.WHITE
import android.os.Build
import android.renderscript.Float4
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.ModelRenderable
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.arcore.MaterialFactory.ShadingAndBlendingMode.LIT_OPAQUE
import com.riwal.rentalapp.common.arcore.MaterialFactory.ShadingAndBlendingMode.UNLIT_TRANSPARENT
import com.riwal.rentalapp.common.extensions.arcore.*
import kotlinx.coroutines.future.await


@RequiresApi(Build.VERSION_CODES.N)
class MaterialFactory(val context: Context) {


    /*--------------------------------------- Properties -----------------------------------------*/


    private val materials: MutableMap<ShadingAndBlendingMode, Material> = mutableMapOf()


    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun makeMaterial(shadingModel: ShadingAndBlendingMode): Material {

        loadMaterialIfNeeded(shadingModel)
        val material = materials[shadingModel]!!.makeCopy()
        setDefaultValues(material)
        return material
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun setDefaultValues(material: Material) = with(material) {
        setBaseColor(WHITE)
        setEmissive(Float4(0f, 0f, 0f, 0f))
        setRoughness(1.0f)
        setMetallic(0.0f)
        setReflectance(0.5f)
        setAmbientOcclusion(0.0f)
    }

    private suspend fun loadMaterialIfNeeded(shadingModel: ShadingAndBlendingMode) {
        if (materials[shadingModel] == null) {
            materials[shadingModel] = loadMaterial(shadingModel)
        }
    }

    private suspend fun loadMaterial(shadingModel: ShadingAndBlendingMode) = ModelRenderable.builder()
            .setSource(context, when (shadingModel) {
                UNLIT_TRANSPARENT -> R.raw.dummy_unlit_transparent
                LIT_OPAQUE -> R.raw.dummy_lit_opaque
            })
            .build()
            .await()
            .material


    /*------------------------------------------ Enums -------------------------------------------*/


    enum class ShadingAndBlendingMode {
        UNLIT_TRANSPARENT,
        LIT_OPAQUE
    }

}
