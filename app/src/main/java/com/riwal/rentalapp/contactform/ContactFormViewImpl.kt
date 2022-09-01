package com.riwal.rentalapp.contactform

import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.clearFocusAndHideKeyboard
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.requestFocusAndShowKeyboard
import com.riwal.rentalapp.common.extensions.android.startActivity
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.contactform.ContactFormView.DataSource
import com.riwal.rentalapp.contactform.ContactFormView.Delegate
import com.riwal.rentalapp.project.ProjectsViewImpl
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_contact_form.*

class ContactFormViewImpl : RentalAppNotificationActivity(), ContactFormView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    override val snackbarContainer: View
        get() = contentCoordinator

    private val contact
        get() = dataSource.contact(view = this)

    private val isValidEmail
        get() = dataSource.isValidEmail(view = this)

    private val canContinue
        get() = dataSource.canContinue(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_contact_form)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, color(R.color.app_bar_text))
        title = getString(R.string.page_contact)

        companyEditTextView.text = contact?.company
        companyEditTextView.onTextChangedListener = { text -> delegate.onCompanyInputChanged(text) }

        nameEditTextView.text = contact?.name
        nameEditTextView.onTextChangedListener = { text -> delegate.onNameInputChanged(text) }

        phoneEditTextView.text = contact?.phoneNumber
        phoneEditTextView.onTextChangedListener = { text -> delegate.onPhoneInputChanged(text) }

        emailEditTextView.text = contact?.email
        emailEditTextView.onTextChangedListener = { text -> delegate.onEmailInputChanged(text) }

        continueButton.setOnClickListener { delegate.onContinueButtonClicked() }

        // Post delayed because otherwise the keyboard won't show -_-
        companyEditTextView.postDelayed({
            if (companyEditTextView.text!!.isEmpty()) {
                companyEditTextView.requestFocusAndShowKeyboard()
            } else {
                companyEditTextView.clearFocusAndHideKeyboard()
            }
        }, 100)

        updateUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> delegate.onBackButtonClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onBackButtonClicked()
    }


    /*--------------------------------------- ContactView ----------------------------------------*/


    override fun navigateBack() {
        finish(transition = ModalPopActivityTransition)
    }

    override fun navigateToProject() {
        startActivity(ProjectsViewImpl::class)
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    override fun updateUI(animated: Boolean) {
        emailEditTextView.error = if (isValidEmail) null else getString(R.string.error_invalid_mail_address)
        continueButton.isEnabled = canContinue
    }
}