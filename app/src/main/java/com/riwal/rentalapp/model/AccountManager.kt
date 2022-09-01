package com.riwal.rentalapp.model

data class AccountManager(val name: String, val phoneNumber: String, val email: String, val rentalDeskEmail: String?) {

    val isUseful
        get() = name.isNotBlank()

}