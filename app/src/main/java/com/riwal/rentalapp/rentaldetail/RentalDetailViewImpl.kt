package com.riwal.rentalapp.rentaldetail

import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.contentView
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.core.StringCapitalizationOptions.MID_SENTENCE
import com.riwal.rentalapp.common.extensions.core.StringCapitalizationOptions.NOUN
import com.riwal.rentalapp.common.extensions.core.ifBlank
import com.riwal.rentalapp.common.extensions.core.ifNullOrBlank
import com.riwal.rentalapp.common.extensions.core.withCapitalization
import com.riwal.rentalapp.common.extensions.datetime.toMediumStyleString
import com.riwal.rentalapp.common.extensions.glide.loadImage
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.extensions.widgets.setTextColorFromResource
import com.riwal.rentalapp.common.extensions.widgets.setTintFromResource
import com.riwal.rentalapp.common.ui.MapInfoWindowAdapter
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.SingleLineKeyValueRowViewHolder
import com.riwal.rentalapp.common.ui.actionsheet.ActionSheet
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder
import com.riwal.rentalapp.common.ui.easyrecyclerview.MaterialRowViewHolder.ImageStyle.SQUARE_SMALL
import com.riwal.rentalapp.common.ui.mapview.MapView
import com.riwal.rentalapp.common.ui.transition.ModalPopActivityTransition
import com.riwal.rentalapp.common.ui.transition.ModalPushActivityTransition
import com.riwal.rentalapp.common.ui.transition.PopActivityTransition
import com.riwal.rentalapp.machinedetail.MachineDetailViewImpl
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.offrentpanel.OffRentPanelViewImpl
import com.riwal.rentalapp.rentaldetail.RentalDetailViewImpl.Action.*
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownViewImpl
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormViewImpl
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_map_container.view.*
import kotlinx.android.synthetic.main.view_rental_detail.*
import kotlinx.coroutines.cancel


