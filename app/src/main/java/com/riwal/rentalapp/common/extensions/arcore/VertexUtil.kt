package com.riwal.rentalapp.common.extensions.arcore

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Vertex

fun Vertex(position: Vector3, normal: Vector3, textureCoordinate: Vertex.UvCoordinate) = Vertex.builder()
        .setPosition(position)
        .setNormal(normal)
        .setUvCoordinate(textureCoordinate)
        .build()