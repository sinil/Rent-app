package com.riwal.rentalapp.myinvoices

import android.content.Context
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.*

interface MyInvoicesView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()

    interface DataSource {
        fun totalOverDue(view: MyInvoicesView): Int
        fun overDueAmount(view: MyInvoicesView): Float
        fun invoices(view: MyInvoicesView): List<Invoice>
        fun contacts(view: MyInvoicesView): List<Contact?>
        fun venues(view: MyInvoicesView): List<VenueBody?>
        fun filter(view: MyInvoicesView): MyInvoicesFilter
        fun updateFilterValues(view: MyInvoicesView): Boolean
        fun activeCountry(view: MyInvoicesView): Country
        fun isUpdatingInvoices(view: MyInvoicesView): Boolean
        fun isFilteringInvoices(view: MyInvoicesView): Boolean
        fun isChatEnabled(view: MyInvoicesView): Boolean
        fun numberOfUnreadMessages(view: MyInvoicesView): Int
        fun hasFailedLoadingInvoices(view: MyInvoicesView): Boolean
        fun isPhoneCallEnable(view: MyInvoicesView): Boolean
        fun rentalDeskContactInfo(view: MyInvoicesView): List<ContactInfo>

    }

    interface Delegate {
        fun onRetryLoadingInvoicesSelected(view: MyInvoicesView)
        fun onFilterChanged(view: MyInvoicesView, newFilter: MyInvoicesFilter)
        fun onDownloadInvoiceSelected(context: Context,invoice: Invoice)
    }

}