package com.riwal.rentalapp.main.account

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.jaredrummler.materialspinner.MaterialSpinner
import com.riwal.rentalapp.R
import com.riwal.rentalapp.accessoriesonrent.AccessoriesOnRentViewImpl
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.mvc.BaseView
import com.riwal.rentalapp.common.ui.EasyViewPager
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.myinvoices.MyInvoicesViewImpl
import com.riwal.rentalapp.myprojects.MyProjectsViewImpl
import com.riwal.rentalapp.myquotations.MyQuotationsViewImpl
import com.riwal.rentalapp.myrentals.MyRentalsViewImpl
import com.riwal.rentalapp.scanmachineqrcode.ScanMachineQRCodeViewImpl
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_account.view.*
import org.joda.time.IllegalInstantException


class AccountViewImpl @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseView(context, attrs, defStyleAttr), AccountView, EasyViewPager.DataSource, EasyViewPager.Delegate, MaterialSpinner.OnItemSelectedListener<String> {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: AccountView.DataSource
    override lateinit var delegate: AccountView.Delegate

    override val loginView = LoginViewImpl(context)

    private val isLoggedIn
        get() = dataSource.isLoggedIn(view = this)

    private val currentCustomer
        get() = dataSource.currentCustomer(view = this)

    private val activePage
        get() = if (currentCustomer != null) customers.indexOf(currentCustomer!!) else 0

    private var customers: List<Customer> = emptyList()
        get() = dataSource.customers(view = this)

    private val isQRScannerVisible
        get() = dataSource.isQRScannerVisible(view = this)

    private val isAddAccessoriesEnabled
        get() = dataSource.isAddAccessoriesEnabled(view = this)

    private lateinit var customersNameAdapter: ArrayAdapter<String>

    private val isMyProjectsVisible
        get() = dataSource.isMyProjectsVisible(view = this)

    private val isMyInvoicesVisible
        get() = dataSource.isMyInvoicesVisible(view = this)

    private val isMyQuotationsVisible
        get() = dataSource.isMyQuotationsVisible(view = this)


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()

        addSubview(R.layout.view_account)

        content.addView(loginView)

        toolbar.title = getString(R.string.page_account)

        customersViewPager.dataSource = this
        customersViewPager.delegate = this

        myRentalsButton.setOnClickListener { delegate.onMyRentalsSelected(view = this) }
        scanMachineButton.setOnClickListener { delegate.onScanMachineSelected(view = this) }
        accessoriesButton.setOnClickListener { delegate.onAccessoriesOnRentSelected(view = this) }
        myProjectsButton.setOnClickListener { delegate.onMyProjectsSelected(view = this) }
        myQuotationButton.setOnClickListener { delegate.onMyQuotationSelected(view = this) }
        invoicesButton.setOnClickListener { delegate.onMyInvoicesSelected(view = this) }
        logOutButton.setOnClickListener { delegate.onLogOutSelected(view = this) }

