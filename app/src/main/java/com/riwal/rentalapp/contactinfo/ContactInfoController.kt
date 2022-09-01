package com.riwal.rentalapp.contactinfo

import com.riwal.rentalapp.model.ContactInfo
import com.riwal.rentalapp.model.CountryManager

class ContactInfoController(val view: ContactInfoView, val countryManager: CountryManager) : ContactInfoView.DataSource, ContactInfoView.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    val contactInfoList: List<ContactInfo>
        get() = countryManager.activeCountry!!.contactInfo


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*---------------------------------- ContactView.DataSource ----------------------------------*/


    override fun contactInfoList(view: ContactInfoView) = contactInfoList


    /*----------------------------------- ContactView.Delegate -----------------------------------*/


    override fun onNavigateBackSelected() {
        view.navigateBack()
    }
}