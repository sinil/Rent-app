package com.riwal.rentalapp.accessoriesonrent

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.accessoriesonrent.AccessoriesOnRentRowViewHolder.Style.NORMAL
import com.riwal.rentalapp.accessoriesonrent.AccessoriesOnRentRowViewHolder.Style.SELECTION
import com.riwal.rentalapp.accessoriesonrentdetail.AccessoryOnRentDetailViewImpl
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.animation.excludeTargets
import com.riwal.rentalapp.common.extensions.core.randomItem
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.AccessoriesOnRent
import com.riwal.rentalapp.offrentpanel.OffRentPanelViewImpl
import kotlinx.android.synthetic.main.view_accessories_on_rent.*
import kotlinx.android.synthetic.main.view_accessories_on_rent.activityIndicator
import kotlinx.android.synthetic.main.view_accessories_on_rent.chatButton
import kotlinx.android.synthetic.main.view_accessories_on_rent.filterPanelOverlay
import kotlinx.android.synthetic.main.view_accessories_on_rent.phoneCallButton
import kotlinx.android.synthetic.main.view_accessories_on_rent.rentalResultsSummaryGroup
import kotlinx.android.synthetic.main.view_accessories_on_rent.retryLoadingMyProjectsButton
import kotlinx.android.synthetic.main.view_accessories_on_rent.retryLoadingMyProjectsContainer
import kotlinx.android.synthetic.main.view_accessories_on_rent.searchAndFilterContainer
import kotlinx.android.synthetic.main.view_accessories_on_rent.selectionActionsToolbar
import kotlinx.android.synthetic.main.view_accessories_on_rent.stopRentingButton
import kotlinx.android.synthetic.main.view_accessories_on_rent.toolbar
import kotlinx.android.synthetic.main.view_my_rentals.resultsRecyclerView


class AccessoriesOnRentViewImpl : RentalAppNotificationActivity(), AccessoriesOnRentView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, AccessoriesOnRentSearchBarWithFilterPanal.Delegate {


    /*--------------------------------------- Properties -----------------------------------------*/


    override lateinit var dataSource: AccessoriesOnRentView.DataSource
    override lateinit var delegate: AccessoriesOnRentView.Delegate

    private var accessories: List<AccessoriesOnRent> = emptyList()
    private lateinit var searchBarWithFilterPanel: AccessoriesOnRentSearchBarWithFilterPanal
    private var offRentPanel: OffRentPanelViewImpl? = null


    private var needsUpdateData = false

    private val filter
        get() = dataSource.filter(view = this)

    private val changeableOrderStatuses
        get() = dataSource.changeableOrderStatuses(view = this)

    private val canChangePeriod
        get() = dataSource.canChangePeriod(view = this)

    private val isFilterPanelOpen
        get() = searchBarWithFilterPanel.isFilterPanelOpen

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val isUpdatingRental
        get() = dataSource.isUpdatingAccessories(view = this)

    private val isRentalPanelOpen
        get() = offRentPanel?.isOpen ?: false

    private val isInSelectionMode
        get() = dataSource.isInSelectionMode(view = this)

    private val selectedAccessories
        get() = dataSource.selectedAccessories(view = this)

