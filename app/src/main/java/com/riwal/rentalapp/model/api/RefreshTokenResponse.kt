package com.riwal.rentalapp.model.api

data class RefreshTokenResponse(val access_token: String, val token_type: String) {

    val token
        get() = JsonWebToken(string = access_token)!!

}