        customersNameSpinner.setOnItemSelectedListener(this)
        customersNameAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, customers.map { customer -> customer.name })
        customersNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        customersNameSpinner.setAdapter(customersNameAdapter)

        updateUI(animated = false)
    }

    override fun onAppear() {
        super.onAppear()
        customersViewPager.currentItem = activePage
    }


    /*--------------------------------------- Account View ---------------------------------------*/


    override fun notifyDataChanged() {
        super.notifyDataChanged()
        loginView.notifyDataChanged()
    }

    override fun navigateToMyRentalsPage() {
        startActivity(MyRentalsViewImpl::class)
    }

    override fun navigateToScanMachinePage() {
        startActivity(ScanMachineQRCodeViewImpl::class)
    }

    override fun navigateToAcessoriesOnRent() {
        startActivity(AccessoriesOnRentViewImpl::class)
    }

    override fun navigateToMyProjects() {
        startActivity(MyProjectsViewImpl::class)
    }

    override fun navigateToMyQuotation() {
        startActivity(MyQuotationsViewImpl::class)
    }

    override fun navigateToMyInvoices() {
        startActivity(MyInvoicesViewImpl::class)
    }

    override fun showLogoutConfirmation(callback: () -> Unit) {
        AlertDialog.Builder(activity)
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirmation_dialog_message)
                .setPositiveButton(R.string.log_out) { _, _ -> callback() }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }


    /*------------------------------- Easy View Pager Data Source --------------------------------*/


    override fun numberOfPages(viewPager: EasyViewPager): Int {
        return customers.size
    }

    override fun viewForPageType(viewPager: EasyViewPager, viewType: Int, parent: ViewGroup, inflater: LayoutInflater, position: Int) = CustomerView(context)

    override fun hashForPage(viewPager: EasyViewPager, page: EasyViewPager.Page): Long {
        val position = viewPager.positionForPage(page)
        val company = customers[position]
        return accountManagerAndPicture(company).hashCode().toLong()
    }


    /*--------------------------------- Easy View Pager Delegate ---------------------------------*/


    override fun onPageCreated(viewPager: EasyViewPager, page: EasyViewPager.Page, position: Int) {
        val view = page.view as CustomerView
        val company = customers[position]
        val accountManagerAndPicture = accountManagerAndPicture(company)

        view.isCustomerNameVisible = customers.size == 1
        view.customer = company
        view.accountManager = accountManagerAndPicture.accountManager
        view.accountManagerPicture = accountManagerAndPicture.picture
        view.isLoading = accountManagerAndPicture.isUpdating
    }

    override fun onScrolledToPage(viewPager: EasyViewPager, page: EasyViewPager.Page, position: Int) {
        val customer = customers[position]
        delegate.onCustomerSelected(view = this, customer = customer)
    }


    /*-------------------------------- Spinner Adapter methods -----------------------------------*/


    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {
        val customer = customers[position]
        delegate.onCustomerSelected(view = this, customer = customer)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    @SuppressLint("SetTextI18n")
    override fun updateUI(animated: Boolean) {


        accountLayout.isVisible = isLoggedIn
        loginView.isVisible = !isLoggedIn


        if (isLoggedIn) {
            if (isQRScannerVisible)
                scanMachineButton.isVisible = isQRScannerVisible
            else
                scanMachineButton.visibility = View.GONE

            myProjectsButton.isVisible = isMyProjectsVisible
            invoicesButton.isVisible = isMyInvoicesVisible
            myQuotationButton.isVisible = isMyQuotationsVisible
            accessoriesButton.isVisible = isAddAccessoriesEnabled
            scanMachineButton.requestLayout()
            customersViewPager.notifyDataSetChanged()
            customersViewPager.currentPage = activePage
            pageInfoTextView.text = "${activePage + 1}/${customers.size}"
            pageInfoTextView.isVisible = customers.size > 1
            customersNameSpinner.isVisible = customers.size > 1

            customersNameAdapter.clear()
            customersNameAdapter.addAll(customers.map { customer -> customer.name })
            customersNameAdapter.notifyDataSetChanged()

            if (customers.isNotEmpty()) {
                try {
                    if (activePage >= 0 && activePage <= customers.size) customersNameSpinner.selectedIndex = activePage
                } catch (error:Exception){
                    println(error) }
            }

            myRentalsButtonSubtitleTextView.text = currentCustomer?.name

        }


        if (animated) {
            beginDelayedTransition(
                    ParallelAutoTransition()
                            .excludeTarget(loginView, true)
                            .excludeTarget(customersViewPager, true)
                            .excludeTarget(customersNameSpinner, true)
                            .excludeTarget(pageInfoTextView, true)
            )
        }

    }

    private fun accountManagerAndPicture(customer: Customer) = dataSource.accountManagerAndPicture(view = this, customer = customer)

    override fun notifyCountryChanged() = runOnUiThread {
        updateUI()
    }

    override fun notifyCustomersChanged(customers: List<Customer>?) {
        if (customers != null) {
            customersNameAdapter.clear()
            this.customers = customers
            updateUI()
        }
    }

}