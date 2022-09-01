package com.riwal.rentalapp.requestaccountform

import com.riwal.rentalapp.common.mvc.MvcView

interface RequestAccountFormView : MvcView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun account(view: RequestAccountFormView): Account
        fun isValidEmail(view: RequestAccountFormView): Boolean
        fun canSubmit(view: RequestAccountFormView): Boolean
    }

    interface Delegate {
        fun onNavigateBackSelected(view: RequestAccountFormView)
        fun onAccountChanged(view: RequestAccountFormView, account: Account)
        fun onSubmitSelected(view: RequestAccountFormView)
    }

}