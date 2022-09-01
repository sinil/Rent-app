package com.riwal.rentalapp.forgotpassword

import com.riwal.rentalapp.common.extensions.core.isEmail
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ForgotPasswordController(
        val view: ForgotPasswordView,
        val sessionManager: SessionManager
) : ViewLifecycleObserver, ForgotPasswordView.DataSource, ForgotPasswordView.Delegate, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    var email = ""
    var delegate: Delegate? = null

    private val isEmailFilledOutAndValid
        get() = email.isNotBlank() && email.isEmail

    private var isLoadingResetPassword = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*----------------------------- Forgot Password View Delegate --------------------------------*/


    override fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    override fun resetPasswordButtonPressed(view: ForgotPasswordView) {
        if (isEmailFilledOutAndValid) {
            resetPassword()
        } else {
            view.notifyDataChanged()
        }
    }

    override fun backButtonPressed() {
        view.navigateBack()
    }


    /*------------------------------ Forgot Password View Data Source ----------------------------*/


    override fun email(view: ForgotPasswordView): String = email
    override fun isValidEmail(view: ForgotPasswordView): Boolean = isEmailFilledOutAndValid
    override fun isLoadingResetPassword(view: ForgotPasswordView): Boolean = isLoadingResetPassword

    /*------------------------------------- Private methods --------------------------------------*/


    private fun resetPassword() = launch {
        try {
            isLoadingResetPassword = true
            sessionManager.resetPassword(email).apply {
                isLoadingResetPassword = false
                if (this) {
                    delegate?.onResetPasswordSuccessfullyFinished()
                    view.navigateBack()
                } else
                    view.notifyInvalidEmail()
            }
        } catch (error: Exception) {
            isLoadingResetPassword = false
            view.notifyResetPasswordFailed(error)
        }
    }

    /*----------------------------------------- Interfaces ---------------------------------------*/


    interface Delegate {
        fun onResetPasswordSuccessfullyFinished()
    }

}