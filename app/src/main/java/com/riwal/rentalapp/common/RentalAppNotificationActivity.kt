package com.riwal.rentalapp.common

import android.annotation.SuppressLint
import android.net.NetworkInfo.DetailedState
import android.net.NetworkInfo.DetailedState.CONNECTED
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.mvc.BaseActivity
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.Notification.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


@SuppressLint("Registered")
open class RentalAppNotificationActivity : BaseActivity() {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var connectivityState: DetailedState? = null
    private var deferredNotifications = emptyList<Notification>()

    open val snackbarContainer: View
        get() = contentView


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        postDeferredNotifications()
    }


    /*------------------------------------------ Events ------------------------------------------*/


    @Subscribe
    fun onNotificationReceived(notification: Notification) {
        if (shouldDeferNotification(notification)) {
            deferredNotifications += notification
        } else {
            showMessage(notification)
        }
    }

    @Subscribe(sticky = true)
    fun onInternetConnectionChanged(status: DetailedState) {
        connectivityState = status
        onNotificationReceived(REACHABILITY_CHANGED)
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    open fun shouldDeferNotification(notification: Notification) = false

    open fun shouldShowNotification(notification: Notification) = true

    fun showInformativeMessage(@StringRes message: Int) = runOnUiThread {
        Snackbar.make(snackbarContainer, getString(message), LENGTH_LONG).show()
    }

    fun showInformativeMessage( message: String) = runOnUiThread {
        Snackbar.make(snackbarContainer, message, LENGTH_LONG).show()
    }

    fun showFailureAlert(@StringRes message: Int) = runOnUiThread {
        requestFailedAlert(title = R.string.error, message = message).show()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun postDeferredNotifications() {
        deferredNotifications.forEach {
            EventBus.getDefault().post(it)
        }
        deferredNotifications = emptyList()
    }

    private fun showMessage(notification: Notification) {

        if (!shouldShowNotification(notification)) {
            return
        }

        when (notification) {
            MACHINE_ADDED -> showInformativeMessage(R.string.machine_order_added)
            ORDER_SUBMITTED -> showInformativeMessage(R.string.order_request_succeeded)
            ACCOUNT_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.account_request_succeeded)
            ACCOUNT_REQUEST_FAILED -> showInformativeMessage(R.string.account_request_failed)
            OFF_RENT_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.off_rent_requested)
            OFF_RENT_REQUEST_FAILED -> showFailureAlert(R.string.request_off_rent_failed)
            CANCEL_RENT_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.cancel_rent_requested)
            CANCEL_RENT_REQUEST_FAILED -> showFailureAlert(R.string.request_cancel_rent_failed)
            CHANGE_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.changes_requested_message)
            CHANGE_REQUEST_FAILED -> showFailureAlert(R.string.change_request_failed)
            SEND_FEEDBACK_SUCCEEDED -> showInformativeMessage(R.string.message_send_feedback_succeeded)
            SEND_FEEDBACK_FAILED -> showInformativeMessage(R.string.message_send_feedback_failed)
            SEND_BREAKDOWN_REPORT_SUCCEEDED -> showInformativeMessage(R.string.breakdown_report_message)
            SEND_BREAKDOWN_REPORT_FAILED -> showFailureAlert(R.string.breakdown_report_failed)
            TRAINING_REQUEST_SUCCEED -> showInformativeMessage(R.string.training_request_succeed)
            SEND_TRANSFER_MACHINES_SUCCEEDED -> showInformativeMessage(R.string.transfer_requested)
            SEND_TRANSFER_MACHINES_FAILED -> showInformativeMessage(R.string.request_transfer_failed)
            SEND_REJECT_TRANSFER_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.reject_transfer_requested)
            SEND_REJECT_TRANSFER_REQUEST_FAILED -> showInformativeMessage(R.string.request_reject_transfer_failed)
            SEND_ACCEPT_TRANSFER_REQUEST_SUCCEEDED -> showInformativeMessage(R.string.accept_transfer_succeeded)
            SEND_ACCEPT_TRANSFER_REQUEST_FAILED -> showInformativeMessage(R.string.accept_transfer_failed)
            INVOICE_NOT_AVAILABLE -> showFailureAlert(R.string.file_not_available)
            DOWNLOAD_INVOICE_FAILED -> showInformativeMessage(R.string.error_unknown)
            QUOTATION_NOT_AVAILABLE -> showFailureAlert(R.string.file_not_available)
            DOWNLOAD_QUOTATION_FAILED -> showInformativeMessage(R.string.error_unknown)
            REACHABILITY_CHANGED -> {
                if (connectivityState != CONNECTED) {
                    showInformativeMessage(R.string.no_internet_connection)
                }
            }

        }
    }

    private fun requestFailedAlert(@StringRes title: Int, @StringRes message: Int) = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)

    open fun showProgressDialog() {}
}