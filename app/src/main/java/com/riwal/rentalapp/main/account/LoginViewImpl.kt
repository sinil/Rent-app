package com.riwal.rentalapp.main.account

import android.content.Context
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.rentalapp.isServerDown
import com.riwal.rentalapp.common.extensions.rentalapp.rentalAppMessage
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.forgotpassword.ForgotPasswordViewImpl
import com.riwal.rentalapp.main.account.LoginView.DataSource
import com.riwal.rentalapp.main.account.LoginView.Delegate
import com.riwal.rentalapp.requestaccountform.RequestAccountFormViewImpl
import kotlinx.android.synthetic.main.view_account_login.view.*

class LoginViewImpl @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseView(context, attrs, defStyleAttr), LoginView {


    /*--------------------------------------- Properties -----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private val isLoggingIn
        get() = dataSource.isLoggingIn(view = this)

    private val email
        get() = dataSource.email(view = this)

    private val password
        get() = dataSource.password(view = this)

    private val isEmailValid
        get() = dataSource.isEmailValid(view = this)

    private val isPasswordValid
        get() = dataSource.isPasswordValid(view = this)

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        addSubview(R.layout.view_account_login)

        emailEditText.onTextChangedListener = { onEmailChanged() }
        passwordEditText.onTextChangedListener = { onPasswordChanged() }

        passwordEditText.onEditorActionListener = { actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.callOnClick()
            }
        }

        loginButton.setOnClickListener { onLoginButtonPressed() }
        requestAccountButton.setOnClickListener { delegate.onRequestAccountSelected() }
        forgotPasswordTextView.setOnClickListener { delegate.onForgotPasswordSelected() }
    }


    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onLoginButtonPressed() {
        clearFocusAndHideKeyboard()
        delegate.onLoginSelected(view = this)
        displayEmailAndPasswordValidityMessage()
    }

    private fun onEmailChanged() {
        delegate.onEmailChanged(emailEditText.text!!)
    }

    private fun onPasswordChanged() {
        delegate.onPasswordChanged(passwordEditText.text!!)
    }


    /*---------------------------------------- LoginView -----------------------------------------*/


    override fun navigateToRequestAccountPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RequestAccountFormViewImpl::class, preparationHandler, ModalPushActivityTransition)
    }


    override fun navigateToForgotPasswordPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(ForgotPasswordViewImpl::class, preparationHandler, ModalPushActivityTransition)
    }


    override fun notifyLoginFailed(error: Exception) = runOnUiThread {
        AlertDialog.Builder(context)
                .setTitle(R.string.login_failed)
                .setMessage(getLoginFailedErrorMessage(error))
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .create()
                .show()
    }

    override fun notifyResetPasswordSucceed() {
        Snackbar.make(rootContainer, getString(R.string.reset_password__success_message).toString(),
                Snackbar.LENGTH_LONG)
                .show()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .excludeTarget(emailEditText, true)
                    .excludeTarget(passwordEditText, true))
        }

        emailEditText.text = email
        emailEditText.isEnabled = !isLoggingIn

        passwordEditText.text = password
        passwordEditText.isEnabled = !isLoggingIn

        loginButton.isEnabled = !isLoggingIn
        loggingInProgressBar.isVisible = isLoggingIn
    }

    private fun displayEmailAndPasswordValidityMessage() {

        emailTextInputLayout.error = if (isEmailValid) null else context.getString(R.string.error_invalid_mail_address)
        emailTextInputLayout.isErrorEnabled = !isEmailValid

        passwordTextInputLayout.error = if (isPasswordValid) null else context.getString(R.string.error_invalid_password)
        passwordTextInputLayout.isErrorEnabled = !isPasswordValid
    }

    private fun getLoginFailedErrorMessage(error: Exception): String {
        if (error.cause != null) {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (error.isServerDown(connectivityManager)) {
                return getString(R.string.server_not_reachable)!!
            }
            return getString(R.string.no_internet_connection)!!
        }
        return error.rentalAppMessage(context)

    }

}