class RentalDetailViewImpl : RentalAppNotificationActivity(), RentalDetailView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MapView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: RentalDetailView.DataSource
    override lateinit var delegate: RentalDetailView.Delegate

    private var locationMarker: Marker? = null
    private var offRentPanel: OffRentPanelViewImpl? = null
    private val contactOptionsActionSheet by lazy {
        ActionSheet(this)
    }

    private var isOrderDetailsExpanded = false

    private val rental
        get() = dataSource.rental(view = this)

    private val rentalDeskContacts
        get() = dataSource.rentalDeskContacts(view = this)

    private val canStopRenting
        get() = dataSource.canStopRenting(view = this)

    private val canCancelRenting
        get() = dataSource.canCancelRenting(view = this)

    private val canRequestChanges
        get() = dataSource.canRequestChanges(view = this)

    private val isLoadingMachine
        get() = dataSource.isLoadingMachine(view = this)

    private val isReceivedMachineDifferent
        get() = dataSource.isReceivedMachineDifferent(view = this)

    private val isPresentedModally
        get() = dataSource.isViewPresentedModally(view = this)

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
            canCancelRenting -> listOf(REQUEST_CHANGES, CANCEL_RENT)
            canRequestBreakdown -> listOf(OFF_RENT, REQUEST_CHANGES, REPORT_BREAKDOWN)
            else -> listOf(OFF_RENT, REQUEST_CHANGES)
        }

    private val machine
        get() = rental.machine

    private val requestedMachine
        get() = requestedMachineDetail?.requestedMachine

    private val contact
        get() = rental.contact

    private val project
        get() = rental.project

    private val isMachineAvailable
        get() = machine != null

    private val isRequestedMachineAvailable
        get() = requestedMachine != null

    private val machineCoordinate
        get() = rental.machineCoordinate

    private val projectCoordinate
        get() = project.coordinate

    private val hasMachineLocation
        get() = !rental.status.isClosed && rental.machineCoordinate != null

    private val hasProjectLocation
        get() = projectCoordinate != null

    private val hasLocationMarker
        get() = locationMarker != null

    private val importantOrderDetails
        get() = listOf(
                getString(R.string.key_order_number) to rental.orderNumber,
                getString(R.string.key_fleet_number) to rental.fleetNumber.ifBlank("-"),
                getString(R.string.last_inspection_date) to if (shouldShowLastInspectionDate) rental.lastInspectionDate!!.toLocalDate().toMediumStyleString() else null,
                getString(R.string.key_rental_order_status) to getString(rental.status.localizedNameRes),
                getString(R.string.key_rental_order_start_renting) to rental.onRentDateTime.toMediumStyleString(this),
                getString(R.string.key_rental_order_stop_renting) to offRentText
        ).filter { it.second != null }

    private val lessImportantOrderDetails
        get() = listOf(
                getString(R.string.key_ordered_by) to rental.orderedByName.ifNullOrBlank("-"),
                getString(R.string.key_contact_person) to contact.name,
                getString(R.string.key_rental_order_project) to project.name,
                getString(R.string.key_project_address) to project.address.ifNullOrBlank(project.street ?: "-"),
                getString(R.string.key_operator_name) to project.contactName.ifNullOrBlank("-"),
                getString(R.string.key_purchase_order) to rental.purchaseOrder,
                getString(R.string.key_price_rate) to rental.priceRate
        )

    private val orderDetails
        get() = if (isOrderDetailsExpanded) importantOrderDetails + lessImportantOrderDetails else importantOrderDetails

    private val offRentText: String
        get() {
            val offRent = rental.offRentDateTime!!
            return if (rental.isOffRentDateFinal) offRent.toMediumStyleString(this) else getString(R.string.open_rental)
        }

    private val isRentalPanelOpen
        get() = offRentPanel?.isOpen ?: false

    private val isContactOptionsActionSheetOpen
        get() = contactOptionsActionSheet.isOpen

    private val canRequestBreakdown
        get() = dataSource.canSendBreakdownReport(view = this)

    private val requestedMachineDetail
        get() = dataSource.rentalDetail(view = this)

    private val isLoadingRequestedMachine
        get() = dataSource.isLoadingRequestedMachine(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        setContentView(R.layout.view_rental_detail)

        setSupportActionBar(toolbar)
        val navigationIcon = if (isPresentedModally) R.drawable.ic_close else R.drawable.ic_arrow_back
        toolbar.setNavigationIcon(navigationIcon, tint = color(R.color.app_bar_text))
        title = rental.rentalType

        receivedMachineDetailsView.setOnClickListener {
            if (isMachineAvailable) delegate.onMachineSelected(false) else {
                beginDelayedTransition()
                receivedMachineUnavailableView.isVisible = true
            }
        }


        receivedMachineUnavailableButton.setOnClickListener { showRentalDeskContactsSheet() }

        requestedMachineDetailsView.setOnClickListener {
            if (isRequestedMachineAvailable) delegate.onMachineSelected(true) else {
                beginDelayedTransition()
                requestedMachineUnavailableView.isVisible = true
            }
        }
        requestedMachineUnavailableButton.setOnClickListener { showRentalDeskContactsSheet() }

        orderSpecificationsRecyclerView.dataSource = this
        orderSpecificationsRecyclerView.delegate = this
        toggleExpansionButton.setOnClickListener { onToggleExpansionButtonClicked() }

        actionsRecyclerView.dataSource = this
        actionsRecyclerView.delegate = this

        mapView.delegate = this

        val layoutParams = actionsRecyclerView.layoutParams
        if (canRequestBreakdown) {
            layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 168f, resources.displayMetrics).toInt()
        } else {
            layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 112f, resources.displayMetrics).toInt()
        }

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
            isContactOptionsActionSheetOpen -> contactOptionsActionSheet.close()
            else -> navigateBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(ParallelAutoTransition()
                    .excludeTarget(orderSpecificationsRecyclerView, true)
            )
        }

        val machineType = machine?.localizedMachineType(this) ?: rental.machineType
        val localCapitalizedMachineType = machineType.withCapitalization(resources.configuration.locale, setOf(MID_SENTENCE, NOUN))

        val requestedMachineType = requestedMachine?.localizedMachineType(this)
                ?: requestedMachineDetail?.machineType
        val localCapitalizedRequestedMachineType = requestedMachineType!!.withCapitalization(resources.configuration.locale, setOf(MID_SENTENCE, NOUN))

        receivedMachineDetailsView.isVisible = !isLoadingMachine
        receivedMachineActivityIndicator.isVisible = if (isLoadingMachine) true else isReceivedMachineDifferent

        requestedMachineDetailsView.isVisible = isReceivedMachineDifferent
        requestedMachineDividerView.isVisible = isLoadingRequestedMachine || isReceivedMachineDifferent
        requestedMachineActivityIndicator.isVisible = isLoadingRequestedMachine

        receivedMachineImageView.loadImage(machine?.thumbnailUrl, placeholder = R.drawable.img_machines_contour)
        receivedMachineImageView.imageTintList = if (machine?.thumbnailUrl != null) null else resources.getColorStateList(R.color.material_icon_disabled)

        requestedMachineImageView.loadImage(requestedMachine?.thumbnailUrl, placeholder = R.drawable.img_machines_contour)
        requestedMachineImageView.imageTintList = if (requestedMachine?.thumbnailUrl != null) null else resources.getColorStateList(R.color.material_icon_disabled)

        receivedRentalTypeTextView.text = rental.rentalType
        receivedMachineTypeTextView.text = machineType
        receivedMachineAccessoryImageView.loadImage(if (isMachineAvailable) R.drawable.ic_chevron_right else R.drawable.ic_info_outline)
        receivedMachineUnavailableTextView.text = getString(R.string.rental_asset_not_available_in_app, "${rental.rentalType} $localCapitalizedMachineType")

        requestedRentalTypeTextView.text = requestedMachineDetail?.itemRequested
        requestedMachineTypeTextView.text = requestedMachineType
        requestedMachineAccessoryImageView.loadImage(if (isRequestedMachineAvailable) R.drawable.ic_chevron_right else R.drawable.ic_info_outline)
        requestedMachineUnavailableTextView.text = getString(R.string.rental_asset_not_available_in_app, "${rental.rentalType} $localCapitalizedRequestedMachineType")

        orderSpecificationsRecyclerView.notifyDataSetChanged()
        toggleExpansionButton.text = if (isOrderDetailsExpanded) getString(R.string.button_less) else getString(R.string.button_more)

        chatButton.isVisible = isChatEnabled
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalContactNumber


        if (!hasLocationMarker) {
            if (hasMachineLocation) {
                locationMarker = addLocationMarkerForMachineIfPossible(machine, rental)
            } else if (hasProjectLocation) {
                locationMarker = addLocationMarkerForProjectIfPossible(rental)
            }
        }

        if (hasLocationMarker) {
            locationMarker!!.showInfoWindow()
            mapView.zoomToLocation(latLng = locationMarker!!.position, zoom = 14f)
        }

        mapSection.isVisible = hasLocationMarker
    }


    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onToggleExpansionButtonClicked() {
        isOrderDetailsExpanded = !isOrderDetailsExpanded
        updateUI()
    }


    /*--------------------------------------- ProjectsView ---------------------------------------*/


    override fun askForComments(callback: (comments: String) -> Unit) = runOnUiThread {
        RentalDialogs.commentsDialog(this, callback).show()
    }


    override fun navigateToMachineDetailsPage(prepareHandler: ControllerPreparationHandler) {
        startActivity(MachineDetailViewImpl::class, controllerPreparationHandler = prepareHandler)
    }

    override fun navigateToChangeRequestPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(ChangeRequestFormViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
    }

    override fun navigateToReportBreakdownPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(ReportBreakdownViewImpl::class, controllerPreparationHandler = preparationHandler, transition = ModalPushActivityTransition)
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
                viewHolder.keyValue = orderDetails[indexPath.row]

            }

            else -> {

                viewHolder as MaterialRowViewHolder

                val action = actions[indexPath.row]
                val isEnabled = when (action) {
                    OFF_RENT -> canStopRenting
                    CANCEL_RENT -> canCancelRenting
                    REPORT_BREAKDOWN -> canRequestBreakdown
                    else -> canRequestChanges
                }

                viewHolder.setTitle(action.nameRes)
                viewHolder.setImage(action.iconRes, SQUARE_SMALL)
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
            OFF_RENT -> delegate.onStopRentingSelected()
            CANCEL_RENT -> delegate.onCancelRentingSelected()
            REQUEST_CHANGES -> delegate.onRequestChangesSelected()
            REPORT_BREAKDOWN -> delegate.onReportBreakdownSelected()
        }
    }


    /*------------------------------ Easy RecyclerView Data Source -------------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return when (recyclerView) {
            orderSpecificationsRecyclerView -> orderDetails.size
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
        return orderDetails[indexPath.row].first.hashCode().toLong()
    }



    /*------------------------------------- MapView delegate -------------------------------------*/


    override fun onMapViewFinishedLoading(mapView: MapView) {
        super.onMapViewFinishedLoading(mapView)

        mapView.setInfoWindowAdapter(MapInfoWindowAdapter(layoutInflater))

        updateUI()
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun addLocationMarkerForMachineIfPossible(machine: Machine?, rental: Rental): Marker? {
        val machineType = machine?.localizedMachineType(this) ?: rental.machineType
        return addLocationMarkerIfPossible(
                coordinate = machineCoordinate!!,
                title = rental.rentalType,
                snippet = machineType
        )
    }

    private fun addLocationMarkerForProjectIfPossible(rental: Rental) = addLocationMarkerIfPossible(
            coordinate = projectCoordinate!!,
            title = rental.project.name,
            snippet = rental.project.address
    )

    private fun addLocationMarkerIfPossible(coordinate: Coordinate, title: String?, snippet: String?) = mapView
            .addMarker(MarkerOptions()
                    .position(coordinate.toLatLng())
                    .title(title)
                    .snippet(snippet)
            )

    private fun navigateBack() = runOnUiThread {
        val transition = if (isPresentedModally) ModalPopActivityTransition else PopActivityTransition
        finish(transition = transition)
    }

    private fun showRentalDeskContactsSheet() {

        val phoneNumbers = rentalDeskContacts.map { it.phoneNumber }.distinct()
        val emailAddresses = rentalDeskContacts.mapNotNull { it.email }.distinct()
        val hasMultiplePhoneNumbers = phoneNumbers.size > 1
        val hasMultipleEmailAddresses = emailAddresses.size > 1

        // In most cases, there is only 1 phone number and e-mail address, so we only display "Call"
        // and "Email" as the contact options, but when there is more than 1 unique phone number or
        // email address, we disambiguate by also appending the name.

        val callActions = phoneNumbers.map { phoneNumber ->
            val contactName = rentalDeskContacts.first { it.phoneNumber == phoneNumber }.name
            val callTitle = getString(R.string.call) + if (hasMultiplePhoneNumbers) " $contactName" else ""
            ActionSheet.Action(R.drawable.ic_phone, callTitle, handler = { call(phoneNumber) })
        }
        val phoneActions = emailAddresses.map { emailAddress ->
            val contactName = rentalDeskContacts.first { it.email == emailAddress }.name
            val emailTitle = getString(R.string.email) + if (hasMultipleEmailAddresses) " $contactName" else ""
            ActionSheet.Action(R.drawable.ic_mail, emailTitle, handler = { email(emailAddress) })
        }

        contactOptionsActionSheet.actions = callActions + phoneActions
        contactOptionsActionSheet.show(contentView)

    }

    private fun call(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    private fun email(emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$emailAddress"))
        startActivity(Intent.createChooser(intent, null))
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    enum class Action(@StringRes val nameRes: Int, @DrawableRes val iconRes: Int) {
        CANCEL_RENT(R.string.cancel_rent_button, R.drawable.ic_remove_circle),
        OFF_RENT(R.string.off_rent_button, R.drawable.ic_remove_circle),
        REQUEST_CHANGES(R.string.request_changes, R.drawable.ic_edit),
        REPORT_BREAKDOWN(R.string.rental_detail_report_breakdown_button, R.drawable.ic_breakdown)
    }

}