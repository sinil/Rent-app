package com.riwal.rentalapp.machinedetail.ar

import android.content.Context
import android.net.Uri
import android.os.Build
import android.renderscript.Float4
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode
import com.google.ar.sceneform.assets.RenderableSource.RecenterMode.NONE
import com.google.ar.sceneform.assets.RenderableSource.SourceType.GLB
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.firebase.storage.FirebaseStorage
import com.riwal.rentalapp.common.arcore.MaterialFactory
import com.riwal.rentalapp.common.arcore.MaterialFactory.ShadingAndBlendingMode.LIT_OPAQUE
import com.riwal.rentalapp.common.extensions.arcore.*
import com.riwal.rentalapp.common.extensions.core.valueForProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.N)
class FirestoreArModelLoader(
        val context: Context,
        private val firebaseStorage: FirebaseStorage,
        private val materialFactory: MaterialFactory
) {


    /*---------------------------------------- Properties ----------------------------------------*/


    var maximumNumberOfCachedModels = 3
    var delegate: Delegate? = null

    private val modelCacheDirectory
        get() = File(context.cacheDir, "ARModelCache")


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        if (!modelCacheDirectory.exists()) {
            modelCacheDirectory.mkdir()
        }
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    suspend fun loadArModel(url: String, scale: Float = 1.0f, recenterMode: RecenterMode = NONE) = withContext(Dispatchers.IO) {

        try {

            val modelCacheFile = modelCacheFileForModelAtStorageUrl(url)
            val hasModelInCache = modelCacheFile.exists()

            if (!hasModelInCache) {
                removeOldestCachedModelsIfNeeded()
                downloadFileFromFirebaseStorage(url, modelCacheFile) { progress ->
                    val correctedProgress = progress * 0.98
                    notifyProgressUpdate(url, progress = correctedProgress)
                }
            }

            notifyProgressUpdate(url, progress = 0.99)

            val renderable = loadArModelFromCache(modelCacheFile, scale, recenterMode)!!
            enableDoubleSidedRendering(renderable)

            notifyProgressUpdate(url, progress = 1.0)

            renderable

        } catch (error: Exception) {
            notifyProgressUpdate(url, progress = 1.0)
            throw error
        }

    }

    fun clearCache() = modelCacheDirectory.deleteRecursively()


    /*------------------------------------- Private methods --------------------------------------*/


    private suspend fun loadArModelFromCache(modelCacheFile: File, scale: Float, recenterMode: RecenterMode) = withContext(Dispatchers.Main) {
        ModelRenderable.builder()
                .setSource(context, RenderableSource.builder()
                        .setSource(context, Uri.parse(modelCacheFile.absolutePath), GLB)
                        .setRecenterMode(recenterMode)
                        .setScale(scale)
                        .build())
                .setRegistryId(modelCacheFile.absolutePath)
                .build()
                .await()
    }

    private suspend fun downloadFileFromFirebaseStorage(url: String, target: File, onProgressListener: (Double) -> Unit) {
        val arModelFileReference = firebaseStorage.getReferenceFromUrl(url)
        val tempFile = createTempFile(prefix = target.name)

        arModelFileReference
                .getFile(tempFile)
                .addOnProgressListener {
                    val progress = it.bytesTransferred.toDouble() / it.totalByteCount.toDouble()
                    onProgressListener(progress)
                }
                .await()

        tempFile.copyTo(target, overwrite = true)
        tempFile.delete()
    }

    private fun notifyProgressUpdate(url: String, progress: Double) {
        delegate?.onLoadingProgressUpdate(loader = this, url = url, progress = progress)
    }

    private fun removeOldestCachedModelsIfNeeded() {

        val cachedModelFiles = modelCacheDirectory
                .listFiles()
                .sortedBy { it.lastModified() }

        val numberOfModelsToDelete = max(0, cachedModelFiles.size - maximumNumberOfCachedModels)

        cachedModelFiles
                .take(numberOfModelsToDelete)
                .forEach { it.delete() }
    }

    private fun modelCacheFileForModelAtStorageUrl(url: String): File {
        val file = File(url)
        return File(modelCacheDirectory, file.name)
    }

    // FIXME: The default GLTF material doesn't support double sided rendering
    // So we replace it with our own custom material to enable double sided rendering
    // Because the parameters of the material (e.g. color) aren't accessible we use reflection to get these.
    // We need to replace this ASAP because when Google changes the names or types or paths in the SDK, this code will crash at runtime!
    private suspend fun enableDoubleSidedRendering(renderable: Renderable) {

        val litMaterial = withContext(Dispatchers.Main) {
            materialFactory.makeMaterial(LIT_OPAQUE)
        }

        for (submeshIndex in 0 until renderable.submeshCount) {

            val originalMaterial = renderable.materials[submeshIndex]

            val baseColorFactor = valueForFloat4Property(originalMaterial, path = "materialParameters/namedParameters/baseColorFactor") ?: throw MissingMaterialPropertyException("baseColorFactor")
            val metallicFactorX = valueForProperty(originalMaterial, path = "materialParameters/namedParameters/metallicFactor/x") as? Float ?: throw MissingMaterialPropertyException("metallicFactor")
            val emissiveFactor = valueForFloat4Property(originalMaterial, path = "materialParameters/namedParameters/emissiveFactor") ?: throw MissingMaterialPropertyException("emissiveFactor")

            val litMaterialCopy = litMaterial.makeCopy().apply {
                setBaseColor(r = baseColorFactor.x, g = baseColorFactor.y, b = baseColorFactor.z, a = baseColorFactor.w)
                setMetallic(metallicFactorX)
                setEmissive(emissiveFactor)
                setAmbientOcclusion(0.8f)
            }

            renderable.setMaterial(submeshIndex, litMaterialCopy)
        }
    }

    private fun valueForFloat4Property(rootObject: Any, path: String): Float4? {
        return Float4(
                valueForProperty(rootObject, path = "$path/x") as? Float ?: return null,
                valueForProperty(rootObject, path = "$path/y") as? Float ?: return null,
                valueForProperty(rootObject, path = "$path/z") as? Float ?: return null,
                valueForProperty(rootObject, path = "$path/w") as? Float ?: return null
        )
    }


    /*----------------------------------- Interfaces/classes -------------------------------------*/


    class MissingMaterialPropertyException(message: String) : Exception(message)

    interface Delegate {
        fun onLoadingProgressUpdate(loader: FirestoreArModelLoader, url: String, progress: Double)
    }

}