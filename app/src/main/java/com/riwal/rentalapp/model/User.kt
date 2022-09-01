package com.riwal.rentalapp.model

import android.content.SharedPreferences
import com.riwal.rentalapp.common.extensions.android.get
import com.riwal.rentalapp.common.extensions.android.set

data class User(
        val name: String,
        val phoneNumber: String?,
        val email: String,
        var customers: List<Customer> = emptyList()
) {
    var currentCustomer = customers.firstOrNull()
}

var SharedPreferences.user: User?
    get() = this["user"]
    set(value) {
        this["user"] = value
    }

fun User.asContact() = Contact(name = name, phoneNumber = phoneNumber, email = email, company = currentCustomer!!.name)
