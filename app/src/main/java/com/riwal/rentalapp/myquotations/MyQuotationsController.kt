package com.riwal.rentalapp.myquotations

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.core.months
import com.riwal.rentalapp.common.extensions.datetime.seconds
import com.riwal.rentalapp.common.extensions.downloadFile
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.extensions.rxjava.debounce
import com.riwal.rentalapp.common.extensions.rxjava.disposedBy
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.io.File
import androidx.core.content.FileProvider
import kotlinx.coroutines.cancel

class MyQuotationsController(val view: MyQuotationsView,
                             val customer: Customer,
                             val rentalManager: RentalManager,
                             val analytics: RentalAnalytics,
                             val activeCountry: Country) : ViewLifecycleObserver, MyQuotationsView.DataSource, MyQuotationsView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/

    private var totalQuotation: Int= 0
    private var quotation: List<Quotation> = emptyList()
    private var contacts: List<Contact?> = emptyList()
    private var venues: List<VenueBody?> = emptyList()
    private var updateFilterValues = false
    private var filteredQuotation: List<Quotation> = emptyList()
    private val asyncFilter: PublishSubject<FilterAndInput> = PublishSubject.create()
    private val defaultFilter = MyQuotationsFilter(
            query = "",
            period = (LocalDate.now() - 1.months())..(LocalDate.now() + 1.months()))
    private var filter = defaultFilter
    private var hasFailedLoadingQuotation = false
    private var isFiltering = false
    private var isUpdatingQuotation = false
    private var filterSubscription = CompositeDisposable()
    private var messagesSubscription = CompositeDisposable()

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }


    override fun onViewCreate() {
        super.onViewCreate()
        getQuotationFromServer(dateRange = filter.period)
        setUpAsyncRentalsFilter()
    }


    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingMyQuotation()
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


    /*--------------------------------- MyQuotationView DataSource ---------------------------------*/


    override fun quotation(view: MyQuotationsView) = filteredQuotation

    override fun venues(view: MyQuotationsView) = venues

    override fun contacts(view: MyQuotationsView)= contacts

    override fun filter(view: MyQuotationsView) = filter

    override fun updateFilterValues(view: MyQuotationsView) = updateFilterValues

    override fun activeCountry(view: MyQuotationsView) = activeCountry

    override fun isUpdatingQuotations(view: MyQuotationsView) = isUpdatingQuotation

    override fun isFilteringQuotations(view: MyQuotationsView) = isFiltering

    override fun hasFailedLoadingQuotations(view: MyQuotationsView) = hasFailedLoadingQuotation




    /*--------------------------------- MyQuotationView Delegate   ---------------------------------*/


    override fun onRetryLoadingQuotationSelected(view: MyQuotationsView) {
        getQuotationFromServer(dateRange = filter.period)
    }

    override fun onFilterChanged(view: MyQuotationsView, newFilter: MyQuotationsFilter) {

        val oldFilter = filter.copy()
        filter = newFilter

        if (oldFilter.period != newFilter.period) {
            getQuotationFromServer(dateRange = filter.period)
        } else {
            filterQuotationForView()
        }
    }

    override fun onDownloadQuotationSelected(context: Context, quotation: Quotation) {

        if (quotation.quotationNo != null) {
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
            analytics.downloadQuotationSelected()
            downloadQuotationFromServer(quotationNumber = quotation.quotationNo!!, context = context)
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
                .map { it.filter.applyTo(it.input) }
                .subscribe {
                    filteredQuotation = it
                    totalQuotation = filteredQuotation.size
                    isFiltering = false
                    view.notifyDataChanged()
                }
                .disposedBy(filterSubscription)
    }


    private fun getQuotationFromServer(dateRange: ClosedRange<LocalDate>) = launch {

        isUpdatingQuotation = true
        view.notifyDataChanged()

        try {
            val quotationsResponse = rentalManager.getQuotations(customer, dateRange)
            quotation = quotationsResponse.Quotations!!.map { it.toQuotations()!! }
            contacts = quotationsResponse.ContactPersons!!.map { it.toContactBody() }
            venues = quotationsResponse.Venues!!.map { it.toVenue() }
            updateFilterValues = true
            hasFailedLoadingQuotation = false
        } catch (error: Exception) {
            quotation = emptyList()
            contacts = emptyList()
            venues = emptyList()
            filteredQuotation = emptyList()
            updateFilterValues = false
            hasFailedLoadingQuotation = true

        }

        filterQuotationForView()

        isUpdatingQuotation = false
        view.notifyDataChanged()
    }


    private fun downloadQuotationFromServer(context: Context, quotationNumber: String) = launch {

        isUpdatingQuotation = true
        view.notifyDataChanged()

        try {
            val quotationResponse = rentalManager.downloadQuotation(customerId = customer.id, quotationId = quotationNumber)
            var file: File? = null
            file = createFile(context, quotationNumber, "pdf")
            if (file != null) {
                quotationResponse.byteStream().downloadFile(file)
                if (file.length() > 0) {
                    showPdf(context, file)
                } else {
                    postEvent(Notification.QUOTATION_NOT_AVAILABLE)
                }
            }
            hasFailedLoadingQuotation = false
        } catch (error: Exception) {
            hasFailedLoadingQuotation = true
            postEvent(Notification.DOWNLOAD_QUOTATION_FAILED)
        }

        isUpdatingQuotation = false
        view.notifyDataChanged()
    }

    private fun showPdf(context: Context, filePath: File) {

        val target = Intent(Intent.ACTION_VIEW)
        val fileURi: Uri = FileProvider.getUriForFile(context, context.applicationContext.packageName.toString() + ".fileprovider", filePath)
        target.setDataAndType(fileURi, "application/pdf")
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(target)

    }

    private fun createFile(context: Context, fileName: String, fileExt: String): File? {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
        val file = File("$storageDir/$fileName.$fileExt")
        return storageDir?.let { file }
    }

    private fun filterQuotationForView() {
        val filterAndInput = FilterAndInput(filter = filter, input = quotation)
        asyncFilter.onNext(filterAndInput)
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    data class FilterAndInput(val filter: MyQuotationsFilter, val input: List<Quotation>)


}