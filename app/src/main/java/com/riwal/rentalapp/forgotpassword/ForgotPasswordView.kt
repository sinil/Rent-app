package com.riwal.rentalapp.forgotpassword

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import java.lang.Exception

interface ForgotPasswordView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun notifyInvalidEmail()
    fun notifyResetPasswordFailed(error: Exception)

    interface DataSource {
        fun email(view: ForgotPasswordView): String
        fun isValidEmail(view: ForgotPasswordView): Boolean
        fun isLoadingResetPassword(view: ForgotPasswordView): Boolean
    }

    interface Delegate {
        fun backButtonPressed()
        fun onEmailChanged(newEmail: String)
        fun resetPasswordButtonPressed(view: ForgotPasswordView)
    }

}