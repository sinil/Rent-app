package com.riwal.rentalapp.model.api


data class TrainingResponse(val CountryId: String,
                            val ServiceInfo: String,
                            val Categories: List<TrainingCategory>)
