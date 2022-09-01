package com.riwal.rentalapp.summary

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Order

interface SummaryView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun notifyOrderRequestFailed()
    fun navigateBack()
    fun goToMainPage()
    fun navigateToWebView(preparationHandler: ControllerPreparationHandler)
    fun showSendRequestConfirmation()
    fun showPurchaseOrderMandatoryError()

    interface DataSource {
        fun isLoggedIn(view: SummaryView): Boolean
        fun order(view: SummaryView): Order
        fun isSubmittingOrder(view: SummaryView): Boolean
        fun isPrivacyPolicyChecked(view: SummaryView): Boolean

    }

    interface Delegate {
        fun onBackButtonPressed()
        fun onPurchaseOrderInputChanged(text: String)
        fun onNotesInputChanged(text: String)
        fun onSendSelected(view: SummaryView)
        fun onConfirmSendSelected()
        fun onPrivacyPolicyClicked()
        fun onPrivacyPolicyChecked(isChecked: Boolean)
    }

}