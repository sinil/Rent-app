package com.riwal.rentalapp.model.api

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor : Interceptor {

    var userName: String? = null
    var password: String? = null

    fun setCredentials(userName: String, password: String) {
        this.userName = userName
        this.password = password
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val authenticatedRequest = chain
                .request()
                .newBuilder()
                .header("Authorization", Credentials.basic(userName ?: "", password ?: ""))
                .build()
        return chain.proceed(authenticatedRequest)
    }

}