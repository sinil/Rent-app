package com.riwal.rentalapp.machinetransferpanel

import com.riwal.rentalapp.common.mvc.MvcView
import com.riwal.rentalapp.common.ui.ObservableLifecycleView
import com.riwal.rentalapp.model.*
import org.joda.time.LocalDate


interface MachineTransferPanelView : MvcView, ObservableLifecycleView {


    var dataSource: DataSource
    var delegate: Delegate

    fun navigateBack()
    fun notifyCannotTransferInWeekend()

    interface DataSource {

        fun myProject(view: MachineTransferPanelView): MyProject?
        fun machineBody(view: MachineTransferPanelView): MachineBody?
        fun transferMachineDate(view: MachineTransferPanelView): LocalDate?
        fun selectedCustomerPosition(view: MachineTransferPanelView): Int
        fun selectedContactPosition(view: MachineTransferPanelView): Int
        fun canTransferMachine(view: MachineTransferPanelView): Boolean
        fun country(view: MachineTransferPanelView): Country
        fun validTransferDate(view: MachineTransferPanelView): LocalDate

    }

    interface Delegate {
        fun onTransferDatePicked(date: LocalDate)
        fun onMachineTransferSelected()
        fun onSelectedCustomer(position: Int)
        fun onSelectedContact(position: Int)
        fun onSelectedCustomer(customerBody: customerBody)
        fun onSelectedContact(contact: Contact)
    }
}