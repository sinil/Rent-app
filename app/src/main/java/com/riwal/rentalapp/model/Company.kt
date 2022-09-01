package com.riwal.rentalapp.model

enum class Company {
    RIWAL,
    MANLIFT
}

fun String.toCompany() = Company.valueOf(value = this)
