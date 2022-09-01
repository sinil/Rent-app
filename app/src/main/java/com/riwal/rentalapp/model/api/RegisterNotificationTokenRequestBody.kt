package com.riwal.rentalapp.model.api

data class RegisterNotificationTokenRequestBody(
        val email: String,
        val token: String
)