package com.riwal.rentalapp.main.account

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.forgotpassword.ForgotPasswordController
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.requestaccountform.Account
import com.riwal.rentalapp.requestaccountform.RequestAccountFormController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AccountController(
        val view: AccountView,
        val sessionManager: SessionManager,
        val backendClient: BackendClient,
        val rentalManager: RentalManager,
        val countryManager: CountryManager,
        val analytics: RentalAnalytics
) : ViewLifecycleObserver, AccountView.DataSource, AccountView.Delegate, LoginView.DataSource, LoginView.Delegate, RequestAccountFormController.Delegate, ForgotPasswordController.Delegate, CountryManager.Observer, SessionManager.Observer, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    private var email = ""
    private var password = ""

    private var isLoggingIn = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private var accountManagersAndPictures: Map<Customer, AccountManagerAndPicture> = emptyMap()

    private val user
        get() = sessionManager.user

    private val currentCustomer
        get() = user?.currentCustomer

    private val customers
        get() = user?.customers ?: emptyList()

    private val isLoggedIn
        get() = sessionManager.isLoggedIn

    private val isEmailFilledOutAndValid
        get() = email.isNotBlank() && email.isEmail

    private val isPasswordFilledOut
        get() = password.isNotBlank()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        view.loginView.dataSource = this
        view.loginView.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()

        sessionManager.addObserver(this)
        countryManager.addObserver(this)
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.userLookingAtAccountPage()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        countryManager.removeObserver(this)
        sessionManager.removeObserver(this)
        cancel()
    }


    /*--------------------------------- Account View Data Source ---------------------------------*/


    override fun isLoggedIn(view: AccountView): Boolean {
        if (isLoggedIn)
            analytics.setUser(user)
        return isLoggedIn
    }

    override fun currentCustomer(view: AccountView) = currentCustomer
    override fun customers(view: AccountView) = customers
    override fun isQRScannerVisible(view: AccountView) = !countryManager.activeCountry?.name.equals("NL")
    override fun isAddAccessoriesEnabled(view: AccountView) = countryManager.activeCountry?.isAddAccessoriesEnabled!!
    override fun accountManagerAndPicture(view: AccountView, customer: Customer): AccountManagerAndPicture {
        updateAccountManagerIfNeeded(customer)
        return accountManagersAndPictures.getValue(customer)
    }
    override fun isMyProjectsVisible(view: AccountView) = countryManager.activeCountry?.isMyProjectsEnabled!!
    override fun isMyInvoicesVisible(view: AccountView): Boolean = !countryManager.activeCountry?.name.equals("PL")
    override fun isMyQuotationsVisible(view: AccountView) = countryManager.activeCountry?.isMyQuotationEnabled!!


/*---------------------------------- Account View Delegate -----------------------------------*/


    override fun onCustomerSelected(view: AccountView, customer: Customer) {
        analytics.setUser(user)
        sessionManager.setCurrentCustomer(customer)
        view.notifyDataChanged()
    }

    override fun onMyRentalsSelected(view: AccountView) {
        view.navigateToMyRentalsPage()
    }

    override fun onAccessoriesOnRentSelected(view: AccountView) {
        view.navigateToAcessoriesOnRent()
    }

    override fun onScanMachineSelected(view: AccountView) {
        view.navigateToScanMachinePage()
    }

    override fun onLogOutSelected(view: AccountView) {
        view.showLogoutConfirmation {
            logout()
        }
    }

    override fun onMyProjectsSelected(view: AccountView) {
        view.navigateToMyProjects()
    }

    override fun onMyQuotationSelected(view: AccountView) {
        view.navigateToMyQuotation()
    }

    override fun onMyInvoicesSelected(view: AccountView) {
        view.navigateToMyInvoices()
    }

/*---------------------------------- Login View Data Source ----------------------------------*/


    override fun email(view: LoginView) = email
    override fun password(view: LoginView) = password
    override fun isEmailValid(view: LoginView) = isEmailFilledOutAndValid
    override fun isPasswordValid(view: LoginView) = isPasswordFilledOut
    override fun isLoggingIn(view: LoginView) = isLoggingIn


/*----------------------------------- Login View Delegate ------------------------------------*/


    override fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    override fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    override fun onLoginSelected(view: LoginView) {
        if (isEmailFilledOutAndValid && isPasswordFilledOut) {
            login()
        } else {
            view.notifyDataChanged()
        }
    }

    override fun onRequestAccountSelected() {
        view.loginView.navigateToRequestAccountPage { controller ->
            controller as RequestAccountFormController
            controller.delegate = this
        }
    }

    override fun onForgotPasswordSelected() {
        view.loginView.navigateToForgotPasswordPage { controller ->
            controller as ForgotPasswordController
            controller.delegate = this
        }
    }

/*---------------------------------- CountryManager Observer ---------------------------------*/


    override fun onCountryChanged(countryManager: CountryManager, country: Country) {
        launch {
            view.notifyCountryChanged()
        }
    }

/*---------------------------------- CountryManager Observer ---------------------------------*/


    override fun onCustomersUpdated(sessionManager: SessionManager, customers: List<Customer>?) {
        launch {
            view.notifyCustomersChanged(customers)
        }
    }

/*-------------------------- RequestAccountFormController Delegate ---------------------------*/


    override fun onAccountRequested(controller: RequestAccountFormController, account: Account) {
        launch {
            rentalManager.requestAccount(account, countryManager.activeCountry!!)
        }
    }

/*--------------------------   ForgotPasswordController Delegate   ---------------------------*/


    override fun onResetPasswordSuccessfullyFinished() {
        view.loginView.notifyResetPasswordSucceed()
    }

/*------------------------------------- Private methods --------------------------------------*/


    private fun login() = launch {
        isLoggingIn = true
        try {
            val user = sessionManager.login(email, password)
            password = ""
            analytics.setUser(user)
        } catch (error: Exception) {
            view.loginView.notifyLoginFailed(error)
        }
        isLoggingIn = false
    }

    private fun logout() {
        sessionManager.logout()
        accountManagersAndPictures = emptyMap()
        view.notifyDataChanged()
    }

    private fun updateAccountManagerIfNeeded(customer: Customer) {

        if (accountManagersAndPictures.containsKey(customer)) {
            return
        }

        val accountManagerAndPicture = AccountManagerAndPicture()
        accountManagersAndPictures = accountManagersAndPictures + (customer to accountManagerAndPicture)

        updateAccountManager(customer)
    }

    private fun updateAccountManager(customer: Customer) = launch {
        val accountManagerAndPicture = accountManagersAndPictures.getValue(customer)
        with(accountManagerAndPicture) {
            isUpdating = true
            view.notifyDataChanged()
            try {
                accountManager = backendClient.getAccountManager(customer)
                picture = backendClient.loadImageFromAccess4U(customer.accountManagerPhotoUrl)
            } catch (error: Exception) {
                error.printStackTrace()
            }
            isUpdating = false
            view.notifyDataChanged()
        }
    }

}