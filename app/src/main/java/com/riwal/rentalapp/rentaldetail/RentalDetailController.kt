package com.riwal.rentalapp.rentaldetail

import android.location.Address
import android.location.Geocoder
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.extensions.datetime.isFuture
import com.riwal.rentalapp.common.extensions.rxjava.plusAssign
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.machinedetail.MachineDetailController
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.model.api.BackendClient
import com.riwal.rentalapp.offrentpanel.OffRentPanelController
import com.riwal.rentalapp.offrentpanel.OffRentPanelController.Style.SINGLE_MACHINE
import com.riwal.rentalapp.reportbreakdownform.ReportBreakdownFormController
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import org.joda.time.LocalDateTime
import java.io.IOException

class RentalDetailController(
        val view: RentalDetailView,
        val customer: Customer,
        val activeCountry: Country,
        val rentalManager: RentalManager,
        val orderManager: OrderManager,
        val machinesManager: MachinesManager,
        val backend: BackendClient,
        val chatManager: ChatManager,
        val geocoder: Geocoder,
        val analytics: RentalAnalytics
) : ViewLifecycleObserver, RentalDetailView.DataSource, RentalDetailView.Delegate, OffRentPanelController.Delegate, ChangeRequestFormController.Delegate, ReportBreakdownFormController.Delegate, MachinesManager.Observer, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    lateinit var rental: Rental
    var rentalDetail = RentalDetail()
    var isViewPresentedModally = false

    private var isLoadingMachine = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private val isReceivedMachineDifferent get() = !rentalDetail.itemReceived.isNullOrBlank()
            && !rentalDetail.itemRequested.isNullOrBlank() && rentalDetail.itemRequested != rentalDetail.itemReceived

    private var isLoadingRequestedMachine = false
        set(value) {
            field = value
            view.notifyDataChanged()
        }

    private val machine
        get() = rental.machine

    private val subscriptions = CompositeDisposable()

    private val canStopRenting
        get() = !canCancelRenting && rental.status in listOf(UNKNOWN, ON_RENT)

    private val canCancelRenting
        get() = rental.onRentDateTime.isFuture && rental.status in listOf(UNKNOWN, PENDING_DELIVERY)

    private val shouldShowInspectionDate
        get() = rental.status != CLOSED && rental.lastInspectionDate != null

    private val canRequestChanges
        get() = rental.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT)

    private val canSendBreakdownReport
        get() = customer.canReportBreakdown && rental.status == ON_RENT && activeCountry.isBreakdownReportingEnabled

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {

        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewAppear() {
        super.onViewAppear()

        // Apparently there is a non-documented quotum: https://hackernoon.com/wtf-android-sdk-part-iii-geocoder-aphasia-3bb8b9b6016b
        // So we only geocode when needed
        if (rental.machineCoordinate == null || rental.status.isClosed) {
            geocodeProjectAddress()
        }

        subscriptions += chatManager.observableNumberOfUnreadMessages.subscribe {
            view.notifyDataChanged()
        }

        isLoadingMachine = rental.machine == null
        isLoadingRequestedMachine = rentalDetail.requestedMachine == null
        machinesManager.addObserver(this)
        getRentalDetail(rental.id)
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        subscriptions.dispose()
        machinesManager.removeObserver(this)
    }

    override fun onViewSave(state: BetterBundle) {
        super.onViewSave(state)
        state["rental"] = rental
    }

    override fun onViewRestore(savedState: BetterBundle) {
        super.onViewRestore(savedState)
        rental = savedState["rental"]!!
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*--------------------------------- RentalDetailView DataSource ------------------------------*/


    override fun rental(view: RentalDetailView) = rental
    override fun rentalDeskContacts(view: RentalDetailView) = activeCountry.rentalDeskContactInfo
    override fun canStopRenting(view: RentalDetailView) = canStopRenting
    override fun canCancelRenting(view: RentalDetailView): Boolean = canCancelRenting
    override fun canRequestChanges(view: RentalDetailView) = canRequestChanges
    override fun isLoadingMachine(view: RentalDetailView) = isLoadingMachine
    override fun isViewPresentedModally(view: RentalDetailView) = isViewPresentedModally
    override fun isChatEnabled(view: RentalDetailView) = chatManager.isChatEnabled
    override fun shouldShowLastInspectionDate(view: RentalDetailView) = shouldShowInspectionDate
    override fun numberOfUnreadMessages(view: RentalDetailView) = chatManager.numberOfUnreadMessages
    override fun canSendBreakdownReport(view: RentalDetailView) = canSendBreakdownReport
    override fun rentalDetail(view: RentalDetailView) = rentalDetail
    override fun isReceivedMachineDifferent(view: RentalDetailView) = isReceivedMachineDifferent
    override fun isLoadingRequestedMachine(view: RentalDetailView) = isLoadingRequestedMachine
    override fun isPhoneCallEnable(view: RentalDetailView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: RentalDetailView): List<ContactInfo> = activeCountry.contactInfo

    /*---------------------------------- RentalDetailView Delegate -------------------------------*/


    override fun onMachineSelected(isReceivedMachine: Boolean) {
        if (isReceivedMachine) {
            analytics.trackRentAgainEvent(rentalDetail.requestedMachine!!)
            analytics.rentAgainSelected(rentalDetail.requestedMachine!!)
            view.navigateToMachineDetailsPage { destination ->
                val controller = destination as MachineDetailController
                controller.machine = rentalDetail.requestedMachine!!
            }
        } else {
            analytics.trackRentAgainEvent(machine!!)
            analytics.rentAgainSelected(machine!!)
            view.navigateToMachineDetailsPage { destination ->
                val controller = destination as MachineDetailController
                controller.machine = machine!!
            }
        }
    }

    override fun onStopRentingSelected() {
        analytics.offRentSelected(rental)
        view.showOffRentPanel { destination ->
            val controller = destination as OffRentPanelController
            controller.style = SINGLE_MACHINE
            controller.delegate = this
        }
    }

    override fun onCancelRentingSelected() {
        view.askForComments { comments ->
            analytics.confirmOffRentSelected()
            launch { rentalManager.requestCancelRent(listOf(rental), rental.onRentDateTime, comments, customer, activeCountry) }
        }
    }

    override fun onRequestChangesSelected() {
        view.navigateToChangeRequestPage { destination ->
            val controller = destination as ChangeRequestFormController
            controller.delegate = this
            controller.setOriginalRental(rental)
        }
    }

    override fun onReportBreakdownSelected() {
        view.navigateToReportBreakdownPage { destination ->
            val controller = destination as ReportBreakdownFormController
            controller.delegate = this
            controller.setRental(rental)
        }

    }


    /*------------------------------- OffRentPanelController Delegate ------------------------------*/


    override fun onOffRentDateConfirmed(controller: OffRentPanelController, offRentDateTime: LocalDateTime, isAvailableForPickup: String?) {
        view.askForComments { comments ->
            analytics.confirmOffRentSelected()
            launch { rentalManager.requestOffRent(listOf(rental), offRentDateTime, comments, customer, activeCountry, isAvailableForPickup) }
        }
    }


    /*-------------------------- ChangeRequestFormController.Delegate ----------------------------*/


    override fun onRentalChanged(controller: ChangeRequestFormController, originalRental: Rental, changedRental: Rental, comments: String) {
        launch {
            rentalManager.requestChange(originalRental, changedRental, comments, customer, activeCountry)
        }
    }

    override fun onAccessoriesChanged(controller: ChangeRequestFormController, originalAccessories: AccessoriesOnRent, changedAccessories: AccessoriesOnRent, comments: String) { }


    /*-------------------------- ReportBreakdownFormController.Delegate ----------------------------*/


    override fun onBreakdownReportFinished(controller: ReportBreakdownFormController, breakdownMessage: String, contactPhoneNumber: String?) {
        launch {
            rentalManager.reportBreakdown(rental, breakdownMessage, customer, activeCountry, contactPhoneNumber)
        }
    }


    /*---------------------------------- MachinesManager Observer --------------------------------*/


    override fun onMachinesUpdated(machinesManager: MachinesManager, machines: List<Machine>) {
        rental = rental.copy(machine = machinesManager.machineForRental(rental))
        isLoadingMachine = false
    }


    /*--------------------------------------- Private methods ------------------------------------*/


    private fun geocodeProjectAddress() = launch {
        try {

            val addresses: List<Address>? = withContext(Dispatchers.IO) {
                // According to the documentation, getFromLocationName can return null or an empty list.
                geocoder.getFromLocationName(rental.project.address!!, 1)
            }
            val address = addresses?.firstOrNull()
            val projectCoordinate = if (address != null) Coordinate(address.latitude, address.longitude) else null

            rental = rental.copy(project = rental.project.copy(coordinate = projectCoordinate))
            view.notifyDataChanged()

        } catch (error: IOException) {
            error.printStackTrace()
        } catch (error: IllegalStateException) {
            // This error will be thrown when the latitude or longitude is null
            error.printStackTrace()
        }
    }

    private fun getRentalDetail(inventTransId: String) = launch {
        try {
            rentalDetail = rentalManager.getRentalDetail(customer = customer, inventTransId = inventTransId)
            rentalDetail = rentalDetail.copy(requestedMachine = machinesManager.machineForRental(rentalDetail))
            isLoadingRequestedMachine = false

        } catch (error: Exception) {
            isLoadingRequestedMachine = false
            error.printStackTrace()
        }
    }

}