    private val isChatEnable
        get() = dataSource.isChatEnable(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessage
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val hasFailedLoadingAccessories
        get() = dataSource.hasFailedLoadingAccessories(view = this)

    private val titleForSelectionMode: String
        @SuppressLint("StringFormatMatches", "StringFormatInvalid")
        get() = when {
            !isInSelectionMode -> getString(R.string.page_my_rentals)
            selectedAccessories.isEmpty() -> getString(R.string.my_rentals_select_rental_order)
            selectedAccessories.size == 1 -> getString(R.string.my_rentals_one_rental_order_selected, "${selectedAccessories.size}")
            else -> getString(R.string.my_rentals_multiple_rental_orders_selected, "${selectedAccessories.size}")
        }


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_accessories_on_rent)

        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))


        searchBarWithFilterPanel = AccessoriesOnRentSearchBarWithFilterPanal(context = this, firstDayOfWeek = activeCountry.firstDayOfWeek)
        searchBarWithFilterPanel.delegate = this
        searchBarWithFilterPanel.filter = filter
        searchBarWithFilterPanel.changableOrderStatuses = changeableOrderStatuses
        searchBarWithFilterPanel.canChangePeriod = canChangePeriod

        searchAndFilterContainer.addView(searchBarWithFilterPanel)

        resultsRecyclerView.dataSource = this
        resultsRecyclerView.delegate = this

        retryLoadingMyProjectsButton.setOnClickListener { delegate.onRetryLoadingAccessoriesSelected(view = this) }
        filterPanelOverlay.setOnClickListener { onFilterOverlayPressed() }
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


        val previousAccessories = accessories

        updateDataIfNeeded()

        val period = filter.period
        val isAccessoriesChanged = previousAccessories != accessories

        title = titleForSelectionMode

        resultsRecyclerView.notifyDataSetChanged()
        resultsRecyclerView.isVisible  = accessories.isNotEmpty() && !hasFailedLoadingAccessories

        accessoriesResultsTextView.text = getString(R.string.search_results, accessories.size)
        accessoriesResultsPeriodTextView.text = period.toShortStyleString()

        filterPanelOverlay.isVisible = isFilterPanelOpen

        retryLoadingMyProjectsContainer.isVisible = !isUpdatingRental && hasFailedLoadingAccessories
        emptyAccessoriesView.isVisible = accessories.isEmpty() && !isUpdatingRental && !hasFailedLoadingAccessories
        activityIndicator.isVisible = isUpdatingRental

        searchAndFilterContainer.isVisible = !isInSelectionMode
        rentalResultsSummaryGroup.isVisible = !isInSelectionMode
        resultsRecyclerView.setPaddingTop(if (isInSelectionMode) dimen(R.dimen.app_bar_default_height) else 100.dp())
        resultsRecyclerView.setPaddingBottom(if (isInSelectionMode) dimen(R.dimen.app_bar_default_height) else 0.dp())
        toolbar.isVisible = isInSelectionMode

        selectionActionsToolbar.isVisible = isInSelectionMode
        stopRentingButton.isEnabled = selectedAccessories.isNotEmpty()

        chatButton.isVisible = isChatEnable && !isInSelectionMode
        chatButton.numberOfUnreadMessages = numberOfUnreadMessage

        phoneCallButton.isVisible = isPhoneCallEnable
        phoneCallButton.phoneNumber = rentalContactNumber


        if (isAccessoriesChanged) {
            resultsRecyclerView.scrollToPosition(0)
        }


    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun updateDataIfNeeded() {

        if (!needsUpdateData) {
            return
        }

        accessories = dataSource.accessoriesOnRent(view = this)

        if (accessories.isNotEmpty() && searchBarWithFilterPanel.searchHint == null) {
            val searchHintValues = with(accessories.first()) { listOfNotNull(rentalType, orderNumber, project.name, project.contactName) }
            searchBarWithFilterPanel.searchHint = getString(R.string.hint_search_example, searchHintValues.randomItem)
        }

        needsUpdateData = false
    }


    private fun hideFilterPanel() = searchBarWithFilterPanel.hideFilterPanel()


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val accessories = accessories[indexPath.row]
        val canStopRenting = dataSource.canStopRenting(view = this, accessories = accessories)
        viewHolder as AccessoriesOnRentRowViewHolder
        viewHolder.updateWith(accessories)
        viewHolder.style = if (isInSelectionMode && canStopRenting) SELECTION else NORMAL
        viewHolder.isChecked = accessories in selectedAccessories
        viewHolder.itemView.setOnLongClickListener {
            if (!isInSelectionMode) {
                delegate.onEnableSelectionModeSelected(view = this)
                delegate.onAccessoriesSelected(view = this, accessoriesOnRent = accessories)
                true
            } else {
                false
            }
        }
    }

        override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
            val accessories = accessories[indexPath.row]
            delegate.onAccessoriesSelected(view = this, accessoriesOnRent = accessories)
        }


        /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


        override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = accessories.size
        override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_rental
        override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = AccessoriesOnRentRowViewHolder(itemView)
        override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = accessories[indexPath.row].hashCode().toLong()


        /*----------------------- MyRentalsSearchBarWithFilterPanel Delegate -------------------------*/


        override fun onBackButtonPressed(view: AccessoriesOnRentSearchBarWithFilterPanal) = onBackPressed()

        override fun onFilterPanelToggled(view: AccessoriesOnRentSearchBarWithFilterPanal, isOpen: Boolean) {
            updateUI()
        }

        override fun onFilterChanged(view: AccessoriesOnRentSearchBarWithFilterPanal, newFilter: AccessoriesOnRentFilter) = delegate.onFilterChanged(view = this, newFilter = newFilter)


        /*---------------------------------------  Methods -------------------------------------------*/

        override fun notifyDataChanged() {
            needsUpdateData = true
            super.notifyDataChanged()
        }

        override fun showOffRentPanel(preparationHandler: ControllerPreparationHandler) {
            offRentPanel = OffRentPanelViewImpl(this)
            offRentPanel!!.show(contentView, preparationHandler)
        }

        override fun showCommentsDialog(callback: (comments: String) -> Unit) {
            RentalDialogs.commentsDialog(this, callback).show()
        }

        override fun navigateToAccessoriesDetailsPage(preparationHandler: ControllerPreparationHandler) {
            startActivity(AccessoryOnRentDetailViewImpl::class, controllerPreparationHandler = preparationHandler)
        }

        override fun navigateBack() {
            finish()
        }


        /*----------------------------------------- Actions ------------------------------------------*/


        private fun onFilterOverlayPressed() = hideFilterPanel()

        override fun onBackPressed() = when {
            isRentalPanelOpen -> offRentPanel!!.navigateBack()
            isInSelectionMode -> {
                delegate.onCancelSelectionModeSelected(view = this)
                updateUI()
            }
            isFilterPanelOpen -> hideFilterPanel()
            else -> navigateBack()
        }

    }