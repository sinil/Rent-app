package com.riwal.rentalapp.myquotations

import android.content.Context
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.*

interface MyQuotationsView : MvcView, ObservableLifecycleView {


    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()


    interface DataSource {

        fun quotation(view: MyQuotationsView): List<Quotation>
        fun contacts(view: MyQuotationsView): List<Contact?>
        fun venues(view: MyQuotationsView): List<VenueBody?>
        fun filter(view: MyQuotationsView): MyQuotationsFilter
        fun updateFilterValues(view: MyQuotationsView): Boolean
        fun activeCountry(view: MyQuotationsView): Country
        fun isUpdatingQuotations(view: MyQuotationsView): Boolean
        fun isFilteringQuotations(view: MyQuotationsView): Boolean
        fun hasFailedLoadingQuotations(view: MyQuotationsView): Boolean

    }

    interface Delegate {
        fun onRetryLoadingQuotationSelected(view: MyQuotationsView)
        fun onFilterChanged(view: MyQuotationsView, newFilter: MyQuotationsFilter)
        fun onDownloadQuotationSelected(context: Context, quotation: Quotation)

    }


}