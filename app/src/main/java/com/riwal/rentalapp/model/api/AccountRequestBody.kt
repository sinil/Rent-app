package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country

data class AccountRequestBody(val name: String, val company: String, val email: String, val phoneNumber: String, val countryCode: Country, val vatNumber: String?)