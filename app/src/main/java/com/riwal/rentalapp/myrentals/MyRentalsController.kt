package com.riwal.rentalapp.myrentals

import android.content.res.Resources
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.months
import com.riwal.rentalapp.common.extensions.core.toggle
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.extensions.rxjava.debounce
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.myrentals.MyRentalsResultsFormat.LIST
import com.riwal.rentalapp.myrentals.MyRentalsResultsFormat.MAP
import com.riwal.rentalapp.offrentpanel.OffRentPanelController
import com.riwal.rentalapp.offrentpanel.OffRentPanelController.Style.MULTIPLE_MACHINES
import com.riwal.rentalapp.rentaldetail.RentalDetailController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.LocalDate.now
import org.joda.time.LocalDateTime

class MyRentalsController(
        val view: MyRentalsView,
        val customer: Customer,
        val activeCountry: Country,
        private val rentalManager: RentalManager,
        private val chatManager: ChatManager,
        val analytics: RentalAnalytics,
        val resources: Resources
) : ViewLifecycleObserver, MyRentalsView.DataSource, MyRentalsView.Delegate, OffRentPanelController.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var rentals: List<Rental> = emptyList()
    private var filteredRentals: List<Rental> = emptyList()
    private val asyncFilter: PublishSubject<FilterAndInput> = PublishSubject.create()
    private val defaultFilter = MyRentalsFilter(
            query = "",
            period = (now() - 1.months())..(now() + 1.months()),
            selectedRentalStatuses = emptyList(),
            relevantRentalStatuses = RentalStatus.allExceptUnknown
    )
    private var filter = defaultFilter
    private var resultsFormat = LIST
    private var hasFailedLoadingRentals = false
    private var isFiltering = false
    private var isUpdatingRentals = false
    private var isInSelectionMode = false
    private var selectedRentals: List<Rental> = emptyList()
    private var filterSubscription = CompositeDisposable()
    private var messagesSubscription = CompositeDisposable()

    private val viewCanChangePeriod
        get() = resultsFormat != MAP

    private val changeableRentalStatusesForView: List<RentalStatus>
        get() {
            val all = RentalStatus.allExceptUnknown
            return if (resultsFormat == MAP) all - listOf(PENDING_DELIVERY, CLOSED) else all
        }

    private val filterForView
        get() = filter.copy(
                relevantRentalStatuses = changeableRentalStatusesForView,
                period = if (resultsFormat == MAP) defaultFilter.period else filter.period
        )


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        getRentalsFromServerThenFilter(dateRange = filter.period)
        setUpAsyncRentalsFilter()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.userLookingAtMyRentals()
        observeNumberOfUnreadChatMessages()
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        messagesSubscription.dispose()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        filterSubscription.dispose()
        cancel()
    }


    /*--------------------------------- OrderListView DataSource ---------------------------------*/


    override fun rentals(view: MyRentalsView) = filteredRentals
    override fun filter(view: MyRentalsView) = filterForView
    override fun canChangePeriod(view: MyRentalsView) = viewCanChangePeriod
    override fun changeableOrderStatuses(view: MyRentalsView) = changeableRentalStatusesForView
    override fun resultsFormat(view: MyRentalsView) = resultsFormat
    override fun activeCountry(view: MyRentalsView) = activeCountry
    override fun isUpdatingRentals(view: MyRentalsView) = isUpdatingRentals
    override fun isFilteringRentals(view: MyRentalsView) = isFiltering
    override fun canStopRenting(view: MyRentalsView, rental: Rental) = canStopRenting(rental)
    override fun isInSelectionMode(view: MyRentalsView) = isInSelectionMode
    override fun selectedRentals(view: MyRentalsView) = selectedRentals
    override fun isChatEnabled(view: MyRentalsView) = chatManager.isChatEnabled
    override fun numberOfUnreadMessages(view: MyRentalsView) = chatManager.numberOfUnreadMessages
    override fun hasFailedLoadingRentals(view: MyRentalsView) = hasFailedLoadingRentals
    override fun isPhoneCallEnable(view: MyRentalsView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: MyRentalsView): List<ContactInfo> = activeCountry.rentalDeskContactInfo


    /*---------------------------------- MyRentalsView Delegate ----------------------------------*/


    override fun onRetryLoadingRentalsSelected(view: MyRentalsView) {
        getRentalsFromServerThenFilter(dateRange = filter.period)
    }

    override fun onRefreshSelected(view: MyRentalsView) {
        getRentalsFromServerThenFilter(dateRange = filter.period)
    }

    override fun onFilterChanged(view: MyRentalsView, newFilter: MyRentalsFilter) {

        val oldFilter = filter.copy()
        filter = newFilter

        if (oldFilter.period != newFilter.period) {
            getRentalsFromServerThenFilter(dateRange = newFilter.period)
        } else {
            filterRentalsForView()
        }
    }

    override fun onRentalSelected(view: MyRentalsView, rental: Rental) {

        if (isInSelectionMode && !canStopRenting(rental)) {
            return
        } else if (isInSelectionMode) {
            selectedRentals = selectedRentals.toggle(rental)
            view.notifyDataChanged()
        } else {
            view.navigateToRentalDetailsPage { destination ->
                val controller = destination as RentalDetailController
                controller.rental = rental
            }
        }
    }

    override fun onResultsFormatChanged(view: MyRentalsView, newResultsFormat: MyRentalsResultsFormat) {
        resultsFormat = newResultsFormat
        if (resultsFormat == MAP)
            analytics.userLookingAtMyRentalsMapView()
        filterRentalsForView()
        view.notifyDataChanged()
    }

    override fun onEnableSelectionModeSelected(view: MyRentalsView) {
        isInSelectionMode = true
        view.notifyDataChanged()
    }

    override fun onCancelSelectionModeSelected(view: MyRentalsView) {
        cancelSelectionMode()
    }

    override fun onStopRentingSelected(view: MyRentalsView) {
        analytics.offRentMultipleSelected(selectedRentals)
        view.showOffRentPanel { destination ->
            val controller = destination as OffRentPanelController
            controller.style = MULTIPLE_MACHINES
            controller.delegate = this
        }
    }


    /*------------------------------ OffRentPanelController Delegate -----------------------------*/


    override fun onOffRentDateConfirmed(controller: OffRentPanelController, offRentDateTime: LocalDateTime, isAvailableForPickup: String?) {
        view.showCommentsDialog { comments ->
            analytics.confirmOffRentSelected()
            launch {
                rentalManager.requestOffRent(selectedRentals, offRentDateTime, comments, customer, activeCountry, isAvailableForPickup)
                cancelSelectionMode()
            }
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun setUpAsyncRentalsFilter() {
        asyncFilter
                .doOnNext {
                    isFiltering = true
                    view.notifyDataChanged()
                }
                .debounce(0.3.seconds())
                .observeOn(Schedulers.computation())
                .map { it.filter.applyTo(it.input, resources) }
                .map { it.sortedByStatusThenByOnRentDateTime() }
                .subscribe {
                    filteredRentals = it
                    isFiltering = false
                    view.notifyDataChanged()
                }
                .disposedBy(filterSubscription)
    }

    private fun observeNumberOfUnreadChatMessages() {
        messagesSubscription = CompositeDisposable()
        chatManager
                .observableNumberOfUnreadMessages
                .subscribe { view.notifyDataChanged() }
                .disposedBy(messagesSubscription)
    }

    private fun canStopRenting(rental: Rental) = rental.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT)

    private fun getRentalsFromServerThenFilter(dateRange: ClosedRange<LocalDate>) = launch {

        isUpdatingRentals = true
        view.notifyDataChanged()

        try {
            rentals = rentalManager.getRentals(dateRange, customer)
            hasFailedLoadingRentals = false
        } catch (error: Exception) {
            rentals = emptyList()
            filteredRentals = emptyList()
            hasFailedLoadingRentals = true
        }

        filterRentalsForView()

        isUpdatingRentals = false
        view.notifyDataChanged()
    }

    private fun filterRentalsForView() {
        val filterAndInput = FilterAndInput(filter = filterForView, input = rentals)
        asyncFilter.onNext(filterAndInput)
    }

    private fun cancelSelectionMode() {
        isInSelectionMode = false
        selectedRentals = emptyList()
        view.notifyDataChanged()
    }


    /*------------------------------------ Private extensions ------------------------------------*/


    private fun List<Rental>.sortedByStatusThenByOnRentDateTime(): List<Rental> {
        val statusOrdering = listOf(ON_RENT, PENDING_DELIVERY, PENDING_PICKUP, CLOSED, UNKNOWN)
        return this.sortedWith(compareBy<Rental> { statusOrdering.indexOf(it.status) }.thenByDescending { it.onRentDateTime })
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    data class FilterAndInput(val filter: MyRentalsFilter, val input: List<Rental>)

}
