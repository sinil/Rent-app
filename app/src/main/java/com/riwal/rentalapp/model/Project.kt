package com.riwal.rentalapp.model

import com.riwal.rentalapp.common.extensions.core.random

data class Project(
        var name: String? = null,
        var address: String? = null,
        var street: String? = null,
        var coordinate: Coordinate? = null,
        var contactName: String? = null,
        var contactPhoneNumber: String? = null,
        val id: Int = Int.random()
)