package com.riwal.rentalapp.myquotations

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.transition.Slide
import androidx.transition.TransitionSet
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Quotation
import com.riwal.rentalapp.model.VenueBody
import kotlinx.android.synthetic.main.view_my_quotations.*
import kotlinx.android.synthetic.main.view_my_quotations.searchAndFilterContainer

class MyQuotationsViewImpl : RentalAppNotificationActivity(), MyQuotationsView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MyQuotationsSearchBarWithFilterPanel.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/

    override lateinit var dataSource: MyQuotationsView.DataSource
    override lateinit var delegate: MyQuotationsView.Delegate


    private var quotation: List<Quotation?> = emptyList()
    private var contacts: List<Contact?> = emptyList()
    private var venues: List<VenueBody?> = emptyList()

    private lateinit var searchBarWithFilterPanel: MyQuotationsSearchBarWithFilterPanel

    private val filter
        get() = dataSource.filter(view = this)

    private var needsUpdateData = false

    private val isFilterPanelOpen
        get() = searchBarWithFilterPanel.isFilterPanelOpen

    private var needsUpdateFilterValues = true

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val isUpdatingQuotations
        get() = dataSource.isUpdatingQuotations(view = this)

    private val isFilteringQuotations
        get() = dataSource.isFilteringQuotations(view = this)

    private val hasFailedLoadingQuotations
        get() = dataSource.hasFailedLoadingQuotations(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_my_quotations)

        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))


        searchBarWithFilterPanel = MyQuotationsSearchBarWithFilterPanel(context = this, firstDayOfWeek = activeCountry.firstDayOfWeek)
        searchBarWithFilterPanel.delegate = this
        searchBarWithFilterPanel.filter = filter

        searchAndFilterContainer.addView(searchBarWithFilterPanel)

        resultsRecyclerView.dataSource = this
        resultsRecyclerView.delegate = this


        retryLoadingMyQuotationsButton.setOnClickListener { delegate.onRetryLoadingQuotationSelected(view = this) }
        filterPanelOverlay.setOnClickListener { onFilterOverlayPressed() }

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
                    .addTransition(Slide(Gravity.TOP).addTarget(toolbar))
                    .excludeChildren(resultsRecyclerView, true))
        }

        updateDataIfNeeded()

        resultsRecyclerView.notifyDataSetChanged()

        filterPanelOverlay.isVisible = isFilterPanelOpen
        activityIndicator.isVisible = isFilteringQuotations || isUpdatingQuotations

        retryLoadingMyQuotationsContainer.isVisible = !isUpdatingQuotations && hasFailedLoadingQuotations
        emptyMyQuotationsView.isVisible = quotation.isEmpty() && !isUpdatingQuotations && !isFilteringQuotations && !hasFailedLoadingQuotations

        val period = filter.period

        totalResultsTextView.text = getString(R.string.search_results, quotation.size)
        resultsPeriodTextView.text = period.toShortStyleString()

    }

    /*----------------------- MyQuotationsSearchBarWithFilterPanel Delegate ------------------------*/


    override fun onBackButtonPressed(view: MyQuotationsSearchBarWithFilterPanel) = onBackPressed()

    override fun onFilterPanelToggled(view: MyQuotationsSearchBarWithFilterPanel, isOpen: Boolean) {
        updateUI()
    }

    override fun onFilterChanged(view: MyQuotationsSearchBarWithFilterPanel, newFilter: MyQuotationsFilter) = delegate.onFilterChanged(view = this, newFilter = newFilter)


    /*----------------------------------------- Actions ------------------------------------------*/

    private fun onFilterOverlayPressed() = hideFilterPanel()

    override fun onBackPressed() = when {
        isFilterPanelOpen -> hideFilterPanel()
        else -> navigateBack()
    }

    /*----------------------------------------- Methods ------------------------------------------*/

    override fun notifyDataChanged() {
        needsUpdateData = true
        super.notifyDataChanged()
    }

    override fun navigateBack() = runOnUiThread {
        finish()
    }


    /*--------------------------  Easy RecyclerView Data Source methods --------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = quotation.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_my_quotations
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = MyQuotationsViewHolder(itemView, delegate)
    override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = quotation[indexPath.row].hashCode().toLong()


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val quotations = quotation[indexPath.row]
        viewHolder as MyQuotationsViewHolder
        viewHolder.updateWith(quotations!!)

    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
    }


    /*------------------------------------- Private methods --------------------------------------*/

    private fun updateDataIfNeeded() {

        if (!needsUpdateData) {
            return
        }

        quotation = dataSource.quotation(view = this)
        contacts = dataSource.contacts(view = this)
        venues = dataSource.venues(view = this)


        if (needsUpdateFilterValues) {
            searchBarWithFilterPanel.populateContacts(contacts)
            searchBarWithFilterPanel.populateVenues(venues)
            if (contacts.isNotEmpty() || venues.isNotEmpty())
                needsUpdateFilterValues = false
        }


        needsUpdateData = false

    }

    private fun hideFilterPanel() = searchBarWithFilterPanel.hideFilterPanel()


}