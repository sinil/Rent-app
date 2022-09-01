package com.riwal.rentalapp.myrentals

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.Cluster
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.excludeTargets
import com.riwal.rentalapp.common.extensions.core.randomItem
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.common.ui.mapview.ClusterItem
import com.riwal.rentalapp.common.ui.mapview.MapView
import com.riwal.rentalapp.common.ui.mapview.memberItems
import com.riwal.rentalapp.common.ui.mapview.zoomToShowAllClusterItems
import com.riwal.rentalapp.model.Country
import com.riwal.rentalapp.model.Rental
import com.riwal.rentalapp.model.toLatLng
import com.riwal.rentalapp.model.util.bounds
import com.riwal.rentalapp.myrentals.MyRentalsResultsFormat.LIST
import com.riwal.rentalapp.myrentals.MyRentalsResultsFormat.MAP
import com.riwal.rentalapp.myrentals.RentalRowViewHolder.Style.NORMAL
import com.riwal.rentalapp.myrentals.RentalRowViewHolder.Style.SELECTION
import com.riwal.rentalapp.offrentpanel.OffRentPanelViewImpl
import com.riwal.rentalapp.rentaldetail.RentalDetailViewImpl
import kotlinx.android.synthetic.main.view_map_container.view.*
import kotlinx.android.synthetic.main.view_my_rentals.*


