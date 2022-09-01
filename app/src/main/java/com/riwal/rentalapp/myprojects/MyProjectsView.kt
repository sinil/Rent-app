package com.riwal.rentalapp.myprojects

import com.riwal.rentalapp.ControllerPreparationHandler
import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.Customer
import com.riwal.rentalapp.model.MachineBody
import com.riwal.rentalapp.model.MyProject
import com.riwal.rentalapp.model.VenueBody
import com.riwal.rentalapp.model.api.MachineTransferRequestBody

interface MyProjectsView : MvcView, ObservableLifecycleView {

    var dataSource: DataSource
    var delegate: Delegate


    fun navigateBack()
    fun notifyCustomersChanged(customers: List<Customer>?)
    fun showMachineTransferPanel(preparationHandler: ControllerPreparationHandler)
    fun navigateToNotificationPage(preparationHandler: ControllerPreparationHandler)
    fun showConfirmDialog(transferMachine: MachineTransferRequestBody, callback: () -> Unit)

    interface DataSource {
        fun myProjects(view: MyProjectsView): MyProject?
        fun venue(view: MyProjectsView): List<VenueBody>
        fun machines(view: MyProjectsView): List<MachineBody>
        fun customers(view: MyProjectsView): List<Customer>
        fun isChatEnable(view: MyProjectsView): Boolean
        fun isPhoneCallEnable(view: MyProjectsView): Boolean
        fun isTransferMachineEnable(view: MyProjectsView): Boolean
        fun selectedCustomer(view: MyProjectsView): Customer?
        fun selectedCustomerPosition(view: MyProjectsView): Int
        fun selectedProjectPosition(view: MyProjectsView): Int
        fun isUpdatingMyProjects(view: MyProjectsView): Boolean
        fun hasFailedLoadingMyProjects(view: MyProjectsView): Boolean


    }

    interface Delegate {
        fun onMachineTransferSelected(machines: MachineBody)
        fun onRetryLoadingMyProjectSelected(view: MyProjectsView)
        fun onSelectCustomerSpinner(position: Int)
        fun onSelectedProject(position: Int)
        fun onNotificationSelected()
    }

}