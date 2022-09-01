package com.riwal.rentalapp.main.account

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView

interface LoginView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateToRequestAccountPage(preparationHandler: ControllerPreparationHandler)
    fun navigateToForgotPasswordPage(preparationHandler: ControllerPreparationHandler)
    fun notifyLoginFailed(error: Exception)
    fun notifyResetPasswordSucceed()

    interface DataSource {
        fun email(view: LoginView): String
        fun password(view: LoginView): String
        fun isEmailValid(view: LoginView): Boolean
        fun isPasswordValid(view: LoginView): Boolean
        fun isLoggingIn(view: LoginView): Boolean
    }

    interface Delegate {
        fun onEmailChanged(newEmail: String)
        fun onPasswordChanged(newPassword: String)
        fun onLoginSelected(view: LoginView)
        fun onRequestAccountSelected()
        fun onForgotPasswordSelected()
    }

}