package com.riwal.rentalapp.model

import com.riwal.rentalapp.common.extensions.core.isEmail

data class Contact(
        var id: String? = "",
        var name: String? = "",
        var phoneNumber: String? = "",
        var email: String? = "",
        var company: String? = "") {

    val isValid
        get() = listOf(name, phoneNumber, company).none { it.isNullOrBlank() } && email!!.isEmail

}
