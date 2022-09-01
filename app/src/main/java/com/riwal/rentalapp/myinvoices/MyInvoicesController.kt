package com.riwal.rentalapp.myinvoices

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import java.io.File


class MyInvoicesController(val view: MyInvoicesView,
                           val customer: Customer,
                           private val rentalManager: RentalManager,
                           private val chatManager: ChatManager,
                           val analytics: RentalAnalytics,
                           val activeCountry: Country) : ViewLifecycleObserver, MyInvoicesView.DataSource, MyInvoicesView.Delegate, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var totalOverDues: Int = 0
    private var overDuesAmount: Float = 0.0f
    private var invoices: List<Invoice> = emptyList()
    private var contacts: List<Contact?> = emptyList()
    private var venues: List<VenueBody?> = emptyList()
    private var updateFilterValues = false
    private var filteredInvoices: List<Invoice> = emptyList()
    private val asyncFilter: PublishSubject<FilterAndInput> = PublishSubject.create()
    private val defaultFilter = MyInvoicesFilter(
            query = "",
            period = (LocalDate.now() - 1.months())..(LocalDate.now() + 1.months()))
    private var filter = defaultFilter
    private var hasFailedLoadingInvoices = false
    private var isFiltering = false
    private var isUpdatingInvoices = false
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
        getInvoicesFromServer(dateRange = filter.period)
        setUpAsyncRentalsFilter()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingMyInvoices()
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


    /*--------------------------------- MyInvoiceView DataSource ---------------------------------*/


    override fun totalOverDue(view: MyInvoicesView): Int = totalOverDues
    override fun overDueAmount(view: MyInvoicesView): Float = overDuesAmount
    override fun invoices(view: MyInvoicesView): List<Invoice> = filteredInvoices
    override fun contacts(view: MyInvoicesView): List<Contact?> = contacts
    override fun venues(view: MyInvoicesView): List<VenueBody?> = venues
    override fun filter(view: MyInvoicesView) = filter
    override fun updateFilterValues(view: MyInvoicesView): Boolean = updateFilterValues
    override fun activeCountry(view: MyInvoicesView): Country = activeCountry
    override fun isUpdatingInvoices(view: MyInvoicesView): Boolean = isUpdatingInvoices
    override fun isFilteringInvoices(view: MyInvoicesView): Boolean = isFiltering
    override fun isChatEnabled(view: MyInvoicesView): Boolean = chatManager.isChatEnabled
    override fun numberOfUnreadMessages(view: MyInvoicesView): Int = chatManager.numberOfUnreadMessages
    override fun hasFailedLoadingInvoices(view: MyInvoicesView): Boolean = hasFailedLoadingInvoices
    override fun isPhoneCallEnable(view: MyInvoicesView): Boolean = activeCountry.isPhoneCallEnable
    override fun rentalDeskContactInfo(view: MyInvoicesView): List<ContactInfo> = activeCountry.rentalDeskContactInfo


    /*--------------------------------- MyInvoiceView Delegate   ---------------------------------*/


    override fun onRetryLoadingInvoicesSelected(view: MyInvoicesView) {
        getInvoicesFromServer(dateRange = filter.period)
    }

    override fun onFilterChanged(view: MyInvoicesView, newFilter: MyInvoicesFilter) {

        val oldFilter = filter.copy()
        filter = newFilter

        if (oldFilter.period != newFilter.period) {
            getInvoicesFromServer(dateRange = filter.period)
        } else {
            filterInvoicesForView()
        }
    }

    override fun onDownloadInvoiceSelected(context: Context, invoice: Invoice) {

        if (invoice.invoiceNumber != null) {
//            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
//            val filePath = File("$storageDir/${invoice.invoiceNumber}.pdf")
//            if (filePath.isFile) { showPdf(context, filePath)  } else
            analytics.downloadInvoiceSelected()
            downloadInvoiceFromServer(invoiceNumber = invoice.invoiceNumber, context = context)
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
//                .map { it.sortedByStatusThenByOnRentDateTime() }
                .subscribe {
                    filteredInvoices = it
                    // as per web portal logic
                    totalOverDues = filteredInvoices.filter { invoice -> invoice.overDue > 0 && !invoice.paid }.size
                    overDuesAmount = filteredInvoices.filter { invoice -> invoice.overDue > 0 && !invoice.paid }.map { invoice -> invoice.amount }.sum()

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

    private fun getInvoicesFromServer(dateRange: ClosedRange<LocalDate>) = launch {

        isUpdatingInvoices = true
        view.notifyDataChanged()

        try {
            val invoiceResponse = rentalManager.getInvoices(customer, dateRange)
            totalOverDues = invoiceResponse.TotalOverDues
            overDuesAmount = invoiceResponse.TotalOverDuesAmount
            invoices = invoiceResponse.Invoices.map { it.toInvoice() }
            contacts = invoiceResponse.Contacts.map { it.toContactBody() }
            venues = invoiceResponse.Venues.map { it.toVenueBody() }
            updateFilterValues = true
            hasFailedLoadingInvoices = false
        } catch (error: Exception) {
            invoices = emptyList()
            contacts = emptyList()
            venues = emptyList()
            filteredInvoices = emptyList()
            updateFilterValues = false
            hasFailedLoadingInvoices = true

        }

        filterInvoicesForView()

        isUpdatingInvoices = false
        view.notifyDataChanged()
    }

    private fun downloadInvoiceFromServer(context: Context, invoiceNumber: String) = launch {

        isUpdatingInvoices = true
        view.notifyDataChanged()

        try {
            val invoiceResponse = rentalManager.downloadInvoice(customerId = customer.id, invoiceId = invoiceNumber)
            var file: File? = null
            file = createFile(context, invoiceNumber, "pdf")
            if (file != null) {
                invoiceResponse.byteStream().downloadFile(file)
                if (file.length() > 0) {
                    showPdf(context, file)
                } else {
                    postEvent(Notification.INVOICE_NOT_AVAILABLE)
                }
            }
            hasFailedLoadingInvoices = false
        } catch (error: Exception) {
            hasFailedLoadingInvoices = true
            postEvent(Notification.DOWNLOAD_INVOICE_FAILED)
        }

        isUpdatingInvoices = false
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


    private fun filterInvoicesForView() {
        val filterAndInput = FilterAndInput(filter = filter, input = invoices)
        asyncFilter.onNext(filterAndInput)
    }


    /*------------------------------------ Private extensions ------------------------------------*/


    private fun List<Invoice>.sortedByStatusThenByOnRentDateTime(): List<Invoice> {
        val rentalType = InvoiceTypes.all
        return this.sortedWith(compareBy<Invoice> { rentalType.indexOf(it.invoiceType) }.thenByDescending { it.invoiceDate })
    }


    /*------------------------------- Classes / Interfaces / Enums -------------------------------*/


    data class FilterAndInput(val filter: MyInvoicesFilter, val input: List<Invoice>)

}