package com.riwal.rentalapp.requestaccountform

import com.riwal.rentalapp.common.extensions.core.isEmail

data class Account(val name: String? = null, val company: String? = null, val email: String? = null, val phoneNumber: String? = null,
                   val vatNumber: String? = null) {

    val isValid
        get() = !name.isNullOrEmpty() &&
                !company.isNullOrBlank() &&
                !email.isNullOrBlank() && email.isEmail &&
                !phoneNumber.isNullOrBlank()

}