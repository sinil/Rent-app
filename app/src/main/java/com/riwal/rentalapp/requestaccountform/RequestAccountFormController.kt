package com.riwal.rentalapp.requestaccountform

import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver

class RequestAccountFormController(val view: RequestAccountFormView) : ViewLifecycleObserver, RequestAccountFormView.DataSource, RequestAccountFormView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null

    private var account = Account()

    private val canSubmit
        get() = account.isValid

    private val isValidEmail
        get() = account.email != null && account.email!!.isNotBlank() && account.email!!.isEmail


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this
    }


    /*------------------------------ RequestAccountFormView Data Source ------------------------------*/


    override fun account(view: RequestAccountFormView) = account
    override fun isValidEmail(view: RequestAccountFormView) = isValidEmail
    override fun canSubmit(view: RequestAccountFormView) = canSubmit


    /*------------------------------- RequestAccountFormView Delegate --------------------------------*/


    override fun onNavigateBackSelected(view: RequestAccountFormView) {
        view.navigateBack()
    }

    override fun onAccountChanged(view: RequestAccountFormView, account: Account) {
        this.account = account
        view.notifyDataChanged()
    }

    override fun onSubmitSelected(view: RequestAccountFormView) {
        delegate?.onAccountRequested(controller = this, account = account)
        view.navigateBack()
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun onAccountRequested(controller: RequestAccountFormController, account: Account)
    }

}