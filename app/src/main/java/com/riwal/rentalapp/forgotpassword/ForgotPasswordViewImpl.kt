package com.riwal.rentalapp.forgotpassword

import android.content.Context
import android.net.ConnectivityManager
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.clearFocusAndHideKeyboard
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.rentalapp.isServerDown
import com.riwal.rentalapp.common.extensions.rentalapp.rentalAppMessage
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import kotlinx.android.synthetic.main.view_account_forget_password.*

class ForgotPasswordViewImpl : RentalAppNotificationActivity(), ForgotPasswordView {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: ForgotPasswordView.DataSource
    override lateinit var delegate: ForgotPasswordView.Delegate

    private val isValidEmail
        get() = dataSource.isValidEmail(view = this)

    private val isLoadingResetPassword
        get() = dataSource.isLoadingResetPassword(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_account_forget_password)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        toolbar.title = getString(R.string.reset_password_button_title)

        emailEditText.onTextChangedListener = { onEmailChanged() }
        resetPasswordButton.setOnClickListener { onResetPasswordButtonPressed() }
    }

    override fun updateUI(animated: Boolean) {
        resetPasswordButton.isEnabled = !isLoadingResetPassword
        resetPasswordProgressBar.isVisible = isLoadingResetPassword
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> delegate.backButtonPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    /*--------------------------------- Forgot Password methods -----------------------------------*/

    override fun navigateBack() = runOnUiThread {
        finish()
    }

    override fun notifyResetPasswordFailed(error: Exception) = runOnUiThread {
        AlertDialog.Builder(this)
                .setTitle(R.string.reset_password_failed_title)
                .setMessage(getResetPasswordFailedErrorMessage(error))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .create()
                .show()
    }


    override fun notifyInvalidEmail() {
        AlertDialog.Builder(this)
                .setTitle(R.string.reset_password_failed_title)
                .setMessage(getString(R.string.reset_password_failed))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .create()
                .show()
    }

    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onResetPasswordButtonPressed() {
        clearFocusAndHideKeyboard()
        resetPasswordProgressBar.isVisible = true
        displayEmailValidityMessage()
        delegate.resetPasswordButtonPressed(view = this)
    }

    private fun onEmailChanged() {
        delegate.onEmailChanged(newEmail = emailEditText.text!!)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun displayEmailValidityMessage() {
        
        emailTextInputLayout.error = if (isValidEmail) null else getString(R.string.error_invalid_mail_address)
        emailTextInputLayout.isErrorEnabled = !isValidEmail
    }

    private fun getResetPasswordFailedErrorMessage(error: Exception): String {

        if (error.cause != null) {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (error.isServerDown(connectivityManager)) {
                return getString(R.string.server_not_reachable)
            }
            return getString(R.string.no_internet_connection)
        }
        return error.rentalAppMessage(this)
    }


}