package com.riwal.rentalapp.accessoriesonrentdetail

import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.core.ifNullOrBlank
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.extensions.widgets.setTextColorFromResource
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.SingleLineKeyValueRowViewHolder
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.common.ui.transition.PopActivityTransition
import com.riwal.rentalapp.offrentpanel.OffRentPanelViewImpl
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormViewImpl
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_accessories_on_rent_detail.*
import kotlinx.android.synthetic.main.view_accessories_on_rent_detail.orderSpecificationsRecyclerView
import kotlinx.coroutines.cancel

class AccessoryOnRentDetailViewImpl : RentalAppNotificationActivity(), AccessoryOnRentDetailView, EasyRecyclerView.Delegate, EasyRecyclerView.DataSource {


    /*---------------------------------------- Properties ----------------------------------------*/

    override lateinit var dataSource: AccessoryOnRentDetailView.Datasource
    override lateinit var delegate: AccessoryOnRentDetailView.Delegate

    private var offRentPanel: OffRentPanelViewImpl? = null

    private val accessoriesOnRent
        get() = dataSource.accessoriesOnRent(view = this)

    private val canStopRenting
        get() = dataSource.canStopRenting(view = this)

    private val canCancelRenting
        get() = dataSource.canCancelRenting(view = this)

    private val canRequestChanges
        get() = dataSource.canRequestChanges(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val shouldShowLastInspectionDate
        get() = dataSource.shouldShowLastInspectionDate(view = this)

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val actions
        get() = when {
            canCancelRenting -> listOf(Action.REQUEST_CHANGES, Action.CANCEL_RENT)
            else -> listOf(Action.OFF_RENT, Action.REQUEST_CHANGES)
        }

    private val contact
        get() = accessoriesOnRent.contact

    private val project
        get() = accessoriesOnRent.project

    private val accessoriesDetails
        get() = listOf(
                getString(R.string.key_order_number) to accessoriesOnRent.orderNumber,
                getString(R.string.key_fleet_number) to accessoriesOnRent.fleetNumber,
                getString(R.string.last_inspection_date) to if (shouldShowLastInspectionDate) accessoriesOnRent.lastInspectionDate!!.toLocalDate().toMediumStyleString() else null,
                getString(R.string.key_rental_order_status) to getString(accessoriesOnRent.status.localizedNameRes),
                getString(R.string.key_rental_order_start_renting) to accessoriesOnRent.onRentDateTime.toMediumStyleString(this),
                getString(R.string.key_rental_order_stop_renting) to offRentText,
                getString(R.string.key_contact_person) to contact.name,
                getString(R.string.key_rental_order_project) to project.name,
                getString(R.string.key_project_address) to project.address,
                getString(R.string.key_operator_name) to project.contactName.ifNullOrBlank("-"),
                getString(R.string.key_purchase_order) to accessoriesOnRent.purchaseOrder,
                getString(R.string.key_price_rate) to accessoriesOnRent.priceRate
        ).filter { it.second != null }

    private val offRentText: String
        get() {
            val offRent = accessoriesOnRent.offRentDateTime!!
            return if (accessoriesOnRent.isOffRentDateFinal) offRent.toMediumStyleString(this) else getString(R.string.open_rental)
        }

    private val isRentalPanelOpen
        get() = offRentPanel?.isOpen ?: false


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        setContentView(R.layout.view_accessories_on_rent_detail)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = accessoriesOnRent.accessoryName


        orderSpecificationsRecyclerView.dataSource = this
        orderSpecificationsRecyclerView.delegate = this

        accessoriesOnRentActionsRecyclerView.dataSource = this
        accessoriesOnRentActionsRecyclerView.delegate = this

        val layoutParams = accessoriesOnRentActionsRecyclerView.layoutParams

        layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, resources.displayMetrics).toInt()


        updateUI(animated = false)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navigateBack()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            isRentalPanelOpen -> offRentPanel?.navigateBack()
            else -> navigateBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    override fun updateUI(animated: Boolean) {
        super.updateUI(animated)

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .excludeTarget(orderSpecificationsRecyclerView, true)
            )
        }


        orderSpecificationsRecyclerView.notifyDataSetChanged()

        accessoriesOnRentChatButton.isVisible = isChatEnabled
        accessoriesOnRentChatButton.numberOfUnreadMessages = numberOfUnreadMessages

        accessoriesOnRentPhoneCallButton.isVisible = isPhoneCallEnable
        accessoriesOnRentPhoneCallButton.phoneNumber = rentalContactNumber


    }


    /*--------------------------------------- AccessoriesOnRent Detail  ---------------------------------------*/


    override fun askForComments(callback: (comments: String) -> Unit) = runOnUiThread {
        RentalDialogs.commentsDialog(this, callback).show()
    }

    override fun navigateToChangeRequestPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(ChangeRequestFormViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
    }

    override fun showOffRentPanel(preparationHandler: ControllerPreparationHandler) {
        offRentPanel = OffRentPanelViewImpl(this)
        offRentPanel!!.show(contentView, preparationHandler)
    }


    /*-------------------------------- Easy RecyclerView Delegate --------------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        when (recyclerView) {
            orderSpecificationsRecyclerView -> {

                viewHolder as SingleLineKeyValueRowViewHolder
                viewHolder.keyValue = accessoriesDetails[indexPath.row]

            }

            else -> {

                viewHolder as MaterialRowViewHolder

                val action = actions[indexPath.row]
                val isEnabled = when (action) {
                    Action.OFF_RENT -> canStopRenting
                    Action.CANCEL_RENT -> canCancelRenting
                    else -> canRequestChanges
                }

                viewHolder.setTitle(action.nameRes)
                viewHolder.setImage(action.iconRes, MaterialRowViewHolder.ImageStyle.SQUARE_SMALL)
                viewHolder.itemView.isEnabled = isEnabled

                viewHolder.titleTextView.setTextColorFromResource(if (isEnabled) R.color.material_text_high_emphasis else R.color.material_text_disabled)
                viewHolder.imageView.setTintFromResource(if (isEnabled) R.color.colorPrimary else R.color.material_text_disabled)

            }
        }
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        if (recyclerView == orderSpecificationsRecyclerView) {
            return
        }

        when (actions[indexPath.row]) {
            Action.OFF_RENT -> delegate.onStopRentingSelected()
            Action.CANCEL_RENT -> delegate.onCancelRentingSelected()
            Action.REQUEST_CHANGES -> delegate.onRequestChangesSelected()
        }
    }


    /*------------------------------ Easy RecyclerView Data Source -------------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return when (recyclerView) {
            orderSpecificationsRecyclerView -> accessoriesDetails.size
            else -> actions.size
        }
    }

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder {
        return when (recyclerView) {
            orderSpecificationsRecyclerView -> SingleLineKeyValueRowViewHolder(itemView)
            else -> MaterialRowViewHolder(itemView)
        }
    }

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int {
        return when (recyclerView) {
            orderSpecificationsRecyclerView -> R.layout.row_single_line_key_value
            else -> R.layout.row_material_list_item
        }
    }

    override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Long {
        return accessoriesDetails[indexPath.row].first.hashCode().toLong()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun navigateBack() = runOnUiThread {
        val transition = PopActivityTransition
        finish(transition = transition)
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    enum class Action(@StringRes
                      val nameRes: Int,
                      @DrawableRes
                      val iconRes: Int) {
        CANCEL_RENT(R.string.cancel_rent_button, R.drawable.ic_remove_circle),
        OFF_RENT(R.string.off_rent_button, R.drawable.ic_remove_circle),
        REQUEST_CHANGES(R.string.request_changes, R.drawable.ic_edit)
    }

}