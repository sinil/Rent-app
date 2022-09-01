package com.riwal.rentalapp.model

data class Document(
        val url: String,
        val type: DocumentType,
        val language: String)



enum class DocumentType {
    MANUAL,
    SPECIFICATION
}