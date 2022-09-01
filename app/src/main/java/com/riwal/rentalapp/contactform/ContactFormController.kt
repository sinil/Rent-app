package com.riwal.rentalapp.contactform

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.OrderManager

class ContactFormController(val view: ContactFormView, val orderManager: OrderManager, val analytics: RentalAnalytics) : ViewLifecycleObserver, ContactFormView.DataSource, ContactFormView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var contact
        get() = orderManager.currentOrder.contact ?: Contact()
        set(value) {
            orderManager.currentOrder.contact = value
            orderManager.save()
        }

    private val isValidEmail: Boolean
        get() = contact.email!!.isEmpty() || contact.email!!.isEmail


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.userLookingAtOrderContactForm()
    }


    /*---------------------------------- ContactView DataSource ----------------------------------*/


    override fun contact(view: ContactFormView) = contact
    override fun isValidEmail(view: ContactFormView) = isValidEmail
    override fun canContinue(view: ContactFormView) = contact.isValid


    /*----------------------------------- ContactView Delegate -----------------------------------*/


    override fun onBackButtonClicked() {
        view.navigateBack()
    }

    override fun onCompanyInputChanged(text: String) {
        contact = contact.copy(company = text)
        view.notifyDataChanged()
    }

    override fun onNameInputChanged(text: String) {
        contact = contact.copy(name = text)
        view.notifyDataChanged()
    }

    override fun onPhoneInputChanged(text: String) {
        contact = contact.copy(phoneNumber = text)
        view.notifyDataChanged()
    }

    override fun onEmailInputChanged(text: String) {
        contact = contact.copy(email = text)
        view.notifyDataChanged()
    }

    override fun onContinueButtonClicked() {
        view.navigateToProject()
    }
}