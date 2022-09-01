package com.riwal.rentalapp.model.api

import okhttp3.Interceptor

class RentalAppApiAuthorizationInterceptor : Interceptor {

    var firebaseIdToken: String? = null
    var access4UToken: String? = null

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {

        val authenticatedRequest = chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer $firebaseIdToken")
        if (access4UToken != null) {
            authenticatedRequest.header("Access4U-Token", access4UToken!!)
        }

        return chain.proceed(authenticatedRequest.build())
    }
}