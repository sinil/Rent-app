package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.model.Country

data class TrainingRequestBody(
        var courseId: String,
        var depotId: String,
        var startDate: String = "",
        var custCompanyName: String = "",
        var custContactName: String = "",
        var custContactPhone: String = "",
        var custContactEmail: String = "",
        var comments: String?,
        var participantsCount: String = "",
        val countryCode: Country?) {

    constructor() : this("","","", "", "", "", "", "", "", null)

    val canSendRequest
        get() = listOf(depotId,startDate, participantsCount, custCompanyName, custContactName, custContactPhone, custContactEmail).none { it.isNullOrBlank() } && custContactEmail.isEmail

}
