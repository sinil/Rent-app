package com.riwal.rentalapp.contactinfo

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.ContactInfo

interface ContactInfoView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun contactInfoList(view: ContactInfoView): List<ContactInfo>
    }

    interface Delegate {
        fun onNavigateBackSelected()
    }

}