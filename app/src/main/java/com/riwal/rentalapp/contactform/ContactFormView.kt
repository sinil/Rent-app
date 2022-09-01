package com.riwal.rentalapp.contactform

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Contact

interface ContactFormView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun navigateToProject()

    interface DataSource {
        fun contact(view: ContactFormView): Contact?
        fun isValidEmail(view: ContactFormView): Boolean
        fun canContinue(view: ContactFormView): Boolean
    }

    interface Delegate {
        fun onBackButtonClicked()
        fun onCompanyInputChanged(text: String)
        fun onNameInputChanged(text: String)
        fun onPhoneInputChanged(text: String)
        fun onEmailInputChanged(text: String)
        fun onContinueButtonClicked()
    }

}