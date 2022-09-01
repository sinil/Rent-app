package com.riwal.rentalapp.myinvoices

import android.annotation.SuppressLint
import androidx.transition.TransitionSet
import androidx.transition.Slide
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.extensions.android.beginDelayedTransition
import com.riwal.rentalapp.common.extensions.android.color
import com.riwal.rentalapp.common.extensions.android.isVisible
import com.riwal.rentalapp.common.extensions.core.formatThousandSeparator
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.model.Contact
import com.riwal.rentalapp.model.Invoice
import com.riwal.rentalapp.model.VenueBody
import kotlinx.android.synthetic.main.view_my_invoices.*

class MyInvoicesViewImpl : RentalAppNotificationActivity(), MyInvoicesView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MyInvoicesSearchBarWithFilterPanel.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: MyInvoicesView.DataSource
    override lateinit var delegate: MyInvoicesView.Delegate

    private var totalOverDues: Int = 0
    private var overDuesAmount: Float = 0f
    private var invoices: List<Invoice> = emptyList()
    private var contacts: List<Contact?> = emptyList()
    private var venues: List<VenueBody?> = emptyList()

    private lateinit var searchBarWithFilterPanel: MyInvoicesSearchBarWithFilterPanel

    private var needsUpdateData = false

    private val isFilterPanelOpen
        get() = searchBarWithFilterPanel.isFilterPanelOpen

    private val filter
        get() = dataSource.filter(view = this)

    private var needsUpdateFilterValues = true

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val isUpdatingInvoices
        get() = dataSource.isUpdatingInvoices(view = this)

    private val isFilteringInvoices
        get() = dataSource.isFilteringInvoices(view = this)

    private val isChatEnabled
        get() = dataSource.isChatEnabled(view = this)

    private val isPhoneCallEnable
        get() = dataSource.isPhoneCallEnable(view = this)

    private val rentalContactNumber
        get() = dataSource.rentalDeskContactInfo(view = this)[0].phoneNumber

    private val numberOfUnreadMessages
        get() = dataSource.numberOfUnreadMessages(view = this)

    private val hasFailedLoadingInvoices
        get() = dataSource.hasFailedLoadingInvoices(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_my_invoices)

        setSupportActionBar(toolbar)
        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_close, tint = color(R.color.app_bar_text))


        searchBarWithFilterPanel = MyInvoicesSearchBarWithFilterPanel(context = this, firstDayOfWeek = activeCountry.firstDayOfWeek)
        searchBarWithFilterPanel.delegate = this
        searchBarWithFilterPanel.filter = filter

        searchAndFilterContainer.addView(searchBarWithFilterPanel)

        resultsRecyclerView.dataSource = this
        resultsRecyclerView.delegate = this


        retryLoadingMyInvoicesButton.setOnClickListener { delegate.onRetryLoadingInvoicesSelected(view = this) }
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
        activityIndicator.isVisible = isFilteringInvoices || isUpdatingInvoices

        retryLoadingMyInvoicesContainer.isVisible = !isUpdatingInvoices && hasFailedLoadingInvoices
        emptyMyInvoicesView.isVisible = invoices.isEmpty() && !isUpdatingInvoices && !isFilteringInvoices && !hasFailedLoadingInvoices

        val period = filter.period

        totalResultsTextView.text = getString(R.string.search_results, invoices.size)
        resultsPeriodTextView.text = period.toShortStyleString()
        if (totalOverDues > 0) {
            overdueResults.visibility = View.VISIBLE
            overdueResults.text = getString(R.string.invoices_overview, totalOverDues.toString(),overDuesAmount.formatThousandSeparator())
        } else {
            overdueResults.visibility = View.GONE

        }


    }

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


    /*----------------------- MyInvoicesSearchBarWithFilterPanel Delegate ------------------------*/


    override fun onBackButtonPressed(view: MyInvoicesSearchBarWithFilterPanel) = onBackPressed()

    override fun onFilterPanelToggled(view: MyInvoicesSearchBarWithFilterPanel, isOpen: Boolean) {
        updateUI()
    }

    override fun onFilterChanged(view: MyInvoicesSearchBarWithFilterPanel, newFilter: MyInvoicesFilter) = delegate.onFilterChanged(view = this, newFilter = newFilter)


    /*--------------------------  Easy RecyclerView Data Source methods --------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = invoices.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = R.layout.row_invoice
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int) = InvoiceRowViewHolder(itemView, delegate)
    override fun stableIdForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath) = invoices[indexPath.row].hashCode().toLong()


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val invoice = invoices[indexPath.row]
        viewHolder as InvoiceRowViewHolder
        viewHolder.updateWith(invoice)

    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun updateDataIfNeeded() {

        if (!needsUpdateData) {
            return
        }

        totalOverDues = dataSource.totalOverDue(view = this)
        overDuesAmount = dataSource.overDueAmount(view = this)
        invoices = dataSource.invoices(view = this)
        contacts = dataSource.contacts(view = this)
        venues = dataSource.venues(view = this)


        if (needsUpdateFilterValues) {
            searchBarWithFilterPanel.populateContacts(contacts)
            searchBarWithFilterPanel.populateVenues(venues)
            if (contacts.isNotEmpty() || venues.isNotEmpty())
                needsUpdateFilterValues = false
        }

//        searchBarWithFilterPanel.updateUI(false)

        needsUpdateData = false
    }

    private fun hideFilterPanel() = searchBarWithFilterPanel.hideFilterPanel()

}