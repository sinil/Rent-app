package com.riwal.rentalapp.summary

import android.content.res.Resources
import com.riwal.rentalapp.R
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.OrderManager
import com.riwal.rentalapp.model.SessionManager
import com.riwal.rentalapp.webview.WebViewController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SummaryController(
        val view: SummaryView,
        val sessionManager: SessionManager,
        val orderManager: OrderManager,
        val country: Country,
        val analytics: RentalAnalytics,
        val resources: Resources
) : ViewLifecycleObserver, SummaryView.DataSource, SummaryView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    private val order
        get() = orderManager.currentOrder

    private var isSubmittingOrder = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private var isPrivacyPolicyChecked = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private val isPurchaseOrderFilledOut
        get() = !order.purchaseOrder.isNullOrBlank()

    private val isLoggedIn
        get() = sessionManager.isLoggedIn


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()

        analytics.userLookingAtOrderSummary()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*---------------------------------- SummaryView DataSource ----------------------------------*/

    override fun isLoggedIn(view: SummaryView) = isLoggedIn
    override fun order(view: SummaryView) = order
    override fun isSubmittingOrder(view: SummaryView) = isSubmittingOrder
    override fun isPrivacyPolicyChecked(view: SummaryView): Boolean = isPrivacyPolicyChecked


    /*----------------------------------- SummaryView Delegate -----------------------------------*/


    override fun onBackButtonPressed() {
        if (!isSubmittingOrder) {
            view.navigateBack()
        }
    }

    override fun onPurchaseOrderInputChanged(text: String) {
        order.purchaseOrder = if (text.isBlank()) null else text
        orderManager.save()
    }

    override fun onNotesInputChanged(text: String) {
        order.notes = if (text.isBlank()) null else text
        orderManager.save()
    }

    override fun onSendSelected(view: SummaryView) {

        if (country.name == "ES") {
            view.showSendRequestConfirmation()
        } else if (isPurchaseOrderFilledOut) {
            view.showSendRequestConfirmation()
        } else {
            view.showPurchaseOrderMandatoryError()
        }
    }

    override fun onConfirmSendSelected() {
        launch {
            isSubmittingOrder = true

            try {
                orderManager.submitCurrentOrder(country)
                analytics.trackCheckOutEvent(country)
                analytics.checkoutComplete()
                view.goToMainPage()
            } catch (error: Exception) {
                error.printStackTrace()
                isSubmittingOrder = false
                view.notifyOrderRequestFailed()
            }
        }
    }

    override fun onPrivacyPolicyClicked() {
        view.navigateToWebView { destination ->
            val controller = destination as WebViewController
            controller.pageTitle = resources.getString(R.string.setting_privacy_policy)
            controller.pageUrl = resources.getString(R.string.privacy_policy_url)
        }
    }

    override fun onPrivacyPolicyChecked(isChecked: Boolean) {
        isPrivacyPolicyChecked = isChecked
    }

}