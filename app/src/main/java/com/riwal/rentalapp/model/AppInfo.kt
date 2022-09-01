package com.riwal.rentalapp.model


data class AppInfo(
    val minimumVersion: SemanticVersion,
    val currentVersion: SemanticVersion,
    val featureVersion: Int,
    val informationMessage: String,
    val manliftAppLink: String,
    val riwalAppLink: String,
    val liveCountries: List<String>
)
