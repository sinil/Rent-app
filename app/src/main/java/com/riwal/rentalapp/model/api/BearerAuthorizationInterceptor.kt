package com.riwal.rentalapp.model.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class BearerAuthorizationInterceptor : Interceptor {

    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {

        val authenticatedRequest = chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()

        try {
            return chain.proceed(authenticatedRequest)
        } catch (exception: IOException) {
            throw IOException(chain.request().url().toString(), exception)
        }
    }

}