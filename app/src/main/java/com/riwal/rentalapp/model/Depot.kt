package com.riwal.rentalapp.model

data class Depot(val name: String,
                 val address: String,
                 val coordinate: Coordinate? = null,
                 val phoneNumber: String?,
                 val email: String?)