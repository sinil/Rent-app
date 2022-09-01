package com.riwal.rentalapp.country

import androidx.appcompat.app.AlertDialog
import android.view.View
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.country.CountryView.DataSource
import com.riwal.rentalapp.country.CountryView.Delegate
import com.riwal.rentalapp.model.Notification
import com.riwal.rentalapp.model.Country.CORPORATE
import kotlinx.android.synthetic.main.view_country.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CountryViewImpl : RentalAppNotificationActivity(), CountryView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: DataSource
    override lateinit var delegate: Delegate

    private val activeCountry
        get() = dataSource.activeCountry(view = this)

    private val countries
        get() = dataSource.countries(view = this)

    // Riwal International up top, followed by the other countries in alphabetical order
    private val sortedCountries
        get() = countries.sortedWith(compareBy({ it != CORPORATE }, { getString(it.localizedNameRes) }))


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_country)

        titleTextView.text = getString(R.string.page_country)

        countryRecyclerView.dataSource = this
        countryRecyclerView.delegate = this
    }

    override fun onBackPressed() {
        delegate.onNavigateBackSelected()
    }

    override fun shouldDeferNotification(notification: Notification) = true


    /*-------------------------------------- OrderListView ---------------------------------------*/


    override suspend fun confirmMachineOrdersWillBeCleared(): Boolean = suspendCoroutine { c ->
        runOnUiThread {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_clear_machine_orders_title))
                    .setMessage(getString(R.string.dialog_clear_machine_orders_message))
                    .setPositiveButton(getString(R.string.button_continue)) { _, _ -> c.resume(true) }
                    .setNegativeButton(getString(R.string.cancel)) { _, _ -> c.resume(false) }
                    .show()
        }
    }

    override fun navigateBack() {
        finish()
    }


    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int): Int {
        return sortedCountries.size
    }

    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int {
        return R.layout.row_country
    }

    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder {
        return CountryRowViewHolder(itemView)
    }


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val country = sortedCountries[indexPath.row]
        viewHolder as CountryRowViewHolder
        viewHolder.updateWith(country, country == activeCountry)
    }

    override fun onRowSelected(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {
        val country = sortedCountries[indexPath.row]
        delegate.onCountrySelected(country)
    }

}