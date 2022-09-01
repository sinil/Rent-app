package com.riwal.rentalapp.myprojects

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import com.jaredrummler.materialspinner.MaterialSpinner
import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.R
import com.riwal.rentalapp.common.RentalAppNotificationActivity
import com.riwal.rentalapp.common.RentalDialogs
import com.riwal.rentalapp.common.extensions.android.*
import com.riwal.rentalapp.common.extensions.widgets.setNavigationIcon
import com.riwal.rentalapp.common.ui.ParallelAutoTransition
import com.riwal.rentalapp.common.ui.easyrecyclerview.EasyRecyclerView
import com.riwal.rentalapp.common.ui.easyrecyclerview.IndexPath
import com.riwal.rentalapp.machinetransferpanel.MachineTransferPanelViewImpl
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.MyProject
import com.riwal.rentalapp.model.api.MachineTransferRequestBody
import com.riwal.rentalapp.myprojects.machinetransfernotifications.TransferNotificationViewImpl
import kotlinx.android.synthetic.main.view_my_projects.*
import kotlinx.android.synthetic.main.view_my_projects.activityIndicator
import kotlinx.android.synthetic.main.view_my_projects.emptyMyRentalsView
import kotlinx.android.synthetic.main.view_my_rentals.toolbar
import kotlinx.coroutines.cancel


class MyProjectsViewImpl : RentalAppNotificationActivity(), MyProjectsView, EasyRecyclerView.DataSource, EasyRecyclerView.Delegate, MaterialSpinner.OnItemSelectedListener<String> {


    /*---------------------------------------- Properties ----------------------------------------*/


    override lateinit var dataSource: MyProjectsView.DataSource
    override lateinit var delegate: MyProjectsView.Delegate
    private lateinit var customersNameAdapter: ArrayAdapter<String>
    private lateinit var projectsNameAdapter: ArrayAdapter<String>


    private val myProject: MyProject?
        get() = dataSource.myProjects(view = this)

    private val venues
        get() = dataSource.venue(view = this)

    private val machines
        get() = dataSource.machines(view = this)

    private val customers
        get() = dataSource.customers(view = this)

    private val isUpdatingMyProjects
        get() = dataSource.isUpdatingMyProjects(view = this)

    private val hasFailedLoadingMyProjects
        get() = dataSource.hasFailedLoadingMyProjects(view = this)

    private val selectedCustomer
        get() = dataSource.selectedCustomer(view = this)

    private val selectedCustomerPosition
        get() = dataSource.selectedCustomerPosition(view = this)

    private val selectedProjectPosition
        get() = dataSource.selectedProjectPosition(view = this)

    private var machineTransferPanel: MachineTransferPanelViewImpl? = null
    private var notificationViewPage: TransferNotificationViewImpl? = null

    private val isMachineTransferPanelOpen
        get() = machineTransferPanel?.isOpen ?: false

