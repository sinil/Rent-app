package com.riwal.rentalapp.main.account

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Customer

interface AccountView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    val loginView: LoginView

    fun notifyCustomersChanged(customers: List<Customer>?)
    fun notifyCountryChanged()
    fun navigateToMyRentalsPage()
    fun navigateToScanMachinePage()
    fun navigateToAcessoriesOnRent()
    fun navigateToMyProjects()
    fun navigateToMyQuotation()
    fun navigateToMyInvoices()
    fun showLogoutConfirmation(callback: () -> Unit)

    interface DataSource {
        fun isLoggedIn(view: AccountView): Boolean
        fun isQRScannerVisible(view: AccountView): Boolean
        fun isAddAccessoriesEnabled(view: AccountView): Boolean
        fun currentCustomer(view: AccountView): Customer?
        fun customers(view: AccountView): List<Customer>
        fun accountManagerAndPicture(view: AccountView, customer: Customer): AccountManagerAndPicture
        fun isMyProjectsVisible(view: AccountView): Boolean
        fun isMyInvoicesVisible(view: AccountView): Boolean
        fun isMyQuotationsVisible(view: AccountView): Boolean
    }

    interface Delegate {
        fun onCustomerSelected(view: AccountView, customer: Customer)
        fun onMyRentalsSelected(view: AccountView)
        fun onAccessoriesOnRentSelected(view: AccountView)
        fun onMyProjectsSelected(view: AccountView)
        fun onMyQuotationSelected(view: AccountView)
        fun onMyInvoicesSelected(view: AccountView)
        fun onScanMachineSelected(view: AccountView)
        fun onLogOutSelected(view: AccountView)
    }

}