package com.riwal.rentalapp.accessoriesonrentdetail

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.BetterBundle
import com.riwal.rentalapp.common.extensions.datetime.isFuture
import com.riwal.rentalapp.common.extensions.rxjava.plusAssign
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.offrentpanel.OffRentPanelController
import com.riwal.rentalapp.requestchangesform.ChangeRequestFormController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.joda.time.LocalDateTime

class AccessoryOnRentDetailController(
        val view: AccessoryOnRentDetailView,
        val customer: Customer,
        val activeCountry: Country,
        val rentalManager: RentalManager,
        val orderManager: OrderManager,
        val chatManager: ChatManager,
        val analytics: RentalAnalytics
) : ViewLifecycleObserver, AccessoryOnRentDetailView.Datasource, AccessoryOnRentDetailView.Delegate, OffRentPanelController.Delegate, ChangeRequestFormController.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    lateinit var accessoriesOnRent: AccessoriesOnRent
    private val subscriptions = CompositeDisposable()

    private val canStopRenting
        get() = !canCancelRenting && accessoriesOnRent.status in listOf(UNKNOWN, ON_RENT)

    private val canCancelRenting
        get() = accessoriesOnRent.onRentDateTime.isFuture && accessoriesOnRent.status in listOf(UNKNOWN, PENDING_DELIVERY)

    private val shouldShowInspectionDate
        get() = accessoriesOnRent.status != CLOSED && accessoriesOnRent.lastInspectionDate != null

    private val canRequestChanges
        get() = accessoriesOnRent.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }


    override fun onViewAppear() {
        super.onViewAppear()

        subscriptions += chatManager.observableNumberOfUnreadMessages.subscribe {
            view.notifyDataChanged()
        }
    }


    override fun onViewDisappear() {
        super.onViewDisappear()
        subscriptions.dispose()
    }

    override fun onViewSave(state: BetterBundle) {
        super.onViewSave(state)
        state["accessoriesOnRent"] = accessoriesOnRent
    }

    override fun onViewRestore(savedState: BetterBundle) {
        super.onViewRestore(savedState)
        accessoriesOnRent = savedState["accessoriesOnRent"]!!
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        cancel()
    }


    /*-------------------------- AccessoriesOnRentDetails.DataSource -----------------------------*/


    override fun accessoriesOnRent(view: AccessoryOnRentDetailView): AccessoriesOnRent = accessoriesOnRent
    override fun canStopRenting(view: AccessoryOnRentDetailView): Boolean = canStopRenting
    override fun canCancelRenting(view: AccessoryOnRentDetailView): Boolean = canCancelRenting
    override fun canRequestChanges(view: AccessoryOnRentDetailView): Boolean = canRequestChanges
    override fun isChatEnabled(view: AccessoryOnRentDetailView): Boolean = chatManager.isChatEnabled
    override fun shouldShowLastInspectionDate(view: AccessoryOnRentDetailView): Boolean = shouldShowInspectionDate
    override fun numberOfUnreadMessages(view: AccessoryOnRentDetailView): Int = chatManager.numberOfUnreadMessages
    override fun isPhoneCallEnable(view: AccessoryOnRentDetailView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: AccessoryOnRentDetailView): List<ContactInfo> = activeCountry.contactInfo


    /*-------------------------- AccessoriesOnRentDetails.Delegate -----------------------------*/


    override fun onStopRentingSelected() {
        analytics.offRentAccessoriesSelected(accessoriesOnRent)
        view.showOffRentPanel { destination ->
            val controller = destination as OffRentPanelController
            controller.style = OffRentPanelController.Style.SINGLE_MACHINE
            controller.delegate = this
        }
    }

    override fun onCancelRentingSelected() {
        view.askForComments { comments ->
            analytics.confirmOffRentSelected()
            launch { rentalManager.requestCancelAccessories(listOf(accessoriesOnRent), accessoriesOnRent.onRentDateTime, comments, customer, activeCountry) }
        }
    }

    override fun onRequestChangesSelected() {
        view.navigateToChangeRequestPage { destination ->
            val controller = destination as ChangeRequestFormController
            controller.delegate = this
            controller.setOriginalRental(accessoriesOnRent)
        }
    }

    /*------------------------------- OffRentPanelController Delegate ------------------------------*/


    override fun onOffRentDateConfirmed(controller: OffRentPanelController, offRentDateTime: LocalDateTime, isAvailableForPickup: String?) {
        view.askForComments { comments ->
            analytics.confirmOffRentSelected()
            launch { rentalManager.requestOffRentAccessories(listOf(accessoriesOnRent), offRentDateTime, comments, customer, activeCountry, isAvailableForPickup) }
        }
    }


    /*-------------------------- ChangeRequestFormController.Delegate ----------------------------*/


    override fun onRentalChanged(controller: ChangeRequestFormController, originalRental: Rental, changedRental: Rental, comments: String) {}

    override fun onAccessoriesChanged(controller: ChangeRequestFormController, originalAccessories: AccessoriesOnRent, changedAccessories: AccessoriesOnRent, comments: String) {
        launch {
            rentalManager.requestChange(originalAccessories, changedAccessories, comments, customer, activeCountry)
        }
    }

}