class MyRentalsViewImpl : RentalAppNotificationActivity(), MyRentalsView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MapView.Delegate, MyRentalsSearchBarWithFilterPanel.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: MyRentalsView.DataSource
    override lateinit var delegate: MyRentalsView.Delegate

    private var rentals: List<Rental> = emptyList()
    private var mapOnlyShowsGpsMachinesSnackbar: Snackbar? = null
    private lateinit var searchBarWithFilterPanel: MyRentalsSearchBarWithFilterPanel
    private var offRentPanel: OffRentPanelViewImpl? = null

    private var needsUpdateData = false

    private val isFilterPanelOpen
        get() = searchBarWithFilterPanel.isFilterPanelOpen

    private val filter
        get() = dataSource.filter(view = this)

    private val changeableOrderStatuses
        get() = dataSource.changeableOrderStatuses(view = this)

    private val canChangePeriod
        get() = dataSource.canChangePeriod(view = this)

    private val resultsFormat
        get() = dataSource.resultsFormat(view = this)

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val isUpdatingRentals
        get() = dataSource.isUpdatingRentals(view = this)

    private val isFilteringRentals
        get() = dataSource.isFilteringRentals(view = this)

    private val isInSelectionMode
        get() = dataSource.isInSelectionMode(view = this)

    private val selectedRentals
        get() = dataSource.selectedRentals(view = this)

    private val isRentalPanelOpen
        get() = offRentPanel?.isOpen ?: false

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val hasFailedLoadingRentals
        get() = dataSource.hasFailedLoadingRentals(view = this)

    private val titleForSelectionMode: String
        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
        get() = when {
            !isInSelectionMode -> getString(R.string.page_my_rentals)
            selectedRentals.isEmpty() -> getString(R.string.my_rentals_select_rental_order)
            selectedRentals.size == 1 -> getString(R.string.my_rentals_one_rental_order_selected, "${selectedRentals.size}")
            else -> getString(R.string.my_rentals_multiple_rental_orders_selected, "${selectedRentals.size}")
        }

    private var isMapViewRendered = false


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_my_rentals)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))

        searchBarWithFilterPanel = MyRentalsSearchBarWithFilterPanel(context = this, firstDayOfWeek = activeCountry.firstDayOfWeek)
        searchBarWithFilterPanel.delegate = this
        searchBarWithFilterPanel.filter = filter
        searchBarWithFilterPanel.changableOrderStatuses = changeableOrderStatuses
        searchBarWithFilterPanel.canChangePeriod = canChangePeriod

        searchAndFilterContainer.addView(searchBarWithFilterPanel)

        resultsRecyclerView.dataSource = this
        resultsRecyclerView.delegate = this

        mapView.delegate = this

        mapView.refreshButton.setOnClickListener { delegate.onRefreshSelected(view = this) }
        retryLoadingMyProjectsButton.setOnClickListener { delegate.onRetryLoadingRentalsSelected(view = this) }
        filterPanelOverlay.setOnClickListener { onFilterOverlayPressed() }
        listFormatButton.setOnClickListener { onResultsFormatChanged(LIST) }
        mapFormatButton.setOnClickListener { onResultsFormatChanged(MAP) }
        stopRentingButton.setOnClickListener { delegate.onStopRentingSelected(view = this) }

        updateUI(animated = false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun updateUI(animated: Boolean) {

        if (animated) {
            beginDelayedTransition(TransitionSet()
                    .addTransition(ParallelAutoTransition().excludeTargets(listOf(toolbar, selectionActionsToolbar), true))
                    .addTransition(Slide(Gravity.TOP).addTarget(toolbar))
                    .addTransition(Slide(Gravity.BOTTOM).addTarget(selectionActionsToolbar).excludeChildren(selectionActionsToolbar, true))
                    .excludeChildren(resultsRecyclerView, true))
        }

        val previousRentals = rentals

        updateDataIfNeeded()

        val period = filter.period
        val isRentalsChanged = previousRentals != rentals

        title = titleForSelectionMode

        resultsRecyclerView.notifyDataSetChanged()
        resultsRecyclerView.isVisible = resultsFormat == LIST && rentals.isNotEmpty() && !hasFailedLoadingRentals

        mapView.isVisible = resultsFormat == MAP && !hasFailedLoadingRentals
        if (!isFilteringRentals && isMapViewRendered) {
            showRentalsWithCoordinatesOnMap()
        }

        rentalResultsTextView.text = getString(R.string.search_results, rentals.size)
        rentalResultsPeriodTextView.text = period.toShortStyleString()

        filterPanelOverlay.isVisible = isFilterPanelOpen

        retryLoadingMyProjectsContainer.isVisible = !isUpdatingRentals && hasFailedLoadingRentals
        emptyMyRentalsView.isVisible = rentals.isEmpty() && !isUpdatingRentals && !isFilteringRentals && !hasFailedLoadingRentals
        activityIndicator.isVisible = isFilteringRentals || isUpdatingRentals
        mapView.refreshButton.isVisible = !isUpdatingRentals

        searchAndFilterContainer.isVisible = !isInSelectionMode
        resultsFormatOptionsContainerView.isVisible = !isInSelectionMode
        rentalResultsSummaryGroup.isVisible = !isInSelectionMode
        resultsRecyclerView.setPaddingTop(if (isInSelectionMode) dimen(R.dimen.app_bar_default_height) else 156.dp())
        resultsRecyclerView.setPaddingBottom(if (isInSelectionMode) dimen(R.dimen.app_bar_default_height) else 0.dp())
        toolbar.isVisible = isInSelectionMode

        selectionActionsToolbar.isVisible = isInSelectionMode
        stopRentingButton.isEnabled = selectedRentals.isNotEmpty()

        chatButton.isVisible = isChatEnabled && !isFilterPanelOpen && !isInSelectionMode
        chatButton.numberOfUnreadMessages = numberOfUnreadMessages

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalContactNumber

        if (isRentalsChanged) {
            resultsRecyclerView.scrollToPosition(0)
        }
    }


    /*----------------------------------------- Actions ------------------------------------------*/


    private fun onFilterOverlayPressed() = hideFilterPanel()

    private fun onResultsFormatChanged(resultsFormat: MyRentalsResultsFormat) {
        delegate.onResultsFormatChanged(view = this, newResultsFormat = resultsFormat)
        when (resultsFormat) {
            MAP -> {
                mapOnlyShowsGpsMachinesSnackbar = Snackbar.make(mapView, R.string.my_rentals_map_displays_only_gps_machines_message, Snackbar.LENGTH_LONG)
                mapOnlyShowsGpsMachinesSnackbar!!.show()
            }
            LIST -> mapOnlyShowsGpsMachinesSnackbar?.dismiss()
        }
        searchBarWithFilterPanel.changableOrderStatuses = changeableOrderStatuses
    }

    override fun onBackPressed() = when {
        isRentalPanelOpen -> offRentPanel!!.navigateBack()
        isInSelectionMode -> {
            delegate.onCancelSelectionModeSelected(view = this)
            updateUI()
        }
        isFilterPanelOpen -> hideFilterPanel()
        else -> navigateBack()
    }


    /*----------------------------------------- Methods ------------------------------------------*/


    override fun notifyDataChanged() {
        needsUpdateData = true
        super.notifyDataChanged()
    }

    override fun showOffRentPanel(preparationHandler: ControllerPreparationHandler) {
        offRentPanel = OffRentPanelViewImpl(this)
        offRentPanel!!.show(contentView, preparationHandler)
    }

    override fun showCommentsDialog(callback: (comments: String) -> Unit) = runOnUiThread {
        RentalDialogs.commentsDialog(this, callback).show()
    }

    override fun navigateToRentalDetailsPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(RentalDetailViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    override fun navigateBack() = runOnUiThread {
        finish()
    }


    /*----------------------- MyRentalsSearchBarWithFilterPanel Delegate -------------------------*/


    override fun onBackButtonPressed(view: MyRentalsSearchBarWithFilterPanel) = onBackPressed()
    
    override fun onFilterPanelToggled(view: MyRentalsSearchBarWithFilterPanel, isOpen: Boolean) {
        updateUI()
    }

    override fun onFilterChanged(view: MyRentalsSearchBarWithFilterPanel, newFilter: MyRentalsFilter) = delegate.onFilterChanged(view = this, newFilter = newFilter)


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = rentals.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_rental
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = RentalRowViewHolder(itemView)
    override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = rentals[indexPath.row].hashCode().toLong()


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val rental = rentals[indexPath.row]
        val canStopRenting = dataSource.canStopRenting(view = this, rental = rental)
        viewHolder as RentalRowViewHolder
        viewHolder.updateWith(rental)
        viewHolder.style = if (isInSelectionMode && canStopRenting) SELECTION else NORMAL
        viewHolder.isChecked = rental in selectedRentals
        viewHolder.itemView.setOnLongClickListener {
            if (!isInSelectionMode) {
                delegate.onEnableSelectionModeSelected(view = this)
                delegate.onRentalSelected(view = this, rental = rental)
                true
            } else {
                false
            }
        }
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val rental = rentals[indexPath.row]
        delegate.onRentalSelected(view = this, rental = rental)
    }


    /*-------------------------------- Map View Delegate methods ---------------------------------*/


    override fun onClusterSelected(mapView: MapView, cluster: Cluster<ClusterItem>) {
        mapView.zoomToShowClusterItems(cluster.memberItems)
    }

    override fun onClusterItemInfoWindowSelected(mapView: MapView, clusterItem: ClusterItem) {
        clusterItem as RentalClusterItem
        delegate.onRentalSelected(view = this, rental = clusterItem.rental)
    }

    override fun onMapViewFinishedRendering(mapView: MapView) {
        super.onMapViewFinishedRendering(mapView)
        isMapViewRendered = true
        updateUI(animated = false)
    }

    
    /*------------------------------------- Private methods --------------------------------------*/


    private fun updateDataIfNeeded() {

        if (!needsUpdateData) {
            return
        }

        rentals = dataSource.rentals(view = this)

        if (rentals.isNotEmpty() && searchBarWithFilterPanel.searchHint == null) {
            val searchHintValues = with(rentals.first()) { listOfNotNull(rentalType, orderNumber, project.name, project.contactName) }
            searchBarWithFilterPanel.searchHint = getString(R.string.hint_search_example, searchHintValues.randomItem)
        }

        needsUpdateData = false
    }

    private fun showRentalsWithCoordinatesOnMap() {

        val rentalsWithCoordinate = rentals.filter { it.machineCoordinate != null }
        val items = rentalsWithCoordinate.map { RentalClusterItem(it) }
        mapView.setClusterItems(items)

        if (rentalsWithCoordinate.isEmpty()) {
            mapView.zoomToCountry(activeCountry)
        } else {
            mapView.zoomToShowAllClusterItems()
        }

    }

    private fun hideFilterPanel() = searchBarWithFilterPanel.hideFilterPanel()


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    class RentalClusterItem(val rental: Rental) : ClusterItem(
            title = rental.orderNumber,
            subtitle = rental.rentalType,
            position = rental.machineCoordinate!!.toLatLng()
    )


    /*---------------------------------------- Extensions ----------------------------------------*/


    private fun MapView.zoomToCountry(country: Country, animated: Boolean = true) {
        zoomToBounds(country.bounds, animated)
    }

}