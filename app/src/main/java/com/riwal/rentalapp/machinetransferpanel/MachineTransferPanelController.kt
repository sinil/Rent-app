package com.riwal.rentalapp.machinetransferpanel

import com.riwal.rentalapp.common.extensions.datetime.toLocalTime
import com.riwal.rentalapp.common.extensions.json.toJson
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.model.*
import com.riwal.rentalapp.model.api.MachineTransferRequestBody
import com.riwal.rentalapp.model.api.MachineTransferRequestCustomerBody
import com.riwal.rentalapp.model.api.MachineTransferRequestMachinesBody
import org.joda.time.LocalDate


class MachineTransferPanelController(
        val view: MachineTransferPanelView,
        val activeCountry: Country
) : ViewLifecycleObserver, MachineTransferPanelView.Delegate, MachineTransferPanelView.DataSource {


    /*--------------------------------------- Properties -----------------------------------------*/

    private val validOnRentPeriodCalculator = ValidOnRentPeriodCalculator(nextDayDeliveryCutoffTime = activeCountry.nextDayDeliveryCutoffTime, weekend = activeCountry.weekend)

    var delegate: Delegate? = null
    var myProjects: MyProject? = null
    var machine: MachineBody? = null
    var venue:VenueBody? = null
    var fromCustomer: Customer? = null

    private var transferDate: LocalDate? = null
    private var selectedContactPosition = 0
    private var selectedCustomerPosition = 0

    private var selectedCustomer: customerBody? = null
    private var selectedContact: Contact? = null
    private var canTransferMachine = false

    private val defaultTransferTime
        get() = "08:00".toLocalTime()!!

    private val validTransferDate
        get() = validOnRentPeriodCalculator.earliestValidOnRentDateTime().toLocalDate()

    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this


    }


    /*---------------------------------------- DataSource ----------------------------------------*/


    override fun myProject(view: MachineTransferPanelView) = myProjects

    override fun machineBody(view: MachineTransferPanelView) = machine

    override fun transferMachineDate(view: MachineTransferPanelView) = transferDate ?: validTransferDate

    override fun selectedContactPosition(view: MachineTransferPanelView) = selectedContactPosition

    override fun selectedCustomerPosition(view: MachineTransferPanelView) = selectedCustomerPosition

    override fun canTransferMachine(view: MachineTransferPanelView) = canTransferMachine

    override fun country(view: MachineTransferPanelView) = activeCountry

    override fun validTransferDate(view: MachineTransferPanelView) = validTransferDate


    /*----------------------------------------- Delegate -----------------------------------------*/


    override fun onSelectedCustomer(position: Int) {
        selectedCustomerPosition = position
        selectedCustomer = myProjects!!.customers[position]
    }

    override fun onSelectedContact(contact: Contact) {
        selectedContact = contact
    }

    override fun onSelectedCustomer(customerBody: customerBody) {
        selectedCustomer = customerBody
    }

    override fun onSelectedContact(position: Int) {
        selectedContactPosition = position
        selectedContact = myProjects!!.customers[selectedCustomerPosition].contact[selectedContactPosition]
    }

    override fun onTransferDatePicked(date: LocalDate) {

        if (DayOfWeek.fromValue(date.dayOfWeek) !in activeCountry.weekend) {
            transferDate = date
            canTransferMachine = true
            view.notifyDataChanged()
        } else {
            view.notifyCannotTransferInWeekend()
        }

    }

    override fun onMachineTransferSelected() {

        val transferMachine = MachineTransferRequestBody(
                toCustomer = MachineTransferRequestCustomerBody(id = selectedCustomer!!.id, name = selectedCustomer!!.name,
                        contact = Contact(id = selectedContact!!.id, name = selectedContact!!.name, phoneNumber = selectedContact!!.phoneNumber, email = selectedContact!!.email)),
                fromCustomer = MachineTransferRequestCustomerBody(id = fromCustomer!!.id, name = fromCustomer!!.name),
                machine = MachineTransferRequestMachinesBody(
                        rentalType = machine!!.rentalType,
                        fleetNumber = machine!!.fleetNumber,
                        onHireDate = machine!!.onRentDateTime.toString(),
                        transactionId = machine!!.inventTransactionId!!),
                projectName = venue!!.name,
                countryCode = activeCountry,
                transferDate = "${transferDate ?: validTransferDate}")
        println("sinilkumar:: ${transferMachine.toJson()}")
        view.navigateBack()
        delegate?.machineTransferConfirmed(controller = this, transferMachine = transferMachine, customerId = selectedCustomer!!.id, inventTransactionId = machine!!.inventTransactionId)
    }


    /*---------------------------------------- Interfaces ----------------------------------------*/


    interface Delegate {
        fun machineTransferConfirmed(controller: MachineTransferPanelController, transferMachine: MachineTransferRequestBody, customerId: String, inventTransactionId: String?) {}
    }

}