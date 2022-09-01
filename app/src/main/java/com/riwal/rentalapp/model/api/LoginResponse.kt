package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.json.fromJson

data class LoginResponse(
        private val access_token: String,
        val token_type: String,
        private val refresh_token: String
) {

    val accessToken
        get() = JsonWebToken(string = access_token)!!

    val refreshToken
        get() = refresh_token

    val firstName
        get() = payload.firstName

    val lastName
        get() = payload.lastName

    val email
        get() = payload.email

    val phoneNumber
        get() = payload.phoneNumber

    private val payload: LoginPayload
        get() = fromJson(accessToken.payloadString)!!

    data class LoginPayload(val email: String, val firstName: String, val lastName: String, val phoneNumber: String?)
}