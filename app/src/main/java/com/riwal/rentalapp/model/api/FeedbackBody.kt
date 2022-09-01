package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country

data class FeedbackBody(val rating: String, val message: String?, val email: String?, val countryCode: Country)