    private var notificationMenuItem: MenuItem? = null


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.view_my_projects)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back, tint = color(R.color.app_bar_text))
        title = getString(R.string.my_projects)


        myProjectsRecyclerView.delegate = this
        myProjectsRecyclerView.dataSource = this

        customersNameSpinner.setOnItemSelectedListener(this)
        customersNameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customers.map { customer -> customer.name })
        customersNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        customersNameSpinner.setAdapter(customersNameAdapter)


        retryLoadingMyProjectsButton.setOnClickListener { delegate.onRetryLoadingMyProjectSelected(view = this) }



        updateUI(animated = false)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_notification, menu)

        notificationMenuItem = menu.findItem(R.id.action_notification)

        menu.items.forEach { it.icon.setTintList(R.color.toolbar_button) }

        updateUI(true)

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_notification -> {
            delegate.onNotificationSelected(); true
        }

        android.R.id.home -> {
            navigateBack(); true
        }

        else -> super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        when {
            isMachineTransferPanelOpen -> machineTransferPanel?.navigateBack()
            else -> navigateBack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun updateUI(animated: Boolean) {
        super.updateUI(animated)

        if (animated) {
            val transition = ParallelAutoTransition()
                    .excludeTarget(myProjectsRecyclerView, true)
            beginDelayedTransition(transition)
        }

        myProjectsRecyclerView.notifyDataSetChanged()
        myProjectsRecyclerView.isVisible = !hasFailedLoadingMyProjects && !isUpdatingMyProjects


        retryLoadingMyProjectsContainer.isVisible = !isUpdatingMyProjects && hasFailedLoadingMyProjects
        emptyMyRentalsView.isVisible = machines.isEmpty() && !isUpdatingMyProjects && !hasFailedLoadingMyProjects
        projectNameSpinner.isVisible = !isUpdatingMyProjects && !hasFailedLoadingMyProjects && venues.isNotEmpty()
        activityIndicator.isVisible = isUpdatingMyProjects

        customersNameAdapter.clear()
        customersNameAdapter.addAll(customers.map { customer -> customer.name })
        customersNameAdapter.notifyDataSetChanged()
        customersNameSpinner.selectedIndex = selectedCustomerPosition


        if (!venues.isNullOrEmpty()) {
            projectNameSpinner.setOnItemSelectedListener(this)
            projectsNameAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, venues.map { project -> project.name as String })
            projectsNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            projectNameSpinner.setAdapter(projectsNameAdapter)
        }

        projectSpinnerUpdate()


    }


    /*-------------------------------- Spinner Adapter methods -----------------------------------*/


    override fun onItemSelected(view: MaterialSpinner?, position: Int, id: Long, item: String?) {

        when (view) {
            customersNameSpinner -> {
                delegate.onSelectCustomerSpinner(position)
            }
            projectNameSpinner -> {
                delegate.onSelectedProject(position)
            }
        }

    }

    /*-------------------------- Easy RecyclerView Data Source methods ---------------------------*/


    override fun numberOfRowsInSection(recyclerView: EasyRecyclerView, section: Int) = machines.size
    override fun viewTypeForRow(recyclerView: EasyRecyclerView, indexPath: IndexPath): Int = R.layout.row_my_project
    override fun viewHolderForItemView(recyclerView: EasyRecyclerView, itemView: View, viewType: Int): EasyRecyclerView.ViewHolder = MyProjectRowViewHolder(itemView, this, delegate)


    /*---------------------------- Easy RecyclerView Delegate methods ----------------------------*/


    override fun prepareViewHolderForDisplay(recyclerView: EasyRecyclerView, viewHolder: EasyRecyclerView.ViewHolder, indexPath: IndexPath) {

        viewHolder as MyProjectRowViewHolder
        val machines = machines[indexPath.row]
        viewHolder.updateWith(machines)
    }


    /*------------------------------------- My Project View --------------------------------------*/


    override fun showMachineTransferPanel(preparationHandler: ControllerPreparationHandler) {
        machineTransferPanel = MachineTransferPanelViewImpl(this)
        machineTransferPanel!!.show(contentView, preparationHandler)
    }


    /*------------------------------------- Private methods --------------------------------------*/


    override fun notifyCustomersChanged(customers: List<Customer>?) {
        if (customers != null) {
            customersNameAdapter.clear()
            updateUI()
        }
    }

    override fun navigateToNotificationPage(preparationHandler: ControllerPreparationHandler) {
        startActivity(TransferNotificationViewImpl::class, controllerPreparationHandler = preparationHandler)
    }

    @SuppressLint("StringFormatInvalid")
    override fun showConfirmDialog(transferMachine: MachineTransferRequestBody, callback: () -> Unit) = runOnUiThread {
        RentalDialogs.messageDialog(this,
                getString(R.string.transfer_confirmation_dialog_message, transferMachine.machine.fleetNumber, transferMachine.toCustomer.name, transferMachine.toCustomer.contact?.name),
                getString(R.string.transfer_confirm_title), getString(R.string.confirm), true, callback).show()
    }

    override fun navigateBack() = runOnUiThread { finish() }


    private fun projectSpinnerUpdate() {

        try {
            if (!projectsNameAdapter.isEmpty) {
                projectsNameAdapter.clear()
                projectsNameAdapter.addAll(venues.map { project -> project.name })
                projectsNameAdapter.notifyDataSetChanged()

                if (venues.size > 1) {
                    projectNameSpinner.selectedIndex = selectedProjectPosition
                }
            }
        } catch (e: Exception) {
        }
    }


}