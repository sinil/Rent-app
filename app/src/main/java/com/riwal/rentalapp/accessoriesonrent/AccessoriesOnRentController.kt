package com.riwal.rentalapp.accessoriesonrent

import android.content.res.Resources
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.accessoriesonrentdetail.AccessoryOnRentDetailController
import com.riwal.rentalapp.common.extensions.core.months
import com.riwal.rentalapp.common.extensions.core.toggle
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.extensions.rxjava.debounce
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.RentalStatus.*
import com.riwal.rentalapp.offrentpanel.OffRentPanelController
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

class AccessoriesOnRentController(
        val view: AccessoriesOnRentView,
        val customer: Customer,
        val activeCountry: Country,
        val rentalManager: RentalManager,
        val analytics: RentalAnalytics,
        val chatManager: ChatManager,
        val resources: Resources
) : ViewLifecycleObserver, AccessoriesOnRentView.Delegate, AccessoriesOnRentView.DataSource, OffRentPanelController.Delegate, CoroutineScope by MainScope() {

    /*--------------------------------------- Properties -----------------------------------------*/

    private var accessories: List<AccessoriesOnRent> = emptyList()
    private var filteraccessories: List<AccessoriesOnRent> = emptyList()
    private val asyncFilter: PublishSubject<FilterAndInput> = PublishSubject.create()
    private val defaultFilter = AccessoriesOnRentFilter(
            query = "",
            period = (LocalDate.now() - 1.months())..(LocalDate.now() + 1.months()),
            selectedRentalStatuses = emptyList(),
            relevantRentalStatuses = RentalStatus.allExceptUnknown
    )
    private var filter = defaultFilter
    private var isFiltering = false
    private var hasFailedLoadingAccessories = false
    private var isUpdatingAccessories = false
    private var isInSelectionMode = false
    private var selectedAccessories: List<AccessoriesOnRent> = emptyList()
    private var filterSubscription = CompositeDisposable()
    private var messageSubscription = CompositeDisposable()

    private val viewCanChangePeriod = true

    private val changeableAccessoriesStatusesForView = RentalStatus.allExceptUnknown

    private val filterForView
        get() = filter.copy(
                relevantRentalStatuses = changeableAccessoriesStatusesForView,
                period = filter.period
        )


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        getAccessoriesFromServerThenFilter(dateRange = filter.period)
        setUpAsyncRentalsFilter()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingAccessoriesOnRent()
        observeNumberOfUnreadChatMessages()
    }

    override fun onViewDisappear() {
        super.onViewDisappear()
        messageSubscription.dispose()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        filterSubscription.dispose()
        cancel()
    }


    /*--------------------------------- AccessoriesOnRent DataSource -----------------------------*/


    override fun accessoriesOnRent(view: AccessoriesOnRentView) = filteraccessories
    override fun isChatEnable(view: AccessoriesOnRentView) = chatManager.isChatEnabled
    override fun isPhoneCallEnable(view: AccessoriesOnRentView): Boolean = activeCountry.isPhoneCallEnable
    override fun activeCountry(view: AccessoriesOnRentView): Country = activeCountry
    override fun canChangePeriod(view: AccessoriesOnRentView) = viewCanChangePeriod
    override fun changeableOrderStatuses(view: AccessoriesOnRentView) = changeableAccessoriesStatusesForView
    override fun canStopRenting(view: AccessoriesOnRentView, accessories: AccessoriesOnRent) = canStopRent(accessories)
    override fun numberOfUnreadMessages(view: AccessoriesOnRentView): Int = chatManager.numberOfUnreadMessages
    override fun hasFailedLoadingAccessories(view: AccessoriesOnRentView): Boolean = hasFailedLoadingAccessories
    override fun isUpdatingAccessories(view: AccessoriesOnRentView): Boolean = isUpdatingAccessories
    override fun selectedAccessories(view: AccessoriesOnRentView): List<AccessoriesOnRent> = selectedAccessories
    override fun isInSelectionMode(view: AccessoriesOnRentView): Boolean = isInSelectionMode
    override fun rentalDeskContactInfo(view: AccessoriesOnRentView): List<ContactInfo> = activeCountry.rentalDeskContactInfo
    override fun filter(view: AccessoriesOnRentView): AccessoriesOnRentFilter = filterForView


    /*--------------------------------- AccessoriesOnRent Delegate -------------------------------*/


    override fun onAccessoriesSelected(view: AccessoriesOnRentView, accessoriesOnRent: AccessoriesOnRent) {

        if (isInSelectionMode && !canStopRent(accessoriesOnRent)) {
            return
        } else if (isInSelectionMode) {
            selectedAccessories = selectedAccessories.toggle(accessoriesOnRent)
            view.notifyDataChanged()
        } else {
            view.navigateToAccessoriesDetailsPage { destination ->
                val controller = destination as AccessoryOnRentDetailController
                controller.accessoriesOnRent = accessoriesOnRent
            }
        }

    }

    override fun onRetryLoadingAccessoriesSelected(view: AccessoriesOnRentView) {
        getAccessoriesFromServerThenFilter(dateRange = filter.period)
    }

    override fun onEnableSelectionModeSelected(view: AccessoriesOnRentView) {
        isInSelectionMode = true
        view.notifyDataChanged()
    }

    override fun onCancelSelectionModeSelected(view: AccessoriesOnRentView) {
        cancelSelectionMode()
    }

    override fun onStopRentingSelected(view: AccessoriesOnRentView) {
        view.showOffRentPanel { destination ->
            val controller = destination as OffRentPanelController
            controller.style = OffRentPanelController.Style.MULTIPLE_MACHINES
            controller.delegate = this
        }
    }

    override fun onFilterChanged(view: AccessoriesOnRentView, newFilter: AccessoriesOnRentFilter) {

        val oldFilter = filter.copy()
        filter = newFilter

        if (oldFilter.period != newFilter.period) {
            getAccessoriesFromServerThenFilter(dateRange = newFilter.period)
        } else {
            filterRentalsForView()
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
                    filteraccessories = it
                    isFiltering = false
                    view.notifyDataChanged()
                }
                .disposedBy(filterSubscription)
    }

    private fun canStopRent(accessories: AccessoriesOnRent) = accessories.status in listOf(UNKNOWN, PENDING_DELIVERY, ON_RENT)

    private fun observeNumberOfUnreadChatMessages() {
        messageSubscription = CompositeDisposable()
        chatManager
                .observableNumberOfUnreadMessages
                .subscribe { view.notifyDataChanged() }
                .disposedBy(messageSubscription)
    }

    private fun cancelSelectionMode() {
        isInSelectionMode = false
        selectedAccessories = emptyList()
        view.notifyDataChanged()
    }

    private fun filterRentalsForView() {
        val filterAndInput = FilterAndInput(filter = filterForView, input = accessories)
        asyncFilter.onNext(filterAndInput)
    }

    private fun getAccessoriesFromServerThenFilter(dateRange: ClosedRange<LocalDate>) = launch {

        isUpdatingAccessories = true
        view.notifyDataChanged()

        try {
            accessories = rentalManager.getAccessoriesOnRent(dateRange, customer)
            hasFailedLoadingAccessories = false
        } catch (error: Exception) {
            accessories = emptyList()
            filteraccessories = emptyList()
            hasFailedLoadingAccessories = true
        }

        filterRentalsForView()

        isUpdatingAccessories = false
        view.notifyDataChanged()
    }


    /*------------------------------------ Private extensions ------------------------------------*/


    private fun List<AccessoriesOnRent>.sortedByStatusThenByOnRentDateTime(): List<AccessoriesOnRent> {
        val statusOrdering = listOf(ON_RENT, PENDING_DELIVERY, PENDING_PICKUP, CLOSED, UNKNOWN)
        return this.sortedWith(compareBy<AccessoriesOnRent> { statusOrdering.indexOf(it.status) }.thenByDescending { it.onRentDateTime })
    }

    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    data class FilterAndInput(val filter: AccessoriesOnRentFilter, val input: List<AccessoriesOnRent>)


    /*------------------------------ OffRentPanelController Delegate -----------------------------*/


    override fun onOffRentDateConfirmed(controller: OffRentPanelController, offRentDateTime: LocalDateTime, isAvailableForPickup: String?) {
        view.showCommentsDialog { comments ->
            analytics.confirmOffRentSelected()
            launch {
                rentalManager.requestOffRentAccessories(selectedAccessories, offRentDateTime, comments, customer, activeCountry, isAvailableForPickup)
                cancelSelectionMode()
            }
        }
    }

}