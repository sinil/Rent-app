package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.Country

data class TrainingFireBaseRequestBody(
        val comment: String,
        val training: Training,
        val customer: TrainingCustomer,
        val countryCode: Country?) {

    constructor(): this(comment= "", training = Training(), customer = TrainingCustomer(), countryCode = null )

}


data class Training(
        val courseId: String?,
        val depotId: String?,
        val trainingName: String?,
        val trainingCategory: String?,
        val location: String?,
        val startDate: String?,
        val participantes: String?,
        var depotEmail: String?
){
    constructor(): this(courseId = "", depotId = "", trainingName = "", trainingCategory = "",
            location = "", startDate = "", participantes = "", depotEmail = "")
}

data class TrainingCustomer(
        val name: String,
        val phoneNumber: String,
        val email: String,
        val company: String
){
    constructor(): this(name = "", phoneNumber = "", email = "", company = "")
}
