package com.riwal.rentalapp.addaccessories

import com.riwal.rentalapp.RentalAnalytics
import com.riwal.rentalapp.common.ui.ViewLifecycleObserver
import com.riwal.rentalapp.common.ui.observe
import com.riwal.rentalapp.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AddAccessoriesController(val view: AddAccessoriesView,
                               val country: Country,
                               val orderManager: OrderManager,
                               val analytics: RentalAnalytics
) : ViewLifecycleObserver, AddAccessoriesView.DataSource, AddAccessoriesView.Delegate, CoroutineScope by MainScope() {


    /*---------------------------------------- Properties ----------------------------------------*/


    var delegate: Delegate? = null
    lateinit var machineOrder: MachineOrder
    private var isLoading = true
    private var selectedAccessories = mutableListOf<AccessoryOrder>()
    private var accessoriesOrder = mutableListOf<AccessoryOrder>()


    /*---------------------------------------- Lifecycle -----------------------------------------*/


    init {
        view.dataSource = this
        view.delegate = this

        observe(view)
    }

    override fun onViewCreate() {
        super.onViewCreate()
        getAccessories(machine = machineOrder.machine)
    }

    override fun onViewAppear() {
        super.onViewAppear()
//        analytics.userLookingAtCart()
    }


    /*-------------------------------- Accessories View Delegate --------------------------------*/


    override fun onBackButtonClicked() {
        view.navigateBack()
    }

    override fun onAccessoryQuantitySelected(accessory: Accessory, quantity: Int) {

        val accessoryOrder = AccessoryOrder(accessory, quantity)

        val isAccessoryAdded = machineOrder.accessories.find { it.accessory.name == accessory.name } != null

        if (isAccessoryAdded) {
            if (quantity == 0)
                orderManager.deleteAccessory(machineOrder, machineOrder.accessories.find { it.accessory.name == accessory.name }!!)
            else
                machineOrder.accessories.filter { it.accessory.name == accessory.name }.forEach { it.quantity = quantity }
        } else {
            if (quantity > 0)
                accessoriesOrder.add(accessoryOrder)
        }
    }


    override fun onConfirmClicked() {
        orderManager.addAccessory(machineOrder, accessoriesOrder)
        accessoriesOrder.clear()
        view.notifyDataChanged()
        delegate?.onAccessoriesAdded(controller = this)
        view.navigateBack()
    }

    /*-------------------------------- Accessories View DataSource --------------------------------*/


    override fun isLoading(view: AddAccessoriesView): Boolean = isLoading

    override fun accessories(view: AddAccessoriesView): List<AccessoryOrder> = selectedAccessories


    /*------------------------------------- Private methods --------------------------------------*/


    private fun getAccessories(machine: Machine) = launch {
        selectedAccessories = machineOrder.accessories
        val accessories = orderManager.getMachineAccessories(machine, country)

        for (accessory in accessories) {
            val accessoryOrder = AccessoryOrder(accessory, 0)

            val isAccessoryAdded = selectedAccessories.find { it.accessory.name == accessory.name } != null
            if (!isAccessoryAdded) {
                selectedAccessories.add(accessoryOrder)
            }
        }

        isLoading = false
        view.notifyDataChanged()
    }


    /*--------------------------------------- Interfaces -----------------------------------------*/


    interface Delegate {
        fun onAccessoriesAdded(controller: AddAccessoriesController)
    }
}





