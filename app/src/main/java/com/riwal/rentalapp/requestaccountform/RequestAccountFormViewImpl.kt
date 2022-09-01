package com.riwal.rentalapp.requestaccountform

import android.view.Menu
import android.view.MenuItem
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.items
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.requestaccountform.RequestAccountFormView.DataSource
import com.riwal.rentalapp.requestaccountform.RequestAccountFormView.Delegate
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_request_account.*


class RequestAccountFormViewImpl : RentalAppNotificationActivity(), RequestAccountFormView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private var doneMenuItem: MenuItem? = null

    private val account
        get() = dataSource.account(view = this)

    private val isValidEmail
        get() = dataSource.isValidEmail(view = this)

    private val canSubmit
        get() = dataSource.canSubmit(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_request_account)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))
        title = getString(R.string.request_an_account)

        nameEditTextView.onTextChangedListener = { onRequestAccountFormChanged(account.copy(name = it)) }
        companyEditTextView.onTextChangedListener = { onRequestAccountFormChanged(account.copy(company = it)) }
        emailEditTextView.onTextChangedListener = { onRequestAccountFormChanged(account.copy(email = it)); displayEmailValidityMessage() }
        phoneNumberEditText.onTextChangedListener = { onRequestAccountFormChanged(account.copy(phoneNumber = it)) }
        VATNumberEditText.onTextChangedListener = { onRequestAccountFormChanged(account.copy(vatNumber = it)) }

        updateUI(animated = false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_request_account, menu)
        doneMenuItem = menu.findItem(R.id.doneItem)
        menu.items.forEach { it.icon?.setTintList(R.color.toolbar_button) }
        updateUI(animated = false)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> delegate.onNavigateBackSelected(view = this)
            R.id.doneItem -> delegate.onSubmitSelected(view = this)
        }
        return true
    }

    override fun onBackPressed() {
        delegate.onNavigateBackSelected(view = this)
    }

    override fun shouldDeferNotification(notification: Notification) = true


    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onRequestAccountFormChanged(account: Account) {
        delegate.onAccountChanged(view = this, account = account)
    }


    /*-------------------------------- RequestAccountFormView methods --------------------------------*/


    override fun navigateBack() {
        finish(ModalPopActivityTransition)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition())
        }

        doneMenuItem?.isEnabled = canSubmit

    }

    private fun displayEmailValidityMessage() {
        emailEditTextView.error = if (isValidEmail) null else getString(R.string.error_invalid_mail_address)
    }

}
