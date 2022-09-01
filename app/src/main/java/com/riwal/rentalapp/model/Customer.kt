package com.riwal.rentalapp.model

import com.riwal.rentalapp.BuildConfig

data class Customer(val id: String, val name: String, val companyId: String, val databaseName: String, val canReportBreakdown: Boolean, val newId: String)

val Customer.accountManagerPhotoUrl
    get() = "${BuildConfig.ACCESS4U_URL}/AccountManagerPicture/?databaseName=$databaseName&customerId=$id&companyId=$companyId"