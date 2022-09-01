package com.riwal.rentalapp.common.arcore

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Material
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.RenderableDefinition
import com.google.ar.sceneform.rendering.Vertex.UvCoordinate
import com.riwal.rentalapp.common.extensions.arcore.Vertex
import com.riwal.rentalapp.common.extensions.arcore.to
import kotlinx.coroutines.future.await
import java.util.concurrent.ExecutionException

@RequiresApi(Build.VERSION_CODES.N)
class ShapeFactory {

    fun makeCube(size: Vector3, material: Material) = com.google.ar.sceneform.rendering.ShapeFactory.makeCube(size, Vector3.zero(), material)
    fun makeSphere(radius: Float, material: Material) = com.google.ar.sceneform.rendering.ShapeFactory.makeSphere(radius, Vector3.zero(), material)
    fun makeCylinder(radius: Float, height: Float, material: Material) = com.google.ar.sceneform.rendering.ShapeFactory.makeCylinder(radius, height, Vector3.zero(), material)

    suspend fun makePlane(width: Float, height: Float, material: Material): ModelRenderable {
        val topLeft = Vector3(-width / 2, -height / 2, 0f)
        val topRight = Vector3(width / 2, -height / 2, 0f)
        val bottomLeft = Vector3(-width / 2, height / 2, 0f)
        val bottomRight = Vector3(width / 2, height / 2, 0f)

        return makeQuad(topLeft, topRight, bottomLeft, bottomRight, material)
    }

    suspend fun makeFloor(material: Material): ModelRenderable {

        val topLeft = Vector3(-1f, 0f, -1f).scaled(1000f)
        val topRight = Vector3(1f, 0f, -1f).scaled(1000f)
        val bottomLeft = Vector3(-1f, 0f, 1f).scaled(1000f)
        val bottomRight = Vector3(1f, 0f, 1f).scaled(1000f)

        return makeQuad(topLeft, topRight, bottomLeft, bottomRight, material = material)
    }

    suspend fun makeQuad(topLeft: Vector3, topRight: Vector3, bottomLeft: Vector3, bottomRight: Vector3, material: Material): ModelRenderable {

        val normal = Vector3.cross(topLeft.to(topRight), topLeft.to(bottomLeft)).normalized()

        val uv00 = UvCoordinate(0f, 0f)
        val uv10 = UvCoordinate(1f, 0f)
        val uv01 = UvCoordinate(0f, 1f)
        val uv11 = UvCoordinate(1f, 1f)

        val topLeftVertex = Vertex(position = topLeft, normal = normal, textureCoordinate = uv00)
        val topRightVertex = Vertex(position = topRight, normal = normal, textureCoordinate = uv10)
        val bottomLeftVertex = Vertex(position = bottomLeft, normal = normal, textureCoordinate = uv01)
        val bottomRightVertex = Vertex(position = bottomRight, normal = normal, textureCoordinate = uv11)

        val vertices = listOf(
                topLeftVertex,
                topRightVertex,
                bottomLeftVertex,
                bottomRightVertex
        )

        val triangles = listOf(
                topLeftVertex, bottomLeftVertex, topRightVertex,
                topRightVertex, bottomLeftVertex, bottomRightVertex
        )

        val indices = triangles.map { vertices.indexOf(it) }

        val submesh = RenderableDefinition.Submesh.builder()
                .setTriangleIndices(indices)
                .setMaterial(material)
                .build()

        val renderableDefinition = RenderableDefinition.builder()
                .setVertices(vertices)
                .setSubmeshes(listOf(submesh))
                .build()

        try {
            return ModelRenderable.builder()
                    .setSource(renderableDefinition)
                    .build()
                    .await() ?: throw AssertionError("Error creating renderable.")
        } catch (exception: InterruptedException) {
            throw AssertionError("Error creating renderable.", exception)
        } catch (exception: ExecutionException) {
            throw AssertionError("Error creating renderable.", exception)
        }

    }

}