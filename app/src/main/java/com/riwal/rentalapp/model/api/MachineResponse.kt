package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.core.enum
import com.riwal.rentalapp.model.*

data class MachineResponse(
        val id: String?,
        val rentalType: String?,
        val brand: String?,
        val model: String?,
        val type: String?,
        val weight: Float?,
        val width: Float?,
        val length: Float?,
        val height: Float?,
        val platformLength: Float?,
        val platformWidth: Float?,
        val liftCapacity: Float?,
        val propulsion: String?,
        val workingHeight: Float?,
        val workingOutreach: Float?,
        val sites: List<String>?,
        val images: List<String>?,
        val thumbnail: String?,
        val reachDiagram: String?,
        val meshes: List<MachineMeshDescriptorResponse>?,
        val documents: List<MachineDocumentResponse>?

) {

    fun toMachine(): Machine? {

        val workingHeight = workingHeight ?: 0f
        val workingOutreach = workingOutreach ?: 0f
        val liftCapacity = liftCapacity ?: 0f
        if (workingOutreach < 0f || workingHeight <= 0f || liftCapacity <= 0f) {
            return null
        }

        return Machine(
                id = id ?: return null,
                rentalType = rentalType ?: return null,
                brand = brand ?: return null,
                model = model ?: return null,
                type = type?.enum<Machine.Type>() ?: return null,
                weight = weight ?: 0f,
                width = width ?: 0f,
                length = length ?: 0f,
                height = height ?: 0f,
                platformLength = platformLength ?: 0f,
                platformWidth = platformWidth ?: 0f,
                liftCapacity = liftCapacity,
                propulsion = propulsion?.enum<Machine.Propulsion>() ?: return null,
                workingHeight = workingHeight,
                workingOutreach = workingOutreach,
                countries = sites?.mapNotNull { it.enum<Country>() } ?: return null,
                images = images ?: emptyList(),
                thumbnailUrl = thumbnail,
                diagram = reachDiagram,
                documents = documents?.mapNotNull { it.toDocument() } ?: emptyList(),
                meshes = (meshes ?: emptyList())
                        .mapNotNull { it.toMachineMeshDescriptor() }
        )
    }

}

data class MachineMeshDescriptorResponse(
        val company: String?,
        val position: String?,
        val url: String?
) {

    fun toMachineMeshDescriptor(): MachineMeshDescriptor? {
        return MachineMeshDescriptor(
                company = company?.enum<Company>() ?: return null,
                position = position?.enum<MachineMeshPosition>() ?: return null,
                url = url ?: return null
        )
    }

}


data class MachineDocumentResponse(
        val url: String,
        val documentType: String,
        val language: String
) {
    fun toDocument(): Document? {
        return Document(
                url = url,
                type = documentType.enum<DocumentType>() ?: return null,
                language = language
        )
    }
}

