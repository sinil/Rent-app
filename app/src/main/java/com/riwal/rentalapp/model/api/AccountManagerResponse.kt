package com.riwal.rentalapp.model.api

import com.riwal.rentalapp.model.AccountManager

data class AccountManagerResponse(val AccMgrName: String, val Email: String, val CellularPhone: String, val OnRentEmailAddress: String?) {

    fun toAccountManager() = AccountManager(
            name = AccMgrName,
            phoneNumber = CellularPhone,
            email = Email,
            rentalDeskEmail = OnRentEmailAddress
    )

}