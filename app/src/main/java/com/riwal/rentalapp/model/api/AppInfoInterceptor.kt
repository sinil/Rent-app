package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.BuildConfig
import com.riwal.rentalapp.common.extensions.json.toJson
import okhttp3.Interceptor
import okhttp3.Response

class AppInfoInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val appInfo = mapOf(
                "version" to BuildConfig.VERSION_NAME,
                "platform" to "Android"
        )

        val authenticatedRequest = chain
                .request()
                .newBuilder()
                .header("Rental-App-Info", appInfo.toJson())
                .build()

        return chain.proceed(authenticatedRequest)
    }

}