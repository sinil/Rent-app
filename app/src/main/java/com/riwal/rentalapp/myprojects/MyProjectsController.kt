package com.riwal.rentalapp.myprojects

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.extensions.eventbus.postEvent
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.machinetransferpanel.MachineTransferPanelController
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.AcceptTransferMachineRequestBody
import com.riwal.rentalapp.model.api.MachineTransferRequestBody
import com.riwal.rentalapp.myprojects.machinetransfernotifications.TransferNotificationController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MyProjectsController(
        val view: MyProjectsView,
        val activeCountry: Country,
        val chatManager: ChatManager,
        val analytics: RentalAnalytics,
        val orderManager: OrderManager,
        val sessionManager: SessionManager,
        val machinesManager: MachinesManager
) : ViewLifecycleObserver, MyProjectsView.DataSource, MachineTransferPanelController.Delegate, MyProjectsView.Delegate, SessionManager.Observer, CoroutineScope by MainScope() {


    /*--------------------------------------- Properties -----------------------------------------*/


    private var myProjects: MyProject? = null
    private var selectedCustomerPosition: Int = customers.indexOf(currentCustomer)
    private var selectedProjectPosition: Int = 0

    private var venues: List<VenueBody> = emptyList()

    private var machines: List<MachineBody> = emptyList()

    private var selectedProject: VenueBody? = null
    private var isUpdatingMyProjects = false
    private var hasFailedLoadingMyProjects = false

    private val user
        get() = sessionManager.user

    private val currentCustomer
        get() = user?.currentCustomer

    private var selectedCustomer: Customer? = null

    private val customers
        get() = user?.customers ?: emptyList()




    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }


    override fun onViewCreate() {
        super.onViewCreate()
        sessionManager.addObserver(this)
        selectedCustomer = currentCustomer

        getMyProjects()
    }

    override fun onViewDestroy() {
        super.onViewDestroy()
        sessionManager.removeObserver(this)
        cancel()
    }

    override fun onViewAppear() {
        super.onViewAppear()
        analytics.displayingMyProjects()
    }


    /*----------------------------------------  DataSource ---------------------------------------*/

    override fun myProjects(view: MyProjectsView) = myProjects

    override fun venue(view: MyProjectsView) = venues

    override fun machines(view: MyProjectsView) = machines

    override fun isChatEnable(view: MyProjectsView) = chatManager.isChatEnabled

    override fun isPhoneCallEnable(view: MyProjectsView) = activeCountry.isPhoneCallEnable

    override fun customers(view: MyProjectsView) = customers

    override fun isUpdatingMyProjects(view: MyProjectsView) = isUpdatingMyProjects

    override fun hasFailedLoadingMyProjects(view: MyProjectsView) = hasFailedLoadingMyProjects

    override fun selectedCustomer(view: MyProjectsView) = selectedCustomer ?: currentCustomer

    override fun selectedCustomerPosition(view: MyProjectsView) = selectedCustomerPosition

    override fun selectedProjectPosition(view: MyProjectsView) = selectedProjectPosition

    override fun isTransferMachineEnable(view: MyProjectsView) = true /* Dummy value need to update later */


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onMachineTransferSelected(machines: MachineBody) {
        analytics.machineTransferSelected()
        view.showMachineTransferPanel { destination ->
            val controller = destination as MachineTransferPanelController
            controller.myProjects = myProjects
            controller.venue = selectedProject
            controller.machine = machines
            controller.fromCustomer = selectedCustomer
            controller.delegate = this
        }
    }

    override fun onRetryLoadingMyProjectSelected(view: MyProjectsView) {
        getMyProjects()
    }

    override fun onSelectCustomerSpinner(position: Int) {
        selectedCustomerPosition = position
        selectedProjectPosition = 0
        selectedCustomer = customers[position]
        getMyProjects()
    }

    override fun onSelectedProject(position: Int) {
        selectedProjectPosition = position
        selectedProject = venues[selectedProjectPosition]
        machines = myProjects!!.machines.filter { it.venueCode == selectedProject!!.code }
        view.notifyDataChanged()
    }

    override fun onNotificationSelected() {
        navigateToNotificationPage()
    }


    /*-------------------------- MachineTransferPanelController Delegate -------------------------*/


    override fun machineTransferConfirmed(controller: MachineTransferPanelController, transferMachine: MachineTransferRequestBody, customerId: String, inventTransactionId: String?) {

        view.showConfirmDialog(transferMachine) {
            analytics.confirmOffRentSelected()
            launch {

                try {
                    orderManager.submitMachineTransferRequest(activeCountry.company, transferMachine, customerId)
                    myProjects!!.machines.last { machine -> machine.inventTransactionId == inventTransactionId }.status = MachineTransferStatus.PENDING_TRANSFER_MACHINE
                    view.notifyDataChanged()
                    postEvent(Notification.SEND_TRANSFER_MACHINES_SUCCEEDED)
                    analytics.trackMachineTransferCompleteEvent()
                } catch (error: Exception) {
                    postEvent(Notification.SEND_TRANSFER_MACHINES_FAILED)
                }

            }
        }


    }


    /*---------------------------------- SessionManager Observer ---------------------------------*/


    override fun onCustomersUpdated(sessionManager: SessionManager, customers: List<Customer>?) {
        launch {
            view.notifyCustomersChanged(customers)
        }
    }


    /*------------------------------------- Private methods --------------------------------------*/


    private fun getMyProjects() = launch {

        venues = emptyList()
        isUpdatingMyProjects = true
        view.notifyDataChanged()

        try {
            myProjects = orderManager.getMyProjects(selectedCustomer!!.id)

            myProjects?.machines?.map { it.image = machinesManager.machineImage(it.rentalType) }
            if (!myProjects?.venue?.isNullOrEmpty()!!) {
                selectedProject = myProjects?.venue?.get(selectedProjectPosition)
                venues = myProjects?.venue!!
            }
            machines = myProjects?.machines?.filter { it.venueCode == selectedProject!!.code }!!
            hasFailedLoadingMyProjects = false
        } catch (error: Exception) {
            hasFailedLoadingMyProjects = true
        }

        isUpdatingMyProjects = false
        view.notifyDataChanged()

    }

    private fun navigateToNotificationPage() {
        view.navigateToNotificationPage { destination ->
            val controller = destination as TransferNotificationController
            val toCustomer = AcceptTransferMachineRequestBody.acceptCustomersRequestBody(
                    companyId = selectedCustomer!!.companyId,
                    id = selectedCustomer!!.id,
                    name = selectedCustomer!!.name,
                    contact = AcceptTransferMachineRequestBody.acceptContactRequestBody(name = user!!.name, phone = user!!.phoneNumber, email = user!!.email))
            controller.toCustomer = toCustomer
        }
    }


}