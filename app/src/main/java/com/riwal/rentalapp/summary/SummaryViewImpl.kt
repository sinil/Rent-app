package com.riwal.rentalapp.summary

import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.datetime.milliseconds
import com.riwal.rentalapp.common.extensions.widgets.scrollToBottom
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.main.MainViewImpl
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.webview.WebViewImpl
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_summary.*

class SummaryViewImpl : RentalAppNotificationActivity(), SummaryView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: SummaryView.DataSource
    override lateinit var delegate: SummaryView.Delegate

    override val snackbarContainer: View
        get() = contentCoordinator

    private val isLoggedIn
        get() = dataSource.isLoggedIn(view = this)

    private val order
        get() = dataSource.order(view = this)

    private val machines
        get() = order.machineOrders

    private val project
        get() = order.project!!

    private val contact
        get() = order.contact!!

    private val isSubmittingOrder
        get() = dataSource.isSubmittingOrder(view = this)

    private val isPrivacyPolicyChecked
        get() = dataSource.isPrivacyPolicyChecked(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_summary)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.page_summary)

        machineOrdersRecyclerView.dataSource = this
        machineOrdersRecyclerView.delegate = this

        customerNameTextView.text = contact.company
        contactTextView.text = contact.name
        phoneTextView.text = contact.phoneNumber
        emailTextView.text = contact.email

        projectNameTextView.text = project.name
        projectContactNameTextView.text = project.contactName
        projectContactPhoneTextView.text = project.contactPhoneNumber
        projectAddressTextView.text = project.address


        if (!isLoggedIn) {
            sendRequestButton.isEnabled = false
            privacyPolicyLink.isVisible = true
            val privacyPolicy = SpannableString(getString(R.string.privacy_policy_message))
            privacyPolicy.setSpan(UnderlineSpan(), 0, privacyPolicy.length, 0)
            privacyPolicyText.text = privacyPolicy
            privacyPolicyLink.setOnClickListener {
                delegate.onPrivacyPolicyClicked()
            }
            privacyPolicyCheckBox.setOnCheckedChangeListener { _, isChecked ->
                delegate.onPrivacyPolicyChecked(isChecked)
            }
        }

        purchaseOrderEditText.text = order.purchaseOrder
        purchaseOrderEditText.onTextChangedListener = { text ->
            delegate.onPurchaseOrderInputChanged(text)
            setPurchaseOrderMandatoryErrorVisibility(false)
        }
        purchaseOrderEditText.onImeBackListener = { purchaseOrderEditText.clearFocusAndHideKeyboard() }
        notesEditText.text = order.notes
        notesEditText.onTextChangedListener = { text -> delegate.onNotesInputChanged(text) }
        notesEditText.onImeBackListener = { notesEditText.clearFocusAndHideKeyboard() }

        sendRequestButton.setOnClickListener { delegate.onSendSelected(this) }
    }

    override fun onAppear() {
        super.onAppear()

        runOnUIThreadAfter(500.milliseconds()) {
            scrollView.scrollToBottom()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                delegate.onBackButtonPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        delegate.onBackButtonPressed()
    }

    override fun shouldDeferNotification(notification: Notification) = true


    /*-------------------------------------- SummaryView -----------------------------------------*/


    override fun notifyOrderRequestFailed() = runOnUiThread {
        AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(R.string.order_request_failed)
                .setPositiveButton(R.string.retry) { _, _ -> delegate.onConfirmSendSelected() }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }

    override fun navigateBack() = finish()

    override fun goToMainPage() {
        val intent = Intent(this, MainViewImpl::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun showSendRequestConfirmation() {
        AlertDialog.Builder(this)
                .setTitle(R.string.place_this_order)
                .setMessage(R.string.order_confirmation_dialog_message)
                .setPositiveButton(R.string.submit) { _, _ -> delegate.onConfirmSendSelected() }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }

    override fun showPurchaseOrderMandatoryError() {
        setPurchaseOrderMandatoryErrorVisibility(true)
        scrollView.scrollToBottom()
    }

    override fun navigateToWebView(preparationHandler: ControllerPreparationHandler) {
        startActivity(WebViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    /*-------------------------------- EasyRecyclerView.DataSource -------------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = machines.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_machine_summary
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = MachineSummaryRowViewHolder(itemView)


    /*--------------------------------- EasyRecyclerView.Delegate --------------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        viewHolder as MachineSummaryRowViewHolder
        val machineOrder = machines[indexPath.row]
        viewHolder.updateWith(machineOrder)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition())
        }

        activityIndicator.isVisible = isSubmittingOrder
        sendRequestButton.isEnabled = !isSubmittingOrder
        sendRequestButton.isEnabled = isPrivacyPolicyChecked || isLoggedIn
    }

    private fun setPurchaseOrderMandatoryErrorVisibility(isVisible: Boolean) {
        purchaseOrderTextInputLayout.error = if (isVisible) getString(R.string.purchase_order_required) else null
        purchaseOrderTextInputLayout.isErrorEnabled = isVisible
    }

}