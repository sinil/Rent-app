package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.AppInfo
import com.riwal.rentalapp.model.SemanticVersion

data class AppInfoResponse(
        val currentVersion: String,
        val minimumVersion: String,
        val featureVersion: Int,
        val informationMessage: String,
        val manliftAppLink: String,
        val riwalAppLink: String,
        val liveCountries: List<String>
)

fun AppInfoResponse.toAppInfo() = AppInfo(
        currentVersion = SemanticVersion(currentVersion),
        minimumVersion = SemanticVersion(minimumVersion),
        featureVersion = featureVersion,
        informationMessage = informationMessage,
        manliftAppLink = manliftAppLink,
        riwalAppLink = riwalAppLink,
        liveCountries = liveCountries
)