package com.riwal.rentalapp.myprojects.machinetransfernotifications

import android.app.Activity
import android.content.Intent
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.api.RejectTransferMachineRequestBody
import com.riwal.rentalapp.uploadimage.UploadImageViewImpl
import kotlinx.android.synthetic.main.view_notification.*

class TransferNotificationViewImpl : RentalAppNotificationActivity(), TransferNotificationView, EasyRecyclerView.Delegate, EasyRecyclerView.DataSource {


    /*--------------------------------------- Properties -----------------------------------------*/

    override lateinit var dataSource: TransferNotificationView.DataSource
    override lateinit var delegate: TransferNotificationView.Delegate

    private val transferNotification
        get() = dataSource.notification(view = this)

    private val hasFailedLoadingNotification
        get() = dataSource.hasFailedLoadingNotification(view = this)

    private val isUpdatingTransferNotification
        get() = dataSource.isUpdatingTransferMachine(view = this)

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val currentUser
        get() = dataSource.user(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_notification)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.notification)


        notificationEasyRecyclerView.delegate = this
        notificationEasyRecyclerView.dataSource = this

        retryLoadingTransferNotificationButton.setOnClickListener { delegate.onRetryLoadingTransferNotificationSelected(view = this) }


        updateUI(animated = true)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            val transferId = data?.extras?.get("extras")
            Snackbar.make(contentView, transferId.toString(), Snackbar.LENGTH_SHORT).show()
        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                navigateBack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun updateUI(animated: Boolean) {
        super.updateUI(animated)

        if (animated) {
            val transition = ParallelAutoTransition()
                    .excludeTarget(notificationEasyRecyclerView, true)
            beginDelayedTransition(transition)
        }


        notificationEasyRecyclerView.notifyDataSetChanged()
        notificationEasyRecyclerView.isVisible = transferNotification.isNotEmpty() && !hasFailedLoadingNotification


        retryLoadingTransferNotificationContainer.isVisible = !isUpdatingTransferNotification && hasFailedLoadingNotification
        emptyTransferNotificationView.isVisible = transferNotification.isEmpty() && !isUpdatingTransferNotification && !hasFailedLoadingNotification
        activityIndicator.isVisible = isUpdatingTransferNotification


    }


    /*------------------------------------- Private methods --------------------------------------*/

    override fun navigateBack() = runOnUiThread { finish() }

    override fun navigateToImageUploadPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(UploadImageViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun showConfirmDialog(transferMachine: RejectTransferMachineRequestBody, callback: () -> Unit) {
        RentalDialogs.messageDialog(this,
                getString(R.string.reject_confirmation_dialog_message), getString(R.string.transfer_reject_title), getString(R.string.confirm), true, callback).show()
    }


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = transferNotification.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int = R.layout.row_transfer_notification
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder = TransferNotificationRowViewHolder(itemView, this, delegate)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        viewHolder as TransferNotificationRowViewHolder
        val notification = transferNotification[indexPath.row]
        viewHolder.updateWith(notification, activeCountry, currentUser?.currentCustomer)